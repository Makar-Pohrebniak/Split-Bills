package com.example.SplitBills.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponseDto {

    private Long id;

    private String description;

    private BigDecimal amount;

    private UUID paidBy;

    private LocalDateTime createdAt;
}