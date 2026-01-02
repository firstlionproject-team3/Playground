package org.example.playground.domain.question.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.playground.domain.answer.entity.Answer;
import org.example.playground.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(
            mappedBy = "question",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    private List<Answer> answers = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    //질문 생성
    public static Question create(User user, String title, String content) {
        LocalDateTime now = LocalDateTime.now();

        return Question.builder()
                .user(user)
                .title(title)
                .content(content)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    //질문 수정
    public void update(String title, String content) {
        if(title != null && !title.isBlank()) {
            this.title = title;
        }
        if(content != null && !content.isBlank()) {
            this.content = content;
        } this.updatedAt = LocalDateTime.now();
    }


}
