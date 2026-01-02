package org.example.playground.domain.answer.dto.response;

import org.example.playground.domain.answer.entity.Answer;

import java.time.LocalDateTime;

public record AnswerSummaryResponseDTO(
        Long id,
        String nickname,
        String content,
        LocalDateTime createdAt
) {

    public static AnswerSummaryResponseDTO from(Answer answer) {
        return new AnswerSummaryResponseDTO(
                answer.getId(),
                answer.getUser().getNickname(),
                answer.getContent(),
                answer.getCreatedAt()
        );
    }
}
