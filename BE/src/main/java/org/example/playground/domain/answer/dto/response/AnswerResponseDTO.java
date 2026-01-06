package org.example.playground.domain.answer.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.playground.domain.answer.entity.Answer;
import org.example.playground.domain.comment.dto.response.CommentResponseDTO;
import org.example.playground.domain.comment.entity.Comment;

import java.util.List;
import java.util.Map;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AnswerResponseDTO {

    private Long id;
    private Long userId;
    private String content;
    private String nickname;
    private int likeCount;
    private int dislikeCount;
    private String myReactionType; // LIKE | DISLIKE | NONE
    private List<CommentResponseDTO> comments;

    public static AnswerResponseDTO from(
            Answer answer, 
            List<Comment> comments,
            String myReactionType,
            Map<Long, String> commentMyReactionMap
    ) {
        return AnswerResponseDTO.builder()
                .id(answer.getId())
                .userId(answer.getUser().getId())
                .content(answer.getContent())
                .nickname(answer.getUser().getNickname()) // 답변 개수만큼 쿼리
                .likeCount(answer.getLikeCount())
                .dislikeCount(answer.getDislikeCount())
                .myReactionType(myReactionType != null ? myReactionType : "NONE")
                .comments(comments.stream()
                        .map(c -> CommentResponseDTO.from(c, commentMyReactionMap.getOrDefault(c.getId(), "NONE")))
                        .toList())
                .build();
    }
}
