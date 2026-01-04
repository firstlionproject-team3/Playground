package org.example.playground.domain.question.exception;

public class AnswerAcceptForbiddenException extends RuntimeException {
    public AnswerAcceptForbiddenException() {
        super("질문 작성자만 답변을 채택할 수 있습니다.");
    }
}
