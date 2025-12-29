package org.example.playground.domain.notification.service;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.notification.entity.Notification;
import org.example.playground.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationSseService sseService;

    /**
     * 답변 채택 알림 생성 및 전송
     * @param receiver 답변 작성자 (알림 수신자)
     * @param sender 질문 작성자 (채택자)
     * @return 저장된 알림 엔티티
     */
    public Notification notifyAnswerAccepted(User receiver, User sender) {
        Notification notification = Notification.createAnswerAccepted(
                receiver,
                sender,
                "내 답변이 채택되었습니다."
        );
        return sseService.send(notification);
    }

    /**
     * 신고 접수 알림 생성 및 전송 (운영자에게)
     * @param admin 운영자 (알림 수신자)
     * @param reporter 신고자
     * @return 저장된 알림 엔티티
     */
    public Notification notifyReport(User admin, User reporter) {
        Notification notification = Notification.createReport(
                admin,
                reporter,
                "신고가 접수되었습니다."
        );
        return sseService.send(notification);
    }
}
