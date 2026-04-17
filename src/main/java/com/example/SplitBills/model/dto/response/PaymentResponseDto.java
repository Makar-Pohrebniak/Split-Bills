package com.example.SplitBills.model.dto.response;

import com.example.SplitBills.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponseDto {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private Long groupId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
