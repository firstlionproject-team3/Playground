package org.example.playground.domain.question.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
//클라가 서버로 보내는 데이터들
public record QuestionCreateRequest(
    @NotNull Long member_id,
    @NotBlank String title,
    @NotBlank String content
) {
}
