package org.example.playground.domain.question.exception;

public class QuestionNotFoundException extends RuntimeException{
    public QuestionNotFoundException(Long questionId) {
        super("해당하는 질문을 찾을 수 없습니다. questionId = " + questionId);
    }
}
