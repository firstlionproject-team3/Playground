package org.example.playground.domain.answer.exception;

public class CannotAnswerOwnQuestionException extends RuntimeException {
    public CannotAnswerOwnQuestionException() {
        super("자기 질문에는 답변을 작성할 수 없습니다.");
    }
}
