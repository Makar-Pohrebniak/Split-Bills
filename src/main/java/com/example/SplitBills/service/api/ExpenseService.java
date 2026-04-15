package com.example.SplitBills.service.api;

import com.example.SplitBills.model.dto.request.AddExpenseDto;
import com.example.SplitBills.model.dto.request.UpdateExpenseDto;
import com.example.SplitBills.model.dto.response.ExpenseResponseDto;
import com.example.SplitBills.model.dto.response.PersonalBalanceResponseDto;

import java.util.List;
import java.util.UUID;

public interface ExpenseService {

    void addExpense(Long groupId, AddExpenseDto expenseDto, UUID subId);

    ExpenseResponseDto getExpenseById(Long expenseId, UUID subId);

    List<ExpenseResponseDto> getExpensesByGroupId(Long groupId, UUID subId);

    void updateExpense(Long expenseId, UpdateExpenseDto updateDto, UUID subId);

    void deleteExpense(Long expenseId, UUID subId);

    PersonalBalanceResponseDto getUserBalanceInGroup(Long groupId, UUID subId);
}