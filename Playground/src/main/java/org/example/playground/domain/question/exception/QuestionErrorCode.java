package org.example.playground.domain.question.exception;

import lombok.RequiredArgsConstructor;
import org.example.playground.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum QuestionErrorCode implements ErrorCode {

    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "QUESTION_NOT_FOUND", "해당하는 질문을 찾을 수 없습니다."),
    ANSWER_ACCEPT_FORBIDDEN(HttpStatus.FORBIDDEN, "ANSWER_ACCEPT_FORBIDDEN", "질문 작성자만 답변을 채택할 수 있습니다."),
    ANSWER_ALREADY_ACCEPTED(HttpStatus.CONFLICT, "ANSWER_ALREADY_ACCEPTED", "이미 채택된 답변이 존재합니다."),
    SELF_ANSWER_ADOPT_NOT_ALLOWED(HttpStatus.FORBIDDEN, "SELF_ANSWER_ADOPT_NOT_ALLOWED", "자기 답변은 채택할 수 없습니다."),
    QUESTION_OWNER_MISMATCH(HttpStatus.FORBIDDEN, "QUESTION_OWNER_MISMATCH", "작성자만 가능합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override public HttpStatus getStatus() { return status; }
    @Override public String getCode() { return code; }
    @Override public String getMessage() { return message; }
}

