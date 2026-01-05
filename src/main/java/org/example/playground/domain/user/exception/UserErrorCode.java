package org.example.playground.domain.user.exception;

import lombok.RequiredArgsConstructor;
import org.example.playground.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    USER_DUPLICATE(HttpStatus.CONFLICT, "USER_DUPLICATE", "이미 존재하는 사용자입니다."),
    OAUTH2_SIGNED_UP(HttpStatus.CONFLICT, "OAUTH2_SIGNED_UP", "OAuth2로 가입된 계정입니다."),
    ANOTHER_USER_FORBIDDEN(HttpStatus.FORBIDDEN, "ANOTHER_USER_FORBIDDEN", "본인 혹은 관리자만 접근할 수 있습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;


    @Override public HttpStatus getStatus() {return status;}
    @Override public String getCode() {return code;}
    @Override public String getMessage() {return message;}
}
