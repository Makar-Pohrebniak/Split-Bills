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
                .map(ExpenseShare::getShareAmount)
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

    @Test
    void getUserBalanceInGroup_Success_PositiveBalance() {
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));

        ExpenseEntity expense1 = new ExpenseEntity();
        expense1.setPayer(payer);
        expense1.setAmount(new BigDecimal("100.00"));

        ExpenseShare share1 = new ExpenseShare();
        share1.setUser(payer);
        share1.setShareAmount(new BigDecimal("50.00"));
        expense1.setShares(List.of(share1));

        ExpenseEntity expense2 = new ExpenseEntity();
        expense2.setPayer(member2);
        expense2.setAmount(new BigDecimal("20.00"));

        ExpenseShare share2 = new ExpenseShare();
        share2.setUser(payer);
        share2.setShareAmount(new BigDecimal("10.00"));
        expense2.setShares(List.of(share2));

        when(expenseRepository.findByGroupId(10L)).thenReturn(List.of(expense1, expense2));

        var result = expenseService.getUserBalanceInGroup(10L, payerSubId);

        assertNotNull(result);
        assertEquals(0, result.totalPaidByMe().compareTo(new BigDecimal("100.00")));
        assertEquals(0, result.totalMyShares().compareTo(new BigDecimal("60.00")));
        assertEquals(0, result.netBalance().compareTo(new BigDecimal("40.00")));
    }

    @Test
    void getUserBalanceInGroup_EmptyExpenses_ReturnsZeros() {
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));
        when(expenseRepository.findByGroupId(10L)).thenReturn(Collections.emptyList());

        var result = expenseService.getUserBalanceInGroup(10L, payerSubId);

        assertEquals(BigDecimal.ZERO, result.totalPaidByMe());
        assertEquals(BigDecimal.ZERO, result.totalMyShares());
        assertEquals(BigDecimal.ZERO, result.netBalance());
    }

    @Test
    void getUserBalanceInGroup_ThrowsGroupNotFoundException() {
        when(groupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(com.example.SplitBills.exception.GroupNotFoundException.class, () ->
                expenseService.getUserBalanceInGroup(99L, payerSubId)
        );
    }

    @Test
    void getUserBalanceInGroup_ThrowsException_WhenUserNotMember() {
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));
        UUID strangerSubId = UUID.randomUUID();

        assertThrows(RuntimeException.class, () ->
                expenseService.getUserBalanceInGroup(10L, strangerSubId)
        );
    }

    @Test
    void updateExpense_ThrowsException_WhenUserNotPayer() {
        ExpenseEntity expense = new ExpenseEntity();
        expense.setId(1L);
        expense.setPayer(payer);

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        UpdateExpenseDto updateDto = new UpdateExpenseDto(new BigDecimal("200.00"), "Hack");
        UUID strangerId = UUID.randomUUID();

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                expenseService.updateExpense(1L, updateDto, strangerId)
        );
        assertEquals("Only the payer can update this expense", exception.getMessage());
    }

    @Test
    void deleteExpense_ThrowsException_WhenUserNotPayer() {
        ExpenseEntity expense = new ExpenseEntity();
        expense.setId(1L);
        expense.setPayer(payer);

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        UUID strangerId = UUID.randomUUID();

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                expenseService.deleteExpense(1L, strangerId)
        );
        assertEquals("Only the payer can delete this expense", exception.getMessage());
    }

    @Test
    void addExpense_ThrowsException_WhenGroupNotFound() {
        when(groupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(com.example.SplitBills.exception.GroupNotFoundException.class, () ->
                expenseService.addExpense(99L, expenseDto, payerSubId)
        );
    }

    @Test
    void getExpenseById_ThrowsException_WhenNotFound() {
        when(expenseRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                expenseService.getExpenseById(999L, payerSubId)
        );
        assertEquals("Expense not found", exception.getMessage());
    }

    @Test
    void getExpensesByGroupId_ThrowsException_WhenUserNotMember() {
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));
        UUID strangerId = UUID.randomUUID();

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                expenseService.getExpensesByGroupId(10L, strangerId)
        );
        assertTrue(exception.getMessage().contains("Access denied"));
    }
}