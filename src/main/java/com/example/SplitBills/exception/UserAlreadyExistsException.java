package com.example.SplitBills.exception;

import com.example.SplitBills.enums.ErrorType;
import lombok.Getter;

@Getter
public class UserAlreadyExistsException extends AbstractServiceException {
    private static final String ERROR_MESSAGE = "The user already exists with ";
    private final ErrorType ERROR_TYPE = ErrorType.USER_ALREADY_EXISTS;

    public UserAlreadyExistsException(String value) {
        super(ERROR_MESSAGE + value, ErrorType.USER_ALREADY_EXISTS);
    }
}
