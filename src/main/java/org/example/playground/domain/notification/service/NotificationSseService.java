package org.example.playground.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.playground.domain.notification.dto.NotificationResponseDTO;
import org.example.playground.domain.notification.entity.Notification;
import org.example.playground.domain.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationSseService {

    private final NotificationRepository notificationRepository;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * SSE 연결 생성
     *
     * @param userId 사용자 ID
     * @return SseEmitter
     */
    public SseEmitter createEmitter(Long userId) {
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 1시간 타임아웃
        emitters.put(userId, emitter);

        // SSE 연결 완료
        emitter.onCompletion(() -> {
            emitters.remove(userId);
        });
        // SSE 연결 타임아웃
        emitter.onTimeout(() -> {
            emitters.remove(userId);
        });
        // SSE 연결 오류
        emitter.onError((ex) -> {
            emitters.remove(userId);
        });

        // 연결 확인 메시지 전송
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("SSE 연결이 성공했습니다."));
        } catch (IOException e) {
            emitters.remove(userId);
        }

        return emitter;
    }

    /**
     * 알림 저장 및 실시간 전송
     *
     * @param notification 알림 엔티티
     * @return 저장된 알림 엔티티
     */
    public Notification send(Notification notification) {
        // DB에 저장
        Notification savedNotification = notificationRepository.save(notification);

        // DTO로 변환
        NotificationResponseDTO response = NotificationResponseDTO.from(savedNotification);

        // 구독 중인 클라이언트에게 전송
        SseEmitter emitter = emitters.get(notification.getReceiver().getId());
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(response));
            } catch (IOException e) {
                emitters.remove(notification.getReceiver().getId());
            }
        }

        return savedNotification;
    }
}
