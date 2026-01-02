package org.example.playground.domain.notification.exception;

public class UnauthorizedDeleteAccessException extends RuntimeException {
    private final NotificationErrorCode errorCode;

    public UnauthorizedDeleteAccessException(NotificationErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public NotificationErrorCode getErrorCode() {
        return errorCode;
    }
}

