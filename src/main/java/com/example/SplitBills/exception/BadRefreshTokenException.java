package com.example.SplitBills.exception;

import com.example.SplitBills.enums.ErrorType;
import lombok.Getter;

@Getter
public class BadRefreshTokenException extends AbstractServiceException {
    private static final String ERROR_MESSAGE = "Invalid or expired refresh token";
    private final ErrorType ERROR_TYPE = ErrorType.BAD_REFRESH_TOKEN;

    public BadRefreshTokenException() {
        super(ERROR_MESSAGE, ErrorType.BAD_REFRESH_TOKEN);
    }
}