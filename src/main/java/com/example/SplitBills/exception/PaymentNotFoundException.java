package com.example.SplitBills.exception;

import com.example.SplitBills.enums.ErrorType;
import lombok.Getter;

@Getter
public class PaymentNotFoundException extends AbstractServiceException {
    private static final String ERROR_MESSAGE = "Payment with this ID not found!";
    private final ErrorType ERROR_TYPE = ErrorType.PAYMENT_NOT_FOUND;

    public PaymentNotFoundException(Long id) {
        super(String.format("Payment with ID %d not found!", id), ErrorType.PAYMENT_NOT_FOUND);
    }
}