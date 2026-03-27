package com.example.SplitBills.exception;

import com.example.SplitBills.enums.ErrorType;
import lombok.Getter;

@Getter
public class GroupNotFoundException extends AbstractServiceException {
    private static final String ERROR_MESSAGE = "The group not found with: ";
    private final ErrorType ERROR_TYPE = ErrorType.GROUP_NOT_FOUND;

    public GroupNotFoundException(Long id) {
        super(ERROR_MESSAGE + id, ErrorType.GROUP_NOT_FOUND);
    }
}
