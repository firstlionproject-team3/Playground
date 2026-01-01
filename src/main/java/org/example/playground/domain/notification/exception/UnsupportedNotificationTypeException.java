package org.example.playground.domain.notification.exception;

import org.example.playground.domain.notification.entity.NotificationType;

public class UnsupportedNotificationTypeException extends RuntimeException {
    private final NotificationErrorCode errorCode;

    public UnsupportedNotificationTypeException(NotificationErrorCode errorCode, NotificationType type) {
        super(errorCode.getMessage() + " (type: " + type + ")");
        this.errorCode = errorCode;
    }

    public NotificationErrorCode getErrorCode() {
        return errorCode;
    }
}

