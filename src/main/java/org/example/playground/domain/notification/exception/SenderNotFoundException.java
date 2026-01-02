package org.example.playground.domain.notification.exception;

public class SenderNotFoundException extends RuntimeException {
    private final NotificationErrorCode errorCode;

    public SenderNotFoundException(NotificationErrorCode errorCode, Long senderId) {
        super(errorCode.getMessage() + " (senderId: " + senderId + ")");
        this.errorCode = errorCode;
    }

    public NotificationErrorCode getErrorCode() {
        return errorCode;
    }
}

