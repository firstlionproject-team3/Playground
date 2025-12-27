package org.example.playground.domain.notification.dto;

import lombok.Getter;
import org.example.playground.domain.notification.entity.Notification;
import org.example.playground.domain.notification.entity.NotificationType;

import java.time.LocalDateTime;

@Getter
public class NotificationResponseDTO {
    private Long id;
    private NotificationType type;
    private String content;
    private Long senderId;
    private Long receiverId;
    private boolean isRead;
    private LocalDateTime createdAt;

    public NotificationResponseDTO(Notification notification) {
        this.id = notification.getId();
        this.type = notification.getType();
        this.content = notification.getContent();
        this.senderId = notification.getSender() != null
                ? notification.getSender().getId()
                : null;
        this.receiverId = notification.getReceiver().getId();
        this.isRead = notification.isRead();
        this.createdAt = notification.getCreatedAt();
    }
}
