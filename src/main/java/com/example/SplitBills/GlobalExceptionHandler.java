package com.example.SplitBills;

import com.example.SplitBills.enums.ErrorType;
import com.example.SplitBills.exception.IncorrectPasswordException;
import com.example.SplitBills.exception.UserAlreadyExistsException;
import com.example.SplitBills.exception.UserNotFoundException;
import com.example.SplitBills.model.dto.ApiError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        log.info(ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getErrorType(), ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException ex) {
        log.info(ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getErrorType(), ex.getMessage());
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<ApiError> handleIncorrectPasswordException(IncorrectPasswordException ex) {
        log.info(ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getErrorType(), ex.getMessage());
    }

    private ResponseEntity<ApiError> buildResponse(HttpStatus status, ErrorType type, String message) {
        ApiError apiError = ApiError.builder()
                .dateTime(Instant.now())
                .errorType(type)
                .errorMessage(message)
                .build();
        return ResponseEntity.status(status).body(apiError);
    }
}