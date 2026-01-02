package org.example.playground.domain.notification.exception;

public class NotificationNotFoundException extends RuntimeException {
    private final NotificationErrorCode errorCode;

    public NotificationNotFoundException(NotificationErrorCode errorCode, Long notificationId) {
        super(errorCode.getMessage() + " (notificationId: " + notificationId + ")");
        this.errorCode = errorCode;
    }

    public NotificationErrorCode getErrorCode() {
        return errorCode;
    }
}

