package org.example.playground.domain.comment.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.playground.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements ErrorCode {

    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT_NOT_FOUND", "댓글을 찾을 수 없습니다."),
    COMMENT_DELETED(HttpStatus.GONE,"COMMENT_DELETED","삭제된 댓글입니다."),
    COMMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN,"COMMENT_ACCESS_DENIED","댓글에 대한 권한이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

}
