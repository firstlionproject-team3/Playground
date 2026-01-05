package org.example.playground.domain.question.dto.response;

import org.example.playground.domain.answer.dto.response.AnswerSummaryResponseDTO;
import org.example.playground.domain.question.entity.Question;

import java.time.LocalDateTime;
import java.util.List;

public record QuestionDetailResponseDTO(
        Long id,
        String nickname,
        String title,
        String content,
        Long viewCount,

        long likeCount,
        long dislikeCount,
        String myReactionType, // LIKE | DISLIKE | NONE

        List<AnswerSummaryResponseDTO> answers,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    /**
     * Service에서 계산한 reaction 정보 + 답변 DTO 리스트를 주입받아 생성
     */
    public static QuestionDetailResponseDTO from(
            Question question,
            long likeCount,
            long dislikeCount,
            String myReactionType,
            List<AnswerSummaryResponseDTO> answers
    ) {
        return new QuestionDetailResponseDTO(
                question.getId(),
                question.getUser().getNickname(),
                question.getTitle(),
                question.getContent(),
                question.getViewCount(),
                likeCount,
                dislikeCount,
                myReactionType,
                answers,
                question.getCreatedAt(),
                question.getUpdatedAt()
        );
    }
}
