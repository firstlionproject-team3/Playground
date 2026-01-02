package org.example.playground.domain.comment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.playground.domain.answer.entity.Answer;
import org.example.playground.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    // 회원(1) : 댓글(N)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 답변(1) : 댓글(N)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id",nullable = false)
    private Answer answer;

    public static Comment create(String content, User user, Answer answer) {
        return Comment.builder()
                .content(content)
                .user(user)
                .answer(answer)
                .build();
    }

    public void update(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }
}
