package com.example.SplitBills.service.api;

import com.example.SplitBills.model.dto.response.PaymentResponseDto;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    PaymentResponseDto createPayment(UUID senderSubId, Long groupId, String comment);

    PaymentResponseDto approvePayment(Long paymentId, UUID adminSubId);

    PaymentResponseDto declinePayment(Long paymentId, UUID adminSubId);

    List<PaymentResponseDto> getGroupPayments(Long groupId);
}