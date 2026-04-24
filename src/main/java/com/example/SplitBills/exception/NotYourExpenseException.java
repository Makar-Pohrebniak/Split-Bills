package com.example.SplitBills.exception;

import com.example.SplitBills.enums.ErrorType;
import lombok.Getter;

@Getter
public class NotYourExpenseException extends AbstractServiceException {
  private final ErrorType ERROR_TYPE = ErrorType.NOT_YOUR_EXPENSE;

  public NotYourExpenseException(String message) {
    super(message, ErrorType.NOT_YOUR_EXPENSE);
  }

}
