package org.example.playground.domain.notification.service;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.notification.entity.Notification;
import org.example.playground.domain.notification.entity.NotificationType;
import org.example.playground.domain.user.entity.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationSseService sseService;

    /* 답변 채택 알림 */
    public void notifyAnswerAccepted(User receiver, User sender) {
        Notification notification = Notification.create(
                receiver,
                sender,
                NotificationType.ACCEPTED_ANSWER,
                "내 답변이 채택되었습니다."
        );
        sseService.send(notification);
    }

    /* 신고 접수 알림 (운영자) */
    public void notifyReport(User admin, User reporter) {
        Notification notification = Notification.create(
                admin,
                reporter,
                NotificationType.REPORT_RECEIVED,
                "신고가 접수되었습니다."
        );
        sseService.send(notification);
    }
}
