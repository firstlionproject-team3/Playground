package org.example.playground.domain.notification.service;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.notification.entity.Notification;
import org.example.playground.domain.notification.repository.NotificationRepository;
import org.example.playground.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationCommandService {

    private final NotificationRepository notificationRepository;

    /**
     * 알림 읽음 처리
     * @param notificationId 알림 ID
     * @param user 현재 사용자 (권한 검증용)
     */
    public void markAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다: " + notificationId));

        // 권한 검증: 알림 수신자만 읽음 처리 가능
        if (!notification.getReceiver().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인의 알림만 읽음 처리할 수 있습니다.");
        }

        notification.markAsRead();
    }

    /**
     * 알림 삭제
     * @param notificationId 알림 ID
     * @param user 현재 사용자 (권한 검증용)
     */
    public void delete(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다: " + notificationId));

        // 권한 검증: 알림 수신자만 삭제 가능
        if (!notification.getReceiver().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인의 알림만 삭제할 수 있습니다.");
        }

        notificationRepository.delete(notification);
    }
}

