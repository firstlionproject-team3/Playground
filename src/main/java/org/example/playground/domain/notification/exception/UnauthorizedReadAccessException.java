package org.example.playground.domain.notification.exception;

public class UnauthorizedReadAccessException extends RuntimeException {
    private final NotificationErrorCode errorCode;

    public UnauthorizedReadAccessException(NotificationErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public NotificationErrorCode getErrorCode() {
        return errorCode;
    }
}

