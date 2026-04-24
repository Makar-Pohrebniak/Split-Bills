package com.example.SplitBills;

import com.example.SplitBills.enums.ErrorType;
import com.example.SplitBills.exception.*;
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

    @ExceptionHandler(YouAreNotYourFriendException.class)
    public ResponseEntity<ApiError> handleYouAreNotYourFriendException(YouAreNotYourFriendException ex) {
        log.info(ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getErrorType(), ex.getMessage());
    }

    @ExceptionHandler(GroupNotFoundException.class)
    public ResponseEntity<ApiError> handleGroupNotFoundException(GroupNotFoundException ex) {
        log.info(ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getErrorType(), ex.getMessage());
    }

    @ExceptionHandler(NotYourGroupException.class)
    public ResponseEntity<ApiError> handleNotYourGroupException(NotYourGroupException ex) {
        log.info(ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getErrorType(), ex.getMessage());
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ApiError> handleTooManyRequestsException(TooManyRequestsException ex) {
        log.info(ex.getMessage());
        return buildResponse(HttpStatus.TOO_MANY_REQUESTS, ex.getErrorType(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.info("Validation error: {}", errorMessage);

        return buildResponse(HttpStatus.BAD_REQUEST, ErrorType.VALIDATION_ERROR, errorMessage);
    }

    @ExceptionHandler(BadRefreshTokenException.class)
    public ResponseEntity<ApiError> handleGroupNotFoundException(BadRefreshTokenException ex) {
        log.info(ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getErrorType(), ex.getMessage());
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ApiError> handlePaymentNotFoundException(PaymentNotFoundException ex) {
        log.info(ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getERROR_TYPE(), ex.getMessage());
    }

    @ExceptionHandler(InvalidPaymentOperationException.class)
    public ResponseEntity<ApiError> handleInvalidPaymentOperationException(InvalidPaymentOperationException ex) {
        log.info(ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getERROR_TYPE(), ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiError> handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
        log.info(ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, ex.getERROR_TYPE(), ex.getMessage());
    }

    @ExceptionHandler(NotYourExpenseException.class)
    public ResponseEntity<ApiError> handleNotYourExpenseException(NotYourExpenseException ex) {
        log.info(ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getERROR_TYPE(), ex.getMessage());
    }

    @ExceptionHandler(NotAMemberException.class)
    public ResponseEntity<ApiError> handleNotYourExpenseException(NotAMemberException ex) {
        log.info(ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, ex.getERROR_TYPE(), ex.getMessage());
    }

    @ExceptionHandler(ExpenseNotFoundException.class)
    public ResponseEntity<ApiError> handleExpenseNotFoundException(ExpenseNotFoundException ex) {
        log.info(ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getERROR_TYPE(), ex.getMessage());
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