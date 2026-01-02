package org.example.playground.domain.answer.exception;

public class AnswerNotFoundException extends RuntimeException{
    public AnswerNotFoundException(Long answerId){
        super("해당하는 답변을 찾을 수 없습니다 answerId = " + answerId);
    }
}
