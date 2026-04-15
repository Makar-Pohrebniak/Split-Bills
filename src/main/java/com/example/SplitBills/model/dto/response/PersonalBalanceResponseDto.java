package com.example.SplitBills.model.dto.response;

import java.math.BigDecimal;

public record PersonalBalanceResponseDto(
        BigDecimal totalPaidByMe,
        BigDecimal totalMyShares,
        BigDecimal netBalance
) {}