package com.example.SplitBills.swagger;

import com.example.SplitBills.exception.ApiError;
import com.example.SplitBills.model.dto.request.AddExpenseDto;
import com.example.SplitBills.model.dto.request.UpdateExpenseDto;
import com.example.SplitBills.model.dto.response.ExpenseResponseDto;
import com.example.SplitBills.model.dto.response.PersonalBalanceResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "Expense Management", description = "Endpoints for managing group expenses and bills")
public interface ExpenseControllerSwaggerDescription {

    @Operation(summary = "Add a new expense to a group",
            description = "Adds an expense and automatically splits the amount between all group members.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Expense successfully added"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Access denied: you are not a member of this group",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Group not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<Void> addExpense(Long groupId, AddExpenseDto expenseDto, UUID subId);

    @Operation(summary = "Get all expenses for a group",
            description = "Retrieves a list of all expenses associated with a specific group.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved expenses"),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Group not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<List<ExpenseResponseDto>> getGroupExpenses(Long groupId, UUID subId);

    @Operation(summary = "Get detailed information about a specific expense")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved expense"),
            @ApiResponse(responseCode = "404", description = "Expense not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<ExpenseResponseDto> getExpense(Long expenseId, UUID subId);

    @Operation(summary = "Update an existing expense",
            description = "Updates the amount or description of an expense. Only the author or group admin can perform this.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid update data",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Not authorized to update this expense",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Expense not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<Void> updateExpense(Long expenseId, UpdateExpenseDto updateDto, UUID subId);

    @Operation(summary = "Delete an expense",
            description = "Removes an expense from the group and reverts the balance changes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Expense successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Not authorized to delete this expense",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Expense not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<Void> deleteExpense(Long expenseId, UUID subId);

    @Operation(summary = "Get personal balance in a group",
            description = "Calculates how much the user paid, their total shares, and the net balance within a specific group.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved personal balance"),
            @ApiResponse(responseCode = "403", description = "Access denied: you are not a member of this group",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Group not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<PersonalBalanceResponseDto> getUserBalance(Long groupId, UUID subId);
}