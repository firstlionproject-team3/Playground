package org.example.playground.domain.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.playground.domain.user.entity.User;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알림 수신자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    // 알림 발생자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = true)
    private User sender;

    // 알림 유형
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private NotificationType type;

    // 알림 내용
    @Column(name = "content", length = 255, nullable = false)
    private String content;

    // 읽음 여부
    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    // 생성일
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 정적 팩토리 메서드: 답변 채택 알림
    public static Notification createAnswerAccepted(User receiver, User sender, String content) {
        return Notification.builder()
                .receiver(receiver)
                .sender(sender)
                .type(NotificationType.ACCEPTED_ANSWER)
                .content(content)
                .isRead(false)
                .build();
    }

    // 정적 팩토리 메서드: 신고 알림
    public static Notification createReport(User receiver, User sender, String content) {
        return Notification.builder()
                .receiver(receiver)
                .sender(sender)
                .type(NotificationType.REPORT_RECEIVED)
                .content(content)
                .isRead(false)
                .build();
    }

    // 읽음 처리
    public void markAsRead() {
        this.isRead = true;
    }
}
