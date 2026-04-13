package com.example.SplitBills.service;

import com.example.SplitBills.model.dto.request.AddExpenseDto;
import com.example.SplitBills.model.entity.ExpenseEntity;
import com.example.SplitBills.model.entity.GroupEntity;
import com.example.SplitBills.model.entity.UserEntity;
import com.example.SplitBills.repository.ExpenseRepository;
import com.example.SplitBills.repository.GroupRepository;
import com.example.SplitBills.service.impl.ExpenseServiceDefault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceDefaultTest {

    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private ExpenseServiceDefault expenseService;

    private UUID payerSubId;
    private GroupEntity group;
    private AddExpenseDto expenseDto;

    @BeforeEach
    void setUp() {
        payerSubId = UUID.randomUUID();

        UserEntity payer = new UserEntity();
        payer.setId(1L);
        payer.setSubId(payerSubId);

        UserEntity member2 = new UserEntity();
        member2.setId(2L);
        member2.setSubId(UUID.randomUUID());

        group = new GroupEntity();
        group.setId(10L);
        group.setMembers(new HashSet<>(Set.of(payer, member2)));

        expenseDto = new AddExpenseDto(new BigDecimal("100.01"), "Test");
    }

    @Test
    void addExpense_Success() {
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));

        expenseService.addExpense(10L, expenseDto, payerSubId);

        ArgumentCaptor<ExpenseEntity> captor = ArgumentCaptor.forClass(ExpenseEntity.class);
        verify(expenseRepository).save(captor.capture());

        ExpenseEntity saved = captor.getValue();
        assertEquals(new BigDecimal("100.01"), saved.getAmount());
        assertEquals(2, saved.getShares().size());

        BigDecimal totalShares = saved.getShares().stream()
                .map(s -> s.getShareAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertEquals(0, totalShares.compareTo(new BigDecimal("100.01")));
    }

    @Test
    void addExpense_ThrowsException_WhenUserNotMember() {
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));

        assertThrows(RuntimeException.class, () ->
                expenseService.addExpense(10L, expenseDto, UUID.randomUUID())
        );
    }
}