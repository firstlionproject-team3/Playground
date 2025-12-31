package org.example.playground.domain.question.dto.response;

import org.example.playground.domain.question.entity.Question;

import java.time.LocalDateTime;
import java.util.Locale;

public record QuestionSummaryResponseDTO(
        Long id,
        String name, //닉네임
        String title,
        LocalDateTime createdAt
) {
    public static QuestionSummaryResponseDTO from(Question question) {
        return new QuestionSummaryResponseDTO(
                question.getId(),
                question.getUser().getName(),
                question.getTitle(),
                question.getCreatedAt()

        );
    }
}
