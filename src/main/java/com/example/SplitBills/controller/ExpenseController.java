package com.example.SplitBills.controller;

import com.example.SplitBills.model.dto.request.AddExpenseDto;
import com.example.SplitBills.model.dto.request.UpdateExpenseDto;
import com.example.SplitBills.model.dto.response.ExpenseResponseDto;
import com.example.SplitBills.model.dto.response.PersonalBalanceResponseDto;
import com.example.SplitBills.service.api.ExpenseService;
import com.example.SplitBills.swagger.ExpenseControllerSwaggerDescription;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class ExpenseController implements ExpenseControllerSwaggerDescription {

    private final ExpenseService expenseService;

    @Override
    @PostMapping("/group/{groupId}")
    public ResponseEntity<Void> addExpense(
            @PathVariable Long groupId,
            @Valid @RequestBody AddExpenseDto expenseDto,
            @AuthenticationPrincipal UUID subId
    ) {
        expenseService.addExpense(groupId, expenseDto, subId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<ExpenseResponseDto>> getGroupExpenses(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UUID subId
    ) {
        return ResponseEntity.ok(expenseService.getExpensesByGroupId(groupId, subId));
    }

    @Override
    @GetMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponseDto> getExpense(
            @PathVariable Long expenseId,
            @AuthenticationPrincipal UUID subId
    ) {
        return ResponseEntity.ok(expenseService.getExpenseById(expenseId, subId));
    }

    @Override
    @PutMapping("/{expenseId}")
    public ResponseEntity<Void> updateExpense(
            @PathVariable Long expenseId,
            @Valid @RequestBody UpdateExpenseDto updateDto,
            @AuthenticationPrincipal UUID subId
    ) {
        expenseService.updateExpense(expenseId, updateDto, subId);
        return ResponseEntity.ok().build();
    }

    @Override
    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable Long expenseId,
            @AuthenticationPrincipal UUID subId
    ) {
        expenseService.deleteExpense(expenseId, subId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/group/{groupId}/balance")
    public ResponseEntity<PersonalBalanceResponseDto> getUserBalance(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UUID subId
    ) {
        return ResponseEntity.ok(expenseService.getUserBalanceInGroup(groupId, subId));
    }
}