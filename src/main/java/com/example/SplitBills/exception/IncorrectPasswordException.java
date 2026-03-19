package com.example.SplitBills.exception;

import com.example.SplitBills.enums.ErrorType;
import lombok.Getter;

@Getter
public class IncorrectPasswordException extends AbstractServiceException {
    private static final String ERROR_MESSAGE = "Incorrect Password";
    private final ErrorType ERROR_TYPE = ErrorType.INCORRECT_PASSWORD;

    public IncorrectPasswordException() {
        super(ERROR_MESSAGE, ErrorType.INCORRECT_PASSWORD);
    }
}
