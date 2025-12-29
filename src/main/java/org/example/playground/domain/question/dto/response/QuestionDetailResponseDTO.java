package org.example.playground.domain.question.dto.response;

import org.example.playground.domain.question.entity.Question;

import java.time.LocalDateTime;

public record QuestionDetailResponseDTO(
        Long id,
        Long member_id,
        String title,
        String content,
        LocalDateTime created_at,
        LocalDateTime updated_at
) {
    public static QuestionDetailResponseDTO from(Question question) {
        return new QuestionDetailResponseDTO(
                question.getId(),
                question.getMember_id(),
                question.getTitle(),
                question.getContent(),
                question.getCreated_at(),
                question.getUpdated_at()
        );
    }
}
