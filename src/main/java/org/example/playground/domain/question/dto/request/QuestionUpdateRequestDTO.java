package org.example.playground.domain.question.dto.request;
//클라가 서버로 보내는 수정가능한 데이터들
public record QuestionUpdateRequestDTO(
    String title,
    String content
) {
}
