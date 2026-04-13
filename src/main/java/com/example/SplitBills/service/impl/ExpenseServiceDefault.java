package com.example.SplitBills.service.impl;

import com.example.SplitBills.model.dto.request.AddExpenseDto;
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
import java.util.Set;
import java.util.UUID;

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

        Set<UserEntity> members = group.getMembers();
        int membersCount = members.size();

        BigDecimal amount = expenseDto.amount();
        BigDecimal shareAmount = amount.divide(BigDecimal.valueOf(membersCount), 2, RoundingMode.HALF_UP);
        BigDecimal remainder = amount.subtract(shareAmount.multiply(BigDecimal.valueOf(membersCount)));

        ExpenseEntity expenseEntity = new ExpenseEntity();
        expenseEntity.setAmount(amount);
        expenseEntity.setDescription(expenseDto.description());
        expenseEntity.setGroup(group);
        expenseEntity.setPayer(payer);
        expenseEntity.setCreatedAt(LocalDateTime.now());
        expenseEntity.setShares(new ArrayList<>());

        boolean first = true;
        for (UserEntity member : members) {
            ExpenseShare share = new ExpenseShare();
            share.setExpense(expenseEntity);
            share.setUser(member);

            if (first) {
                share.setShareAmount(shareAmount.add(remainder));
                first = false;
            } else {
                share.setShareAmount(shareAmount);
            }
            expenseEntity.getShares().add(share);
        }

        expenseRepository.save(expenseEntity);
    }
}