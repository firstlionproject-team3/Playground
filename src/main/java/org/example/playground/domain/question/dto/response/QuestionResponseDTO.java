package org.example.playground.domain.question.dto.response;

import org.example.playground.domain.question.entity.Question;

import java.time.LocalDateTime;

//서버가 클라에게 주기로 한 데이터들
public record QuestionResponseDTO(
        Long id,
        Long memberId,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    //question 하나를 questionresponse로 바꿔줘야함
    public static QuestionResponseDTO from(Question question) {
        return new QuestionResponseDTO(
                question.getId(),
                question.getMemberId(),
                question.getTitle(),
                question.getContent(),
                question.getCreatedAt(),
                question.getUpdatedAt()
        );
    }
}
