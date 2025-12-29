package org.example.playground.domain.question.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Column(nullable = false)
    private Long memberId; //임시로 사용, todo 나중에 user객체로 찍어서 필드에서 사용

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
    public static Question create(Long member_id, String title, String content) {
        LocalDateTime now = LocalDateTime.now();

        return Question.builder()
                .memberId(member_id)
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
