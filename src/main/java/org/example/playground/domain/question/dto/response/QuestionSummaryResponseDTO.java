package org.example.playground.domain.question.dto.response;

import org.example.playground.domain.question.entity.Question;

import java.time.LocalDateTime;
import java.util.Locale;

public record QuestionSummaryResponseDTO(
        Long id,
        Long memberId,
        String title,
        LocalDateTime createdAt
) {
    public static QuestionSummaryResponseDTO from(Question question) {
        return new QuestionSummaryResponseDTO(
                question.getId(),
                question.getMemberId(),
                question.getTitle(),
                question.getCreatedAt()
                //앞 100지 자르기? preview
        );
    }
}
