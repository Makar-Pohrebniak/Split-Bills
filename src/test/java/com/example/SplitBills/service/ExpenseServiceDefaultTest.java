package com.example.SplitBills.service;

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
import com.example.SplitBills.service.impl.ExpenseServiceDefault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

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
    private UserEntity payer;
    private UserEntity member2;
    private GroupEntity group;
    private AddExpenseDto expenseDto;

    @BeforeEach
    void setUp() {
        payerSubId = UUID.randomUUID();

        payer = new UserEntity();
        payer.setId(1L);
        payer.setSubId(payerSubId);

        member2 = new UserEntity();
        member2.setId(2L);
        member2.setSubId(UUID.randomUUID());

        group = new GroupEntity();
        group.setId(10L);
        group.setMembers(new HashSet<>(Set.of(payer, member2)));

        expenseDto = new AddExpenseDto(new BigDecimal("100.01"), "Test", null, null);
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
                .map(ExpenseShare::getShareAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertEquals(0, totalShares.compareTo(new BigDecimal("100.01")));
    }

    @Test
    void addExpense_UnequalSplit_Success() {
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));

        List<ExpenseShareDto> customShares = List.of(
                new ExpenseShareDto(payerSubId, new BigDecimal("70.00")),
                new ExpenseShareDto(member2.getSubId(), new BigDecimal("30.01"))
        );
        AddExpenseDto unequalDto = new AddExpenseDto(new BigDecimal("100.01"), "Unequal", null, customShares);

        expenseService.addExpense(10L, unequalDto, payerSubId);

        ArgumentCaptor<ExpenseEntity> captor = ArgumentCaptor.forClass(ExpenseEntity.class);
        verify(expenseRepository).save(captor.capture());

        ExpenseEntity saved = captor.getValue();
        assertEquals(0, saved.getShares().get(0).getShareAmount().compareTo(new BigDecimal("70.00")));
        assertEquals(0, saved.getShares().get(1).getShareAmount().compareTo(new BigDecimal("30.01")));
    }

    @Test
    void addExpense_ThrowsException_WhenUserNotMember() {
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));

        assertThrows(RuntimeException.class, () ->
                expenseService.addExpense(10L, expenseDto, UUID.randomUUID())
        );
    }

    @Test
    void getExpenseById_Success() {
        ExpenseEntity expense = new ExpenseEntity();
        expense.setId(1L);
        expense.setGroup(group);
        expense.setAmount(new BigDecimal("100.00"));
        expense.setPayer(payer);

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        ExpenseResponseDto result = expenseService.getExpenseById(1L, payerSubId);

        assertNotNull(result);
        assertEquals(new BigDecimal("100.00"), result.getAmount());
    }

    @Test
    void updateExpense_Success() {
        ExpenseEntity expense = new ExpenseEntity();
        expense.setId(1L);
        expense.setGroup(group);
        expense.setPayer(payer);
        expense.setShares(new ArrayList<>());

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        UpdateExpenseDto updateDto = new UpdateExpenseDto(new BigDecimal("200.00"), "Updated");
        expenseService.updateExpense(1L, updateDto, payerSubId);

        verify(expenseRepository).save(any(ExpenseEntity.class));
        assertEquals(new BigDecimal("200.00"), expense.getAmount());
    }

    @Test
    void deleteExpense_Success() {
        ExpenseEntity expense = new ExpenseEntity();
        expense.setId(1L);
        expense.setPayer(payer);

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        expenseService.deleteExpense(1L, payerSubId);

        verify(expenseRepository).delete(expense);
    }
}