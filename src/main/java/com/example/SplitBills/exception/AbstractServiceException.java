package com.example.SplitBills.exception;

import com.example.SplitBills.enums.ErrorType;
import lombok.Getter;

@Getter
public abstract class AbstractServiceException extends RuntimeException {
    private final ErrorType errorType;

    protected AbstractServiceException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }
}