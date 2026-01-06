package org.example.playground.domain.answer.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AnswerCreateRequestDTO(
        @NotBlank String content
) {
}
