package com.example.SplitBills.exception;

import com.example.SplitBills.enums.ErrorType;
import lombok.Getter;

@Getter
public class UserNotFoundException extends AbstractServiceException {
    private static final String ERROR_MESSAGE = "The user not found with: ";
    private final ErrorType ERROR_TYPE = ErrorType.USER_NOT_FOUND;

    public UserNotFoundException(String value) {
        super(ERROR_MESSAGE + value, ErrorType.USER_NOT_FOUND);
    }
    public UserNotFoundException(Long id) {
        super(ERROR_MESSAGE + id, ErrorType.USER_NOT_FOUND);
    }
}
