package com.example.SplitBills.exception;

import com.example.SplitBills.enums.ErrorType;
import lombok.Getter;

@Getter
public class TooManyRequestsException extends AbstractServiceException {
    private static final String ERROR_MESSAGE = "Not so fast!";
    private final ErrorType ERROR_TYPE = ErrorType.TOO_MANY_REQUESTS;

    public TooManyRequestsException() {
        super(ERROR_MESSAGE, ErrorType.TOO_MANY_REQUESTS);
    }
}
