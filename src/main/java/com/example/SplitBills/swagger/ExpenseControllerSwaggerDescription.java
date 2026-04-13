package com.example.SplitBills.swagger;

import com.example.SplitBills.exception.ApiError;
import com.example.SplitBills.model.dto.request.AddExpenseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Expense Management", description = "Endpoints for managing group expenses and bills")
public interface ExpenseControllerSwaggerDescription {

    @Operation(summary = "Add a new expense to a group",
            description = "Adds an expense and automatically splits the amount between all group members.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Expense successfully added and split"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Access denied: you are not a member of this group",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Group not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<Void> addExpense(Long groupId, AddExpenseDto expenseDto, UUID subId);
}