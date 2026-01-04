package org.example.playground.domain.answer.dto.response;

import org.example.playground.domain.answer.entity.Answer;

import java.time.LocalDateTime;

public record AnswerSummaryResponseDTO(
        Long id,
        String nickname,
        String content,
        boolean accepted,

        long likeCount,
        long dislikeCount,
        String myReactionType, // LIKE | DISLIKE | NONE

        LocalDateTime createdAt
) {
    public static AnswerSummaryResponseDTO from(
            Answer answer,
            long likeCount,
            long dislikeCount,
            String myReactionType
    ) {
        return new AnswerSummaryResponseDTO(
                answer.getId(),
                answer.getUser().getNickname(),
                answer.getContent(),
                answer.isAccepted(),
                likeCount,
                dislikeCount,
                myReactionType,
                answer.getCreatedAt()
        );
    }
}
