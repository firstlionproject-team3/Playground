package org.example.playground.domain.question.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
//클라가 서버로 보내는 데이터들
public record QuestionCreateRequestDTO(
        //작성자는 security에서 결정
    @NotBlank String title,
    @NotBlank String content
) {
}
