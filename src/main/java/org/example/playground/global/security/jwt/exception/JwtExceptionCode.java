package org.example.playground.global.security.jwt.exception;

import lombok.Getter;

import java.util.Arrays;

public enum JwtExceptionCode {

    UNKNOWN_ERROR("UNKNOWN_ERROR", "알 수 없는 에러"),
    INVALID_TOKEN("INVALID_TOKEN", "유효하지 않은 토큰"),
    EXPIRED_TOKEN("EXPIRED_TOKEN", "기간이 만료된 토큰"),
    UNSUPPORTED_TOKEN("UNSUPPORTED_TOKEN", "지원하지 않는 토큰");

    JwtExceptionCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Getter
    private String code;

    @Getter
    private String message;

    public static JwtExceptionCode findByCode(String code) {
        return Arrays.stream(JwtExceptionCode.values())
                .filter(c -> c.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN_ERROR);
    }
}
