package com.example.SplitBills.exception;

import com.example.SplitBills.enums.ErrorType;
import lombok.Getter;

@Getter
public class NotAMemberException extends AbstractServiceException {
    private final ErrorType ERROR_TYPE = ErrorType.NOT_A_MEMBER;

    public NotAMemberException(String message) {
        super(message, ErrorType.NOT_YOUR_EXPENSE);
    }

}
