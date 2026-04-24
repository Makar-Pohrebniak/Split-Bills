package com.example.SplitBills.exception;

import com.example.SplitBills.enums.ErrorType;
import lombok.Getter;

@Getter
public class NotYourGroupException extends AbstractServiceException {
  private final ErrorType ERROR_TYPE = ErrorType.NOT_YOUR_GROUP;

  public NotYourGroupException(String message) {
    super(message, ErrorType.NOT_YOUR_GROUP);
  }
}
