package org.example.playground.domain.answer.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.playground.domain.answer.entity.Answer;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AnswerDetailResponseDTO2 {

    Long id;

    String nickname;

    String content;

    boolean accepted;

    public static AnswerDetailResponseDTO2 from(Answer answer) {
        return AnswerDetailResponseDTO2.builder()
                .id(answer.getId())
                .nickname(answer.getContent())
                .content(answer.getContent())
                .accepted(answer.isAccepted())
                .build();
    }
}
