package com.pk.crm.gateway.exception;

import com.pk.crm.exception.AuthenticationException;
import com.pk.crm.exception.AuthorizationException;
import com.pk.crm.exception.CrmException;
import com.pk.crm.exception.CustomerNotFoundException;
import com.pk.crm.exception.UserNotFoundException;
import com.pk.crm.exception.ValidationException;
import com.pk.crm.gateway.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomerNotFound(CustomerNotFoundException ex, WebRequest request) {
        log.warn("Customer not found: {}", ex.getErrorMessage());
        
        ErrorResponse errorResponse = new ErrorResponse()
                .setErrorCode(ex.getErrorCode())
                .setErrorMessage(ex.getErrorMessage())
                .setPath(request.getDescription(false));
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        log.warn("User not found: {}", ex.getErrorMessage());
        
        ErrorResponse errorResponse = new ErrorResponse()
                .setErrorCode(ex.getErrorCode())
                .setErrorMessage(ex.getErrorMessage())
                .setPath(request.getDescription(false));
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex, WebRequest request) {
        log.warn("Authentication failed: {}", ex.getErrorMessage());
        
        ErrorResponse errorResponse = new ErrorResponse()
                .setErrorCode(ex.getErrorCode())
                .setErrorMessage(ex.getErrorMessage())
                .setPath(request.getDescription(false));
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ErrorResponse> handleAuthorization(AuthorizationException ex, WebRequest request) {
        log.warn("Authorization failed: {}", ex.getErrorMessage());
        
        ErrorResponse errorResponse = new ErrorResponse()
                .setErrorCode(ex.getErrorCode())
                .setErrorMessage(ex.getErrorMessage())
                .setPath(request.getDescription(false));
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex, WebRequest request) {
        log.warn("Validation error: {}", ex.getErrorMessage());
        
        ErrorResponse errorResponse = new ErrorResponse()
                .setErrorCode(ex.getErrorCode())
                .setErrorMessage(ex.getErrorMessage())
                .setPath(request.getDescription(false));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        log.warn("Method argument validation failed: {}", ex.getMessage());
        
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                .orElse("Validation failed");
        
        ErrorResponse errorResponse = new ErrorResponse()
                .setErrorCode("VALIDATION_ERROR")
                .setErrorMessage(errorMessage)
                .setPath(request.getDescription(false));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(CrmException.class)
    public ResponseEntity<ErrorResponse> handleCrmException(CrmException ex, WebRequest request) {
        log.error("CRM business error: {}", ex.getErrorMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse()
                .setErrorCode(ex.getErrorCode())
                .setErrorMessage(ex.getErrorMessage())
                .setPath(request.getDescription(false));
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex, WebRequest request) {
        log.error("Unexpected error occurred", ex);
        
        ErrorResponse errorResponse = new ErrorResponse()
                .setErrorCode("INTERNAL_ERROR")
                .setErrorMessage("An unexpected error occurred")
                .setPath(request.getDescription(false));
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}