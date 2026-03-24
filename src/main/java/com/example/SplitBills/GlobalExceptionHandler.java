package com.example.SplitBills;

import com.example.SplitBills.enums.ErrorType;
import com.example.SplitBills.exception.IncorrectPasswordException;
import com.example.SplitBills.exception.UserAlreadyExistsException;
import com.example.SplitBills.exception.UserNotFoundException;
import com.example.SplitBills.exception.ApiError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.info("Validation error: {}", errorMessage);

        return buildResponse(HttpStatus.BAD_REQUEST, ErrorType.VALIDATION_ERROR, errorMessage);
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