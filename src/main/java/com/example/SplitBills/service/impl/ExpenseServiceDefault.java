package com.example.SplitBills.service.impl;

import com.example.SplitBills.enums.FinancialType;
import com.example.SplitBills.exception.GroupNotFoundException;
import com.example.SplitBills.model.dto.FinancialEventDto;
import com.example.SplitBills.model.dto.request.AddExpenseDto;
import com.example.SplitBills.model.dto.request.UpdateExpenseDto;
import com.example.SplitBills.model.dto.response.ExpenseResponseDto;
import com.example.SplitBills.model.dto.response.PersonalBalanceResponseDto;
import com.example.SplitBills.model.entity.*;
import com.example.SplitBills.repository.ExpenseRepository;
import com.example.SplitBills.repository.GroupRepository;
import com.example.SplitBills.repository.PaymentRepository;
import com.example.SplitBills.service.KafkaProducerService;
import com.example.SplitBills.service.api.ExpenseService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseServiceDefault implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final GroupRepository groupRepository;
    private final KafkaProducerService kafkaProducerService;
    private final PaymentRepository paymentRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void addExpense(Long groupId, AddExpenseDto expenseDto, UUID subId) {
        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        UserEntity payer = group.getMembers().stream()
                .filter(m -> m.getSubId().equals(subId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Access denied: you are not a member of this group"));

        ExpenseEntity expense = new ExpenseEntity();
        expense.setAmount(expenseDto.amount());
        expense.setDescription(expenseDto.description());
        expense.setGroup(group);
        expense.setPayer(payer);
        expense.setCreatedAt(LocalDateTime.now());
        expense.setShares(new ArrayList<>());

        calculateEqualShares(expense, group.getMembers(), expenseDto.amount());

        expenseRepository.save(expense);

        kafkaProducerService.sendFinancialEvent(new FinancialEventDto(
                groupId,
                payer.getId(),
                null,
                expenseDto.amount(),
                FinancialType.EXPENSE_CREATED,
                "New expense: " + expenseDto.description()
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseResponseDto getExpenseById(Long expenseId, UUID subId) {
        ExpenseEntity expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        validateMember(expense.getGroup(), subId);

        return mapToResponseDto(expense);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponseDto> getExpensesByGroupId(Long groupId, UUID subId) {
        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        validateMember(group, subId);

        return expenseRepository.findByGroupId(groupId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateExpense(Long expenseId, UpdateExpenseDto updateDto, UUID subId) {
        ExpenseEntity expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getPayer().getSubId().equals(subId)) {
            throw new RuntimeException("Only the payer can update this expense");
        }

        expense.setDescription(updateDto.getDescription());
        expense.setAmount(updateDto.getAmount());
        expense.getShares().clear();

        calculateEqualShares(expense, expense.getGroup().getMembers(), updateDto.getAmount());

        expenseRepository.save(expense);

        kafkaProducerService.sendFinancialEvent(new FinancialEventDto(
                expense.getGroup().getId(),
                expense.getPayer().getId(),
                null,
                updateDto.getAmount(),
                FinancialType.EXPENSE_CREATED,
                "Updated expense: " + updateDto.getDescription()
        ));
    }

    @Override
    @Transactional
    public void deleteExpense(Long expenseId, UUID subId) {
        ExpenseEntity expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getPayer().getSubId().equals(subId)) {
            throw new RuntimeException("Only the payer can delete this expense");
        }

        Long groupId = expense.getGroup().getId();
        Long payerId = expense.getPayer().getId();
        BigDecimal amount = expense.getAmount();

        expenseRepository.delete(expense);

        kafkaProducerService.sendFinancialEvent(new FinancialEventDto(
                groupId,
                payerId,
                null,
                amount,
                FinancialType.EXPENSE_CREATED,
                "Deleted expense"
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public PersonalBalanceResponseDto getUserBalanceInGroup(Long groupId, UUID subId) {
        entityManager.clear();

        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        UserEntity currentUser = group.getMembers().stream()
                .filter(m -> m.getSubId().equals(subId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found in group"));

        List<ExpenseEntity> groupExpenses = expenseRepository.findByGroupId(groupId);

        BigDecimal totalPaidByMe = groupExpenses.stream()
                .filter(e -> e.getPayer().getSubId().equals(subId))
                .map(ExpenseEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalMyShares = groupExpenses.stream()
                .flatMap(e -> e.getShares().stream())
                .filter(s -> s.getUser().getSubId().equals(subId))
                .map(ExpenseShare::getShareAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<PaymentEntity> userPayments = paymentRepository.findAllUserPaymentsInGroup(currentUser.getId(), groupId);

        BigDecimal totalPaymentsSent = userPayments.stream()
                .filter(p -> p.getSenderId().equals(currentUser.getId()))
                .filter(p -> "CONFIRMED".equals(p.getStatus().name()))
                .map(PaymentEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPaymentsReceived = userPayments.stream()
                .filter(p -> p.getReceiverId().equals(currentUser.getId()))
                .filter(p -> "CONFIRMED".equals(p.getStatus().name()))
                .map(PaymentEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netBalance = totalPaidByMe
                .add(totalPaymentsSent)
                .subtract(totalMyShares)
                .subtract(totalPaymentsReceived);

        return new PersonalBalanceResponseDto(
                totalPaidByMe,
                totalMyShares,
                netBalance
        );
    }

    @Override
    @Transactional
    public void recalculateGroupExpenses(Long groupId) {
        entityManager.clear();

        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        List<ExpenseEntity> expenses = expenseRepository.findByGroupId(groupId);
        Set<UserEntity> members = group.getMembers();

        for (ExpenseEntity expense : expenses) {
            expense.getShares().clear();
            entityManager.flush();

            calculateEqualShares(expense, members, expense.getAmount());
        }

        expenseRepository.saveAll(expenses);
    }

    private void calculateEqualShares(ExpenseEntity expense, Set<UserEntity> members, BigDecimal totalAmount) {
        int membersCount = members.size();
        if (membersCount == 0) return;

        BigDecimal shareAmount = totalAmount.divide(BigDecimal.valueOf(membersCount), 2, RoundingMode.HALF_UP);
        BigDecimal remainder = totalAmount.subtract(shareAmount.multiply(BigDecimal.valueOf(membersCount)));

        boolean first = true;
        for (UserEntity member : members) {
            ExpenseShare share = new ExpenseShare();
            share.setExpense(expense);
            share.setUser(member);
            if (first) {
                share.setShareAmount(shareAmount.add(remainder));
                first = false;
            } else {
                share.setShareAmount(shareAmount);
            }
            expense.getShares().add(share);
        }
    }

    private void validateMember(GroupEntity group, UUID subId) {
        boolean isMember = group.getMembers().stream()
                .anyMatch(m -> m.getSubId().equals(subId));
        if (!isMember) {
            throw new RuntimeException("Access denied: you are not a member of this group");
        }
    }

    private ExpenseResponseDto mapToResponseDto(ExpenseEntity expense) {
        return ExpenseResponseDto.builder()
                .id(expense.getId())
                .description(expense.getDescription())
                .amount(expense.getAmount())
                .paidBy(expense.getPayer().getSubId())
                .createdAt(expense.getCreatedAt())
                .build();
    }
}