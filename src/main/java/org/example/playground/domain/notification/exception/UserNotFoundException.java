package org.example.playground.domain.notification.exception;

public class UserNotFoundException extends RuntimeException {
    private final NotificationErrorCode errorCode;

    public UserNotFoundException(NotificationErrorCode errorCode, Long userId) {
        super(errorCode.getMessage() + " (userId: " + userId + ")");
        this.errorCode = errorCode;
    }

    public NotificationErrorCode getErrorCode() {
        return errorCode;
    }
}

