package org.example.playground.domain.question.dto.response;

import org.example.playground.domain.answer.dto.response.AnswerSummaryResponseDTO;
import org.example.playground.domain.answer.entity.Answer;
import org.example.playground.domain.question.entity.Question;

import java.time.LocalDateTime;
import java.util.List;

public record QuestionDetailResponseDTO(
        Long id,
        String nickname,
        String title,
        String content,
        Long viewCount,
        List<AnswerSummaryResponseDTO> answers,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static QuestionDetailResponseDTO from(Question question, List<Answer> answers) {
        return new QuestionDetailResponseDTO(
                question.getId(),
                question.getUser().getNickname(),
                question.getTitle(),
                question.getContent(),
                question.getViewCount(),
                answers.stream()
                        .map(AnswerSummaryResponseDTO::from)
                        .toList(),
                question.getCreatedAt(),
                question.getUpdatedAt()
        );
    }

    public static QuestionDetailResponseDTO from(Question question) {
        return new QuestionDetailResponseDTO(
                question.getId(),
                question.getUser().getNickname(),
                question.getTitle(),
                question.getContent(),
                question.getViewCount(),
                List.of(), // 답변 없음
                question.getCreatedAt(),
                question.getUpdatedAt()
        );
    }

}
