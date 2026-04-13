package com.example.SplitBills.service.api;

import com.example.SplitBills.model.dto.request.AddExpenseDto;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface ExpenseService {

    void addExpense(Long groupId, AddExpenseDto expenseDto, UUID subId);
}
