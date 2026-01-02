package org.example.playground.domain.answer.dto.response;

import org.example.playground.domain.answer.entity.Answer;

import java.time.LocalDateTime;

public record AnswerDetailResponseDTO(
        Long id,
        Long questionId,
        String nickname,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
    public static AnswerDetailResponseDTO from(Answer answer) {
        return new AnswerDetailResponseDTO(
                answer.getId(),
                answer.getQuestion().getId(),
                answer.getUser().getNickname(),
                answer.getContent(),
                answer.getCreatedAt(),
                answer.getUpdatedAt()
        );
    }
}
