package org.example.playground.domain.notification.exception;

import lombok.RequiredArgsConstructor;
import org.example.playground.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {

    RECEIVER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "NOTIFICATION_RECEIVER_NOT_FOUND",
            "수신자를 찾을 수 없습니다."
    ),

    SENDER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "NOTIFICATION_SENDER_NOT_FOUND",
            "발신자를 찾을 수 없습니다."
    ),

    NOTIFICATION_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "NOTIFICATION_NOT_FOUND",
            "알림을 찾을 수 없습니다."
    ),

    UNSUPPORTED_NOTIFICATION_TYPE(
            HttpStatus.BAD_REQUEST,
            "NOTIFICATION_UNSUPPORTED_TYPE",
            "지원하지 않는 알림 유형입니다."
    ),

    UNAUTHORIZED_READ_ACCESS(
            HttpStatus.FORBIDDEN,
            "NOTIFICATION_UNAUTHORIZED_READ",
            "본인의 알림만 읽음 처리할 수 있습니다."
    ),

    UNAUTHORIZED_DELETE_ACCESS(
            HttpStatus.FORBIDDEN,
            "NOTIFICATION_UNAUTHORIZED_DELETE",
            "본인의 알림만 삭제할 수 있습니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public static NotificationErrorCode from(String code) {
        if (code == null) {
            return null;
        }

        return Arrays.stream(NotificationErrorCode.values())
                .filter(errorCode -> errorCode.getCode().equals(code))
                .findAny()
                .orElse(null);
    }
}

