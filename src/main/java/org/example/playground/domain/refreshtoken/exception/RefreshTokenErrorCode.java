package org.example.playground.domain.refreshtoken.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.playground.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RefreshTokenErrorCode implements ErrorCode {

    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "REFRESH_TOKEN_NOT_FOUND", "토큰을 찾을 수 없습니다."),
    REFRESH_TOKEN_MISMATCH(HttpStatus.BAD_REQUEST, "REFRESH_TOKEN_MISMATCH", "저장된 토큰과 일치하지 않습니다."),
    ;



    private final HttpStatus status;
    private final String code;
    private final String message;
}
