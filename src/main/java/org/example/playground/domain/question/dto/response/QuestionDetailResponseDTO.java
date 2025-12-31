package org.example.playground.domain.question.dto.response;

import org.example.playground.domain.question.entity.Question;

import java.time.LocalDateTime;

public record QuestionDetailResponseDTO(
        Long id,
        String nickname,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static QuestionDetailResponseDTO from(Question question) {
        return new QuestionDetailResponseDTO(
                question.getId(),
                question.getUser().getNickname(),
                question.getTitle(),
                question.getContent(),
                question.getCreatedAt(),
                question.getUpdatedAt()
        );
    }
}
