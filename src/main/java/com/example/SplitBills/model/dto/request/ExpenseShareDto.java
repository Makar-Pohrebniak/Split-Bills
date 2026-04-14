package com.example.SplitBills.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record ExpenseShareDto(
        @NotNull(message = "User ID is required")
        UUID userId,

        @NotNull(message = "Share amount is required")
        @Positive(message = "Share amount must be positive")
        BigDecimal amount
) {}