package org.example.playground.domain.comment.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.playground.domain.comment.entity.Comment;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentResponseDTO {

    private Long id;

    private String nickname;

    private String content;

    private int likeCount;

    private int dislikeCount;

    private String myReactionType; // LIKE | DISLIKE | NONE

    private LocalDateTime createdAt;

    public static CommentResponseDTO from(Comment comment, String myReactionType) {
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .nickname(comment.getUser().getNickname()) // 댓글 개수만큼 쿼리
                .content(comment.getContent())
                .likeCount(comment.getLikeCount())
                .dislikeCount(comment.getDislikeCount())
                .myReactionType(myReactionType != null ? myReactionType : "NONE")
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
