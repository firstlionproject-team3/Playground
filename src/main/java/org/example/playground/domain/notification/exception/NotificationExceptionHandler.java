package org.example.playground.domain.notification.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "org.example.playground.domain.notification")
public class NotificationExceptionHandler {

    @ExceptionHandler(ReceiverNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleReceiverNotFoundException(ReceiverNotFoundException e) {
        return buildErrorResponse(e.getErrorCode(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SenderNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleSenderNotFoundException(SenderNotFoundException e) {
        return buildErrorResponse(e.getErrorCode(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(UserNotFoundException e) {
        return buildErrorResponse(e.getErrorCode(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotificationNotFoundException(NotificationNotFoundException e) {
        return buildErrorResponse(e.getErrorCode(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsupportedNotificationTypeException.class)
    public ResponseEntity<Map<String, Object>> handleUnsupportedNotificationTypeException(UnsupportedNotificationTypeException e) {
        return buildErrorResponse(e.getErrorCode(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedReadAccessException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedReadAccessException(UnauthorizedReadAccessException e) {
        return buildErrorResponse(e.getErrorCode(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UnauthorizedDeleteAccessException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedDeleteAccessException(UnauthorizedDeleteAccessException e) {
        return buildErrorResponse(e.getErrorCode(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", "ILLEGAL_ARGUMENT");
        errorResponse.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(NotificationErrorCode errorCode, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", errorCode.getCode());
        errorResponse.put("message", errorCode.getMessage());
        if (errorCode.getUri() != null) {
            errorResponse.put("uri", errorCode.getUri());
        }
        return ResponseEntity.status(status).body(errorResponse);
    }
}

