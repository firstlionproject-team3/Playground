package org.example.playground.domain.notification.service;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.notification.entity.Notification;
import org.example.playground.domain.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class NotificationSseService {

    private final NotificationRepository notificationRepository;

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    // 알림 저장
    public SseEmitter createEmitter(Long userId) {
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L);
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));

        return emitter;
    }

    // 실시간 전송
    public void send(Notification notification) {
        notificationRepository.save(notification);

        SseEmitter emitter = emitters.get(notification.getReceiverId());
        if (emitter != null) {
            try {
                emitter.send(notification.getContent());
            } catch (IOException e) {
                emitters.remove(notification.getReceiverId());
            }
        }
    }
}
