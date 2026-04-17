package com.example.SplitBills.service;

import com.example.SplitBills.enums.PaymentStatus;
import com.example.SplitBills.exception.GroupNotFoundException;
import com.example.SplitBills.exception.InvalidPaymentOperationException;
import com.example.SplitBills.exception.PaymentNotFoundException;
import com.example.SplitBills.exception.UnauthorizedAccessException;
import com.example.SplitBills.model.dto.response.PaymentResponseDto;
import com.example.SplitBills.model.dto.response.PersonalBalanceResponseDto;
import com.example.SplitBills.model.entity.*;
import com.example.SplitBills.repository.GroupRepository;
import com.example.SplitBills.repository.PaymentRepository;
import com.example.SplitBills.service.api.ExpenseService;
import com.example.SplitBills.service.impl.PaymentServiceDefault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceDefaultTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private ExpenseService expenseService;

    @InjectMocks
    private PaymentServiceDefault paymentService;

    private UUID senderSubId;
    private UUID adminSubId;
    private UserEntity sender;
    private GroupEntity group;

    @BeforeEach
    void setUp() {
        senderSubId = UUID.randomUUID();
        adminSubId = UUID.randomUUID();

        sender = new UserEntity();
        sender.setId(1L);
        sender.setSubId(senderSubId);

        UserEntity admin = new UserEntity();
        admin.setId(2L);
        admin.setSubId(adminSubId);

        group = new GroupEntity();
        group.setId(10L);
        group.setOwner(adminSubId);
        group.setMembers(new HashSet<>(Set.of(sender, admin)));
    }

    @Test
    void createPayment_Success() {
        PersonalBalanceResponseDto balance = new PersonalBalanceResponseDto(
                BigDecimal.ZERO, BigDecimal.valueOf(100), new BigDecimal("-100"));

        when(expenseService.getUserBalanceInGroup(10L, senderSubId)).thenReturn(balance);
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));
        when(paymentRepository.save(any(PaymentEntity.class))).thenAnswer(i -> i.getArgument(0));

        PaymentResponseDto result = paymentService.createPayment(senderSubId, 10L, "Fix");

        assertNotNull(result);
        verify(paymentRepository).save(any(PaymentEntity.class));
    }

    @Test
    void createPayment_ThrowsException_WhenNoDebt() {
        PersonalBalanceResponseDto balance = new PersonalBalanceResponseDto(
                BigDecimal.valueOf(100), BigDecimal.valueOf(50), BigDecimal.valueOf(50));
        when(expenseService.getUserBalanceInGroup(10L, senderSubId)).thenReturn(balance);

        assertThrows(InvalidPaymentOperationException.class, () ->
                paymentService.createPayment(senderSubId, 10L, "No debt"));
    }

    @Test
    void createPayment_ThrowsException_WhenGroupNotFound() {
        PersonalBalanceResponseDto balance = new PersonalBalanceResponseDto(
                BigDecimal.ZERO, BigDecimal.valueOf(100), new BigDecimal("-100"));
        when(expenseService.getUserBalanceInGroup(99L, senderSubId)).thenReturn(balance);
        when(groupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () ->
                paymentService.createPayment(senderSubId, 99L, "Fail"));
    }

    @Test
    void createPayment_ThrowsException_WhenUserNotMember() {
        PersonalBalanceResponseDto balance = new PersonalBalanceResponseDto(
                BigDecimal.ZERO, BigDecimal.valueOf(100), new BigDecimal("-100"));
        when(expenseService.getUserBalanceInGroup(10L, senderSubId)).thenReturn(balance);

        GroupEntity emptyGroup = new GroupEntity();
        emptyGroup.setId(10L);
        emptyGroup.setMembers(new HashSet<>());
        when(groupRepository.findById(10L)).thenReturn(Optional.of(emptyGroup));

        assertThrows(UnauthorizedAccessException.class, () ->
                paymentService.createPayment(senderSubId, 10L, "Member fail"));
    }

    @Test
    void createPayment_ThrowsException_WhenSelfPayment() {
        PersonalBalanceResponseDto balance = new PersonalBalanceResponseDto(
                BigDecimal.ZERO, BigDecimal.valueOf(100), new BigDecimal("-100"));
        when(expenseService.getUserBalanceInGroup(10L, adminSubId)).thenReturn(balance);
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));

        assertThrows(InvalidPaymentOperationException.class, () ->
                paymentService.createPayment(adminSubId, 10L, "Self"));
    }

    @Test
    void createPayment_ThrowsException_WhenAdminNotInMembers() {
        PersonalBalanceResponseDto balance = new PersonalBalanceResponseDto(
                BigDecimal.ZERO, BigDecimal.valueOf(100), new BigDecimal("-100"));
        when(expenseService.getUserBalanceInGroup(10L, senderSubId)).thenReturn(balance);

        GroupEntity brokenGroup = new GroupEntity();
        brokenGroup.setId(10L);
        brokenGroup.setOwner(adminSubId);
        brokenGroup.setMembers(new HashSet<>(Set.of(sender)));
        when(groupRepository.findById(10L)).thenReturn(Optional.of(brokenGroup));

        assertThrows(UnauthorizedAccessException.class, () ->
                paymentService.createPayment(senderSubId, 10L, "Admin missing"));
    }

    @Test
    void approvePayment_Success() {
        PaymentEntity payment = PaymentEntity.builder().id(1L).groupId(10L).status(PaymentStatus.PENDING).build();
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));
        when(paymentRepository.save(any(PaymentEntity.class))).thenAnswer(i -> i.getArgument(0));

        PaymentResponseDto result = paymentService.approvePayment(1L, adminSubId);

        assertEquals(PaymentStatus.CONFIRMED, result.getStatus());
        verify(paymentRepository).save(payment);
    }

    @Test
    void approvePayment_ThrowsException_WhenPaymentNotFound() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(PaymentNotFoundException.class, () -> paymentService.approvePayment(1L, adminSubId));
    }

    @Test
    void approvePayment_ThrowsException_WhenGroupNotFound() {
        PaymentEntity payment = PaymentEntity.builder().id(1L).groupId(10L).build();
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(groupRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> paymentService.approvePayment(1L, adminSubId));
    }

    @Test
    void approvePayment_ThrowsException_WhenNotOwner() {
        PaymentEntity payment = PaymentEntity.builder().id(1L).groupId(10L).build();
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));

        assertThrows(UnauthorizedAccessException.class, () -> paymentService.approvePayment(1L, senderSubId));
    }

    @Test
    void approvePayment_ThrowsException_WhenAlreadyProcessed() {
        PaymentEntity payment = PaymentEntity.builder().id(1L).groupId(10L).status(PaymentStatus.CONFIRMED).build();
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));

        assertThrows(InvalidPaymentOperationException.class, () -> paymentService.approvePayment(1L, adminSubId));
    }

    @Test
    void declinePayment_Success() {
        PaymentEntity payment = PaymentEntity.builder().id(1L).groupId(10L).status(PaymentStatus.PENDING).build();
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));
        when(paymentRepository.save(any(PaymentEntity.class))).thenAnswer(i -> i.getArgument(0));

        PaymentResponseDto result = paymentService.declinePayment(1L, adminSubId);

        assertEquals(PaymentStatus.REJECTED, result.getStatus());
        verify(paymentRepository).save(payment);
    }

    @Test
    void declinePayment_ThrowsException_WhenPaymentNotFound() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(PaymentNotFoundException.class, () -> paymentService.declinePayment(1L, adminSubId));
    }

    @Test
    void declinePayment_ThrowsException_WhenGroupNotFound() {
        PaymentEntity payment = PaymentEntity.builder().id(1L).groupId(10L).build();
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(groupRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> paymentService.declinePayment(1L, adminSubId));
    }

    @Test
    void declinePayment_ThrowsException_WhenNotOwner() {
        PaymentEntity payment = PaymentEntity.builder().id(1L).groupId(10L).build();
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));

        assertThrows(UnauthorizedAccessException.class, () -> paymentService.declinePayment(1L, senderSubId));
    }

    @Test
    void getGroupPayments_Success() {
        when(paymentRepository.findAllByGroupId(10L)).thenReturn(List.of());
        paymentService.getGroupPayments(10L);
        verify(paymentRepository).findAllByGroupId(10L);
    }

    @Test
    void getConfirmedGroupPayments_Success() {
        PaymentEntity confirmedPayment = PaymentEntity.builder()
                .id(1L)
                .groupId(10L)
                .status(PaymentStatus.CONFIRMED)
                .amount(BigDecimal.TEN)
                .build();

        when(paymentRepository.findAllConfirmedByGroupId(10L)).thenReturn(List.of(confirmedPayment));

        List<PaymentResponseDto> result = paymentService.getConfirmedGroupPayments(10L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(PaymentStatus.CONFIRMED, result.get(0).getStatus());
        verify(paymentRepository).findAllConfirmedByGroupId(10L);
    }

    @Test
    void getUserPaymentsInGroup_Success() {
        PaymentEntity userPayment = PaymentEntity.builder()
                .id(1L)
                .senderId(1L)
                .receiverId(2L)
                .groupId(10L)
                .amount(BigDecimal.TEN)
                .build();

        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));
        when(paymentRepository.findAllUserPaymentsInGroup(1L, 10L)).thenReturn(List.of(userPayment));

        List<PaymentResponseDto> result = paymentService.getUserPaymentsInGroup(10L, senderSubId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getSenderId());
        verify(groupRepository).findById(10L);
        verify(paymentRepository).findAllUserPaymentsInGroup(1L, 10L);
    }

    @Test
    void getUserPaymentsInGroup_ThrowsException_WhenUserNotInGroup() {
        UUID strangerSubId = UUID.randomUUID();
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));

        assertThrows(UnauthorizedAccessException.class, () ->
                paymentService.getUserPaymentsInGroup(10L, strangerSubId));

        verify(paymentRepository, never()).findAllUserPaymentsInGroup(anyLong(), anyLong());
    }
}