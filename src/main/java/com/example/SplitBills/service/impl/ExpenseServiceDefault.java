package com.example.SplitBills.service.impl;

import com.example.SplitBills.model.dto.request.AddExpenseDto;
import com.example.SplitBills.model.dto.request.ExpenseShareDto;
import com.example.SplitBills.model.dto.request.UpdateExpenseDto;
import com.example.SplitBills.model.dto.response.ExpenseResponseDto;
import com.example.SplitBills.model.entity.ExpenseEntity;
import com.example.SplitBills.model.entity.ExpenseShare;
import com.example.SplitBills.model.entity.GroupEntity;
import com.example.SplitBills.model.entity.UserEntity;
import com.example.SplitBills.repository.ExpenseRepository;
import com.example.SplitBills.repository.GroupRepository;
import com.example.SplitBills.service.api.ExpenseService;
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

    @Override
    @Transactional
    public void addExpense(Long groupId, AddExpenseDto expenseDto, UUID subId) {
        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

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

        if (expenseDto.shares() == null || expenseDto.shares().isEmpty()) {
            calculateEqualShares(expense, group.getMembers(), expenseDto.amount());
        } else {
            validateAndCreateCustomShares(expense, expenseDto.shares(), group);
        }

        expenseRepository.save(expense);
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
                .orElseThrow(() -> new RuntimeException("Group not found"));

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
    }

    @Override
    @Transactional
    public void deleteExpense(Long expenseId, UUID subId) {
        ExpenseEntity expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getPayer().getSubId().equals(subId)) {
            throw new RuntimeException("Only the payer can delete this expense");
        }

        expenseRepository.delete(expense);
    }

    private void calculateEqualShares(ExpenseEntity expense, Set<UserEntity> members, BigDecimal totalAmount) {
        int membersCount = members.size();
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

    private void validateAndCreateCustomShares(ExpenseEntity expense, List<ExpenseShareDto> customShares, GroupEntity group) {
        BigDecimal sum = customShares.stream()
                .map(ExpenseShareDto::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (sum.compareTo(expense.getAmount()) != 0) {
            throw new RuntimeException("The sum of shares does not equal the total amount");
        }

        for (ExpenseShareDto dto : customShares) {
            UserEntity user = group.getMembers().stream()
                    .filter(m -> m.getSubId().equals(dto.userId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("User not in group: " + dto.userId()));

            ExpenseShare share = new ExpenseShare();
            share.setExpense(expense);
            share.setUser(user);
            share.setShareAmount(dto.amount());
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