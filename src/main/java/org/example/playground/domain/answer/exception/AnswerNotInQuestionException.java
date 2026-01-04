package org.example.playground.domain.answer.exception;

public class AnswerNotInQuestionException extends RuntimeException {
    public AnswerNotInQuestionException() {
        super("해당 질문에 달린 답변만 채택할 수 있습니다.");
    }
}
