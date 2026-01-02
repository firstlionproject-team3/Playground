package org.example.playground.domain.notification.exception;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum NotificationErrorCode {

    RECEIVER_NOT_FOUND(
            "NOTIFICATION_RECEIVER_NOT_FOUND",
            "수신자를 찾을 수 없습니다.",
            null
    ),

    SENDER_NOT_FOUND(
            "NOTIFICATION_SENDER_NOT_FOUND",
            "발신자를 찾을 수 없습니다.",
            null
    ),

    USER_NOT_FOUND(
            "NOTIFICATION_USER_NOT_FOUND",
            "사용자를 찾을 수 없습니다.",
            null
    ),

    NOTIFICATION_NOT_FOUND(
            "NOTIFICATION_NOT_FOUND",
            "알림을 찾을 수 없습니다.",
            null
    ),

    UNSUPPORTED_NOTIFICATION_TYPE(
            "NOTIFICATION_UNSUPPORTED_TYPE",
            "지원하지 않는 알림 유형입니다.",
            null
    ),

    UNAUTHORIZED_READ_ACCESS(
            "NOTIFICATION_UNAUTHORIZED_READ",
            "본인의 알림만 읽음 처리할 수 있습니다.",
            null
    ),

    UNAUTHORIZED_DELETE_ACCESS(
            "NOTIFICATION_UNAUTHORIZED_DELETE",
            "본인의 알림만 삭제할 수 있습니다.",
            null
    );

    private final String code;
    private final String message;
    private final String uri; // 에러 설명 문서 링크

    NotificationErrorCode(String code, String message, String uri) {
        this.code = code;
        this.message = message;
        this.uri = uri;
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

