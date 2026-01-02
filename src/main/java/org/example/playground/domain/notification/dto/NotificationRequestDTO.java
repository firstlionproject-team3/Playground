package org.example.playground.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.playground.domain.notification.entity.NotificationType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDTO {
    private Long receiverId;  // 알림 수신자 ID
    private Long senderId;    // 알림 발생자 ID
    private NotificationType type;  // 알림 유형
    private String content;   // 알림 내용
}

