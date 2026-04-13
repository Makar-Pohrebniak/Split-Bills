package com.example.SplitBills.controller;

import com.example.SplitBills.model.dto.request.AddExpenseDto;
import com.example.SplitBills.service.api.ExpenseService;
import com.example.SplitBills.swagger.ExpenseControllerSwaggerDescription;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class ExpenseController implements ExpenseControllerSwaggerDescription {

    private final ExpenseService expenseService;

    @Override
    @PostMapping("/{groupId}/expenses")
    public ResponseEntity<Void> addExpense(
            @PathVariable Long groupId,
            @Valid @RequestBody AddExpenseDto expenseDto,
            @AuthenticationPrincipal UUID subId
    ) {
        expenseService.addExpense(groupId, expenseDto, subId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
