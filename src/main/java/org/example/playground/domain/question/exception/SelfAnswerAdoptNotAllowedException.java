package org.example.playground.domain.question.exception;

public class SelfAnswerAdoptNotAllowedException extends RuntimeException {
    public SelfAnswerAdoptNotAllowedException() {
        super("자기 답변은 채택할 수 없습니다.");
    }
}
