package org.example.playground.domain.answer.dto.response;

import org.example.playground.domain.answer.entity.Answer;

import java.time.LocalDateTime;

public record AnswerDetailResponseDTO(
        Long id,
        Long questionId,
        String nickname,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,

        long likeCount,
        long dislikeCount,
        String myReactionType // LIKE | DISLIKE | NONE
) {

    // ✅ 기존 서비스(create/update)에서 쓰기 위한 기본 from
    public static AnswerDetailResponseDTO from(Answer answer) {
        return new AnswerDetailResponseDTO(
                answer.getId(),
                answer.getQuestion().getId(),
                answer.getUser().getNickname(),
                answer.getContent(),
                answer.getCreatedAt(),
                answer.getUpdatedAt(),
                0L,
                0L,
                "NONE"
        );
    }

    // ✅ 단건 조회(findOne)에서 추천/비추천 포함해서 쓰는 from
    public static AnswerDetailResponseDTO from(
            Answer answer,
            long likeCount,
            long dislikeCount,
            String myReactionType
    ) {
        return new AnswerDetailResponseDTO(
                answer.getId(),
                answer.getQuestion().getId(),
                answer.getUser().getNickname(),
                answer.getContent(),
                answer.getCreatedAt(),
                answer.getUpdatedAt(),
                likeCount,
                dislikeCount,
                myReactionType
        );
    }
}
