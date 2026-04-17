package com.example.SplitBills.exception;

import com.example.SplitBills.enums.ErrorType;
import lombok.Getter;

@Getter
public class UnauthorizedAccessException extends AbstractServiceException {
    private static final String ERROR_MESSAGE = "You don't have permission to perform this action!";
    private final ErrorType ERROR_TYPE = ErrorType.UNAUTHORIZED_ACCESS;

    public UnauthorizedAccessException(String message) {
        super(message, ErrorType.UNAUTHORIZED_ACCESS);
    }
}