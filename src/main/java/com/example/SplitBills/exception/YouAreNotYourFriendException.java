package com.example.SplitBills.exception;

import com.example.SplitBills.enums.ErrorType;
import lombok.Getter;

@Getter
public class YouAreNotYourFriendException extends AbstractServiceException {
    private static final String ERROR_MESSAGE = "You are not your friend";
    private final ErrorType ERROR_TYPE = ErrorType.YOU_ARE_NOT_YOUR_FRIEND;

    public YouAreNotYourFriendException() {
        super(ERROR_MESSAGE, ErrorType.YOU_ARE_NOT_YOUR_FRIEND);
    }
}
