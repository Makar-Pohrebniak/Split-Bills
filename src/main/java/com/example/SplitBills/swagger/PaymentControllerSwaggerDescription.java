package com.example.SplitBills.swagger;

import com.example.SplitBills.exception.ApiError;
import com.example.SplitBills.model.dto.response.PaymentResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "Payment Management", description = "Endpoints for creating and managing debt settlements between users")
public interface PaymentControllerSwaggerDescription {

    @Operation(summary = "Create a new payment",
            description = "Creates a payment request to settle the current debt in a group. The receiver is always the group owner.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid operation: no debt found or self-payment attempted",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Access denied: you are not a member of this group",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Group not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<PaymentResponseDto> createPayment(Long groupId, String comment, UUID subId);

    @Operation(summary = "Approve a payment",
            description = "Confirms that the payment was received. Only the group owner can perform this action.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment successfully approved"),
            @ApiResponse(responseCode = "400", description = "Payment is already processed",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Only group owner can approve payments",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<PaymentResponseDto> approvePayment(Long paymentId, UUID subId);

    @Operation(summary = "Decline a payment",
            description = "Rejects the payment request. Only the group owner can perform this action.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment successfully declined"),
            @ApiResponse(responseCode = "403", description = "Only group owner can decline payments",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<PaymentResponseDto> declinePayment(Long paymentId, UUID subId);

    @Operation(summary = "Get all payments in a group",
            description = "Retrieves the full history of payments for a specific group.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved payments list"),
            @ApiResponse(responseCode = "404", description = "Group not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<List<PaymentResponseDto>> getGroupPayments(Long groupId);
}