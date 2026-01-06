package org.example.playground.domain.report.exception;

import org.example.playground.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ReportErrorCode implements ErrorCode {
    // ===== 신고 생성 =====
    REPORT_TARGET_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT_TARGET_NOT_FOUND", "신고 대상이 존재하지 않습니다."),
    REPORTER_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORTER_NOT_FOUND", "신고자를 찾을 수 없습니다."),
    REPORTED_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORTED_USER_NOT_FOUND", "피신고자를 찾을 수 없습니다."),
    REPORTED_USER_DELETED(HttpStatus.NOT_FOUND, "REPORTED_USER_DELETED", "피신고자가 탈퇴한 계정입니다."),
    SELF_REPORT_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "SELF_REPORT_NOT_ALLOWED", "자기 자신은 신고할 수 없습니다."),
    DUPLICATE_REPORT(HttpStatus.CONFLICT, "DUPLICATE_REPORT", "이미 신고된 대상입니다."),
    INVALID_REPORT_TARGET_TYPE(HttpStatus.BAD_REQUEST, "INVALID_REPORT_TARGET_TYPE","유효하지 않은 신고 대상 타입입니다."),


    // ===== 신고 처리 =====
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT_NOT_FOUND", "신고 내역을 찾을 수 없습니다."),

    REPORT_NOT_PENDING(HttpStatus.BAD_REQUEST, "REPORT_NOT_PENDING", "대기 상태의 신고만 처리할 수 있습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ReportErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

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
}
