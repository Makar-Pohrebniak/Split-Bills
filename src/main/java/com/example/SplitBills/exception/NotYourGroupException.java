package com.example.SplitBills.exception;

import com.example.SplitBills.enums.ErrorType;
import lombok.Getter;

@Getter
public class NotYourGroupException extends AbstractServiceException {
  private static final String ERROR_MESSAGE = "This is not your group, you can't delete it!";
  private final ErrorType ERROR_TYPE = ErrorType.NOT_YOUR_GROUP;

  public NotYourGroupException() {
    super(ERROR_MESSAGE, ErrorType.NOT_YOUR_GROUP);
  }
}
