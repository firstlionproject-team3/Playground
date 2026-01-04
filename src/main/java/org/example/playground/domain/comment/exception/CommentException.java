package org.example.playground.domain.comment.exception;

import org.springframework.http.HttpStatus;

public class CommentException extends RuntimeException {

    // TODO: 타입 ErrorCode로 변경 예정
    private final CommentErrorCode errorCode;

    public CommentException(CommentErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return errorCode.getStatus();
    }

    public String getCode() {
        return errorCode.getCode();
    }
}
