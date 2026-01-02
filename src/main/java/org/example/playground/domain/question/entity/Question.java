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

    //조회수 필드
    @Column(nullable = false)
    private Long viewCount = 0L;

    //신고횟수, 관리자가 판단할때의 근거
    private int reportCount = 0;

    // 채택된 답변 id (없으면 null)
    @Column(name = "acceptedAnswerId")
    private Long acceptedAnswerId;

    //엔티티 저장 전 기본값 보장
    //Builder 사용 시 null이 될 수 있는 필드(createdAt, updatedAt, viewCount)를
    //NOT NULL 제약 위반 없이 안전하게 초기화하기 위한 생명주기 콜백
    @PrePersist
    protected void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
        if (this.viewCount == null) {
            this.viewCount = 0L;
        }
    }

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

    //조회수 증가 로직
    public void increaseViewCount() {
        this.viewCount++;
    }

    //질문 신고 횟수 증가
    public void reportBy(Long reporterId) {
        this.reportCount++;
    }

    public void acceptAnswer(Long answerId, Long requesterId, Long answerWriterId) {
        //질문 작성자만
        if (!this.user.getId().equals(requesterId)) {
            throw new RuntimeException("질문 작성자만 채택할 수 있습니다.");
        }

        //한 질문당 1개
        if (this.acceptedAnswerId != null) {
            throw new RuntimeException("이미 채택된 답변이 있습니다.");
        }

        //자기 답변 채택 금지
        if (requesterId.equals(answerWriterId)) {
            throw new RuntimeException("자기 답변은 채택할 수 없습니다.");
        }

        this.acceptedAnswerId = answerId;
    }

}
