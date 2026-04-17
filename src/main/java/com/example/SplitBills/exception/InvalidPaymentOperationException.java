package com.example.SplitBills.exception;

import com.example.SplitBills.enums.ErrorType;
import lombok.Getter;

@Getter
public class InvalidPaymentOperationException extends AbstractServiceException {
    private final ErrorType ERROR_TYPE = ErrorType.INVALID_PAYMENT_OPERATION;

    public InvalidPaymentOperationException(String message) {
        super(message, ErrorType.INVALID_PAYMENT_OPERATION);
    }
}