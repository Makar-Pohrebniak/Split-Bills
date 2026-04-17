package com.example.SplitBills.service.impl;

import com.example.SplitBills.enums.PaymentStatus;
import com.example.SplitBills.exception.GroupNotFoundException;
import com.example.SplitBills.exception.PaymentNotFoundException;
import com.example.SplitBills.exception.InvalidPaymentOperationException;
import com.example.SplitBills.exception.UnauthorizedAccessException;
import com.example.SplitBills.model.dto.response.PaymentResponseDto;
import com.example.SplitBills.model.dto.response.PersonalBalanceResponseDto;
import com.example.SplitBills.model.entity.PaymentEntity;
import com.example.SplitBills.model.entity.GroupEntity;
import com.example.SplitBills.model.entity.UserEntity;
import com.example.SplitBills.repository.PaymentRepository;
import com.example.SplitBills.repository.GroupRepository;
import com.example.SplitBills.service.api.PaymentService;
import com.example.SplitBills.service.api.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceDefault implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final GroupRepository groupRepository;
    private final ExpenseService expenseService;

    @Override
    @Transactional
    public PaymentResponseDto createPayment(UUID senderSubId, Long groupId, String comment) {
        PersonalBalanceResponseDto balanceDto = expenseService.getUserBalanceInGroup(groupId, senderSubId);

        if (balanceDto.netBalance().compareTo(BigDecimal.ZERO) >= 0) {
            throw new InvalidPaymentOperationException("No debt found for user: " + senderSubId);
        }

        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        UserEntity sender = group.getMembers().stream()
                .filter(m -> m.getSubId().equals(senderSubId))
                .findFirst()
                .orElseThrow(() -> new UnauthorizedAccessException("User is not a member of the group"));

        UUID adminSubId = group.getOwner();

        if (senderSubId.equals(adminSubId)) {
            throw new InvalidPaymentOperationException("Self-payment is not allowed");
        }

        UserEntity admin = group.getMembers().stream()
                .filter(m -> m.getSubId().equals(adminSubId))
                .findFirst()
                .orElseThrow(() -> new UnauthorizedAccessException("Group owner is not in the members list"));

        PaymentEntity payment = PaymentEntity.builder()
                .senderId(sender.getId())
                .receiverId(admin.getId())
                .groupId(groupId)
                .amount(balanceDto.netBalance().abs())
                .status(PaymentStatus.PENDING)
                .comment(comment)
                .createdAt(LocalDateTime.now())
                .build();

        return mapToResponseDto(paymentRepository.save(payment));
    }

    @Override
    @Transactional
    public PaymentResponseDto approvePayment(Long paymentId, UUID adminSubId) {
        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        GroupEntity group = groupRepository.findById(payment.getGroupId())
                .orElseThrow(() -> new GroupNotFoundException(payment.getGroupId()));

        if (!group.getOwner().equals(adminSubId)) {
            throw new UnauthorizedAccessException("Only group owner can approve payments");
        }

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new InvalidPaymentOperationException("Payment is already " + payment.getStatus());
        }

        payment.setStatus(PaymentStatus.CONFIRMED);
        payment.setResolvedAt(LocalDateTime.now());

        return mapToResponseDto(paymentRepository.save(payment));
    }

    @Override
    @Transactional
    public PaymentResponseDto declinePayment(Long paymentId, UUID adminSubId) {
        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        GroupEntity group = groupRepository.findById(payment.getGroupId())
                .orElseThrow(() -> new GroupNotFoundException(payment.getGroupId()));

        if (!group.getOwner().equals(adminSubId)) {
            throw new UnauthorizedAccessException("Only group owner can decline payments");
        }

        payment.setStatus(PaymentStatus.REJECTED);
        payment.setResolvedAt(LocalDateTime.now());

        return mapToResponseDto(paymentRepository.save(payment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getGroupPayments(Long groupId) {
        return paymentRepository.findAllByGroupId(groupId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getConfirmedGroupPayments(Long groupId) {
        return paymentRepository.findAllConfirmedByGroupId(groupId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getUserPaymentsInGroup(Long groupId, UUID userSubId) {
        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        UserEntity user = group.getMembers().stream()
                .filter(m -> m.getSubId().equals(userSubId))
                .findFirst()
                .orElseThrow(() -> new UnauthorizedAccessException("User not in group"));

        return paymentRepository.findAllUserPaymentsInGroup(user.getId(), groupId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private PaymentResponseDto mapToResponseDto(PaymentEntity payment) {
        return PaymentResponseDto.builder()
                .id(payment.getId())
                .senderId(payment.getSenderId())
                .receiverId(payment.getReceiverId())
                .groupId(payment.getGroupId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .comment(payment.getComment())
                .createdAt(payment.getCreatedAt())
                .resolvedAt(payment.getResolvedAt())
                .build();
    }
}