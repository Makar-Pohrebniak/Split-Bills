package com.example.SplitBills.controller;

import com.example.SplitBills.model.dto.response.PaymentResponseDto;
import com.example.SplitBills.service.api.PaymentService;
import com.example.SplitBills.swagger.PaymentControllerSwaggerDescription;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class PaymentController implements PaymentControllerSwaggerDescription {

    private final PaymentService paymentService;

    @Override
    @PostMapping("/group/{groupId}")
    public ResponseEntity<PaymentResponseDto> createPayment(
            @PathVariable Long groupId,
            @RequestParam(required = false) String comment,
            @AuthenticationPrincipal UUID subId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.createPayment(subId, groupId, comment));
    }

    @Override
    @PatchMapping("/{paymentId}/approve")
    public ResponseEntity<PaymentResponseDto> approvePayment(
            @PathVariable Long paymentId,
            @AuthenticationPrincipal UUID subId
    ) {
        return ResponseEntity.ok(paymentService.approvePayment(paymentId, subId));
    }

    @Override
    @PatchMapping("/{paymentId}/decline")
    public ResponseEntity<PaymentResponseDto> declinePayment(
            @PathVariable Long paymentId,
            @AuthenticationPrincipal UUID subId
    ) {
        return ResponseEntity.ok(paymentService.declinePayment(paymentId, subId));
    }

    @Override
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<PaymentResponseDto>> getGroupPayments(
            @PathVariable Long groupId
    ) {
        return ResponseEntity.ok(paymentService.getGroupPayments(groupId));
    }
}