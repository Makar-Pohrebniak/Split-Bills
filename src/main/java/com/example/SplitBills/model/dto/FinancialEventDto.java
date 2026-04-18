package com.example.SplitBills.model.dto;

import com.example.SplitBills.enums.FinancialType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialEventDto {
    private Long groupId;
    private Long initiatorId;
    private Long targetId;
    private BigDecimal amount;
    private FinancialType type;
    private String message;
}