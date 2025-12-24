package org.example.playground.domain.notification.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 수신자
    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    // 발신자
    @Column(name = "sender_id", nullable = false)
    private Long senderId;

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
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
