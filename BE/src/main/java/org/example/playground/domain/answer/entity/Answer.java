package org.example.playground.domain.answer.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.playground.domain.comment.entity.Comment;
import org.example.playground.domain.question.entity.Question;
import org.example.playground.domain.reaction.entity.ReactionCountable;
import org.example.playground.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access =  AccessLevel.PROTECTED)
@Table(name = "answer")
public class Answer implements ReactionCountable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 질문의 답변인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionId", nullable = false)
    private Question question;

    // 누가 썼는지 (users.id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    //채택여부
    @Column(nullable = false)
    private boolean accepted;

    //삭제여부
    @Column(nullable = false)
    private boolean deleted;

    //신고횟수, 관리자가 판단할때의 근거
    private int reportCount = 0;

    private int likeCount;

    private int dislikeCount;

    // 답변 생성
    public static Answer create(Question question, User user, String content) {
        LocalDateTime now = LocalDateTime.now();
        return Answer.builder()
                .question(question)
                .user(user)
                .content(content)
                .createdAt(now)
                .updatedAt(now)
                .accepted(false)
                .deleted(false)
                .build();
    }

    // 답변 수정
    public void update(String content) {
        if (content != null && !content.isBlank()) {
            this.content = content;
        }
        this.updatedAt = LocalDateTime.now();
    }

    //답변 채택
    public void accept() {
        this.accepted = true;
    }

    //소프트삭제
    public void softDeleteByAdmin() {
        this.deleted = true;
        this.content = "관리자에 의해 삭제된 답변입니다.";
        this.updatedAt = LocalDateTime.now();
    }

    //질문 신고 횟수 증가
    public void reportBy(Long reporterId) {
        this.reportCount++;
    }


    @Override
    public void increaseLike() {
        likeCount++;
    }

    @Override
    public void decreaseLike() {
        likeCount--;
    }

    @Override
    public void increaseDislike() {
        dislikeCount++;
    }

    @Override
    public void decreaseDislike() {
        dislikeCount--;
    }
}

