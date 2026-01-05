package org.example.playground.domain.answer.exception;

import lombok.RequiredArgsConstructor;
import org.example.playground.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum AnswerErrorCode implements ErrorCode {

    ANSWER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "ANSWER_NOT_FOUND",
            "해당하는 답변을 찾을 수 없습니다."
    ),

    ANSWER_NOT_IN_QUESTION(
            HttpStatus.BAD_REQUEST,
            "ANSWER_NOT_IN_QUESTION",
            "해당 질문에 달린 답변만 처리할 수 있습니다."
    ),

    CANNOT_ANSWER_OWN_QUESTION(
            HttpStatus.FORBIDDEN,
            "CANNOT_ANSWER_OWN_QUESTION",
            "자기 질문에는 답변을 작성할 수 없습니다."
    ),

    ANSWER_OWNER_MISMATCH(HttpStatus.FORBIDDEN, "ANSWER_OWNER_MISMATCH", "작성자만 가능합니다.");


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
}
