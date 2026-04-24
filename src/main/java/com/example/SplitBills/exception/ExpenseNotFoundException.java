package com.example.SplitBills.exception;

import com.example.SplitBills.enums.ErrorType;
import lombok.Getter;

@Getter
public class ExpenseNotFoundException extends AbstractServiceException {
    private static final String ERROR_MESSAGE = "Expense not found";
    private ErrorType ERROR_TYPE = ErrorType.EXPENSE_NOT_FOUND;
    public ExpenseNotFoundException() {
        super(ERROR_MESSAGE, ErrorType.EXPENSE_NOT_FOUND);
    }
}
