package org.example.playground.domain.answer.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.playground.domain.question.entity.Question;
import org.example.playground.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access =  AccessLevel.PROTECTED)
@Table(name = "answer")
public class Answer {

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

    // 답변 생성
    public static Answer create(Question question, User user, String content) {
        LocalDateTime now = LocalDateTime.now();
        return Answer.builder()
                .question(question)
                .user(user)
                .content(content)
                .createdAt(now)
                .updatedAt(now)
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

    
    //답변 채택 해제
    public void unaccept() {
        this.accepted = false;
    }

}

