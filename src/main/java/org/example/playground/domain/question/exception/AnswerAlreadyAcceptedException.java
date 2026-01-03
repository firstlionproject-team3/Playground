package org.example.playground.domain.question.exception;

public class AnswerAlreadyAcceptedException extends RuntimeException {
    public AnswerAlreadyAcceptedException() {
        super("이미 채택된 답변이 존재합니다.");
    }
}
