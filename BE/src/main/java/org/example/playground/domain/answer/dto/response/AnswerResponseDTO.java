package org.example.playground.domain.answer.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.playground.domain.answer.entity.Answer;
import org.example.playground.domain.comment.dto.response.CommentResponseDTO;
import org.example.playground.domain.comment.entity.Comment;

import java.util.List;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AnswerResponseDTO {

    private Long id;
    private Long userId;
    private String content;
    private String nickname;
    private boolean accepted;
    private int likeCount;
    private int dislikeCount;
    private List<CommentResponseDTO> comments;

    public static AnswerResponseDTO from(Answer answer, List<Comment> comments) {
        return AnswerResponseDTO.builder()
                .id(answer.getId())
                .userId(answer.getUser().getId())
                .content(answer.getContent())
                .nickname(answer.getUser().getNickname()) // 답변 개수만큼 쿼리
                .accepted(answer.isAccepted())
                .likeCount(answer.getLikeCount())
                .dislikeCount(answer.getDislikeCount())
                .comments(comments.stream()
                        .map(CommentResponseDTO::from)
                        .toList())
                .build();
    }
}
