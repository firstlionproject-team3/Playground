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
    private Long member_id;

    @Column(nullable = false, length = 255)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime created_at;

    @Column(nullable = false)
    private LocalDateTime updated_at;

    public static Question create(Long member_id, String title, String content) {
        LocalDateTime now = LocalDateTime.now();

        return Question.builder()
                .member_id(member_id)
                .title(title)
                .content(content)
                .created_at(now)
                .updated_at(now)
                .build();
    }

    public void update(String title, String content) {
        if(title != null && !title.isBlank()) {
            this.title = title;
        }
        if(content != null && !content.isBlank()) {
            this.content = content;
        } this.updated_at = LocalDateTime.now();
    }


}
