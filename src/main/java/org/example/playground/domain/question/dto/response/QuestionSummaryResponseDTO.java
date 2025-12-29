package org.example.playground.domain.question.dto.response;

import org.example.playground.domain.question.entity.Question;

import java.time.LocalDateTime;
import java.util.Locale;

public record QuestionSummaryResponseDTO(
        Long id,
        Long member_id,
        String title,
        String content,
        LocalDateTime created_at
) {
    public static QuestionSummaryResponseDTO from(Question question) {
        return new QuestionSummaryResponseDTO(
                question.getId(),
                question.getMember_id(),
                question.getTitle(),
                question.getContent(),
                question.getCreated_at()
                //앞 100지 자르기? preview
        );
    }
}
