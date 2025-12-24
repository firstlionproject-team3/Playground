package org.example.playground.domain.question.dto.response;

import org.example.playground.domain.question.entity.Question;

import java.time.LocalDateTime;

//서버가 클라에게 주기로 한 데이터들
public record QuestionResponse(
        Long id,
        Long member_id,
        String title,
        String content,
        LocalDateTime created_at,
        LocalDateTime updated_at
) {
    //question 하나를 questionresponse로 바꿔줘야함
    public static QuestionResponse from(Question question) {
        return new QuestionResponse(
                question.getId(),
                question.getMember_id(),
                question.getTitle(),
                question.getContent(),
                question.getCreated_at(),
                question.getUpdated_at()
        );
    }
}
