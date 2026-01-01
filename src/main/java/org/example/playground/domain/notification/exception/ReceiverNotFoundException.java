package org.example.playground.domain.notification.exception;

public class ReceiverNotFoundException extends RuntimeException {
    private final NotificationErrorCode errorCode;

    public ReceiverNotFoundException(NotificationErrorCode errorCode, Long receiverId) {
        super(errorCode.getMessage() + " (receiverId: " + receiverId + ")");
        this.errorCode = errorCode;
    }

    public NotificationErrorCode getErrorCode() {
        return errorCode;
    }
}

