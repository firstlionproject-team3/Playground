package org.example.playground.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.notification.dto.NotificationListResponseDTO;
import org.example.playground.domain.notification.dto.NotificationRequestDTO;
import org.example.playground.domain.notification.dto.NotificationResponseDTO;
import org.example.playground.domain.notification.service.NotificationService;
import org.example.playground.domain.notification.service.NotificationSseService;
import org.example.playground.domain.user.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationSseService sseService;
    private final NotificationService notificationService;

    /**
     * 알림 생성 및 전송
     * POST /notification
     */
    @PostMapping
    public ResponseEntity<NotificationResponseDTO> createNotification(
            @RequestBody NotificationRequestDTO request) {
        NotificationResponseDTO response = notificationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * SSE 구독 엔드포인트
     * GET /notification/subscribe?userId={userId}
     */
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam Long userId) {
        notificationService.validateUser(userId);
        return sseService.createEmitter(userId);
    }

    /**
     * 알림 목록 조회
     * GET /notification?userId={userId}
     */
    @GetMapping
    public ResponseEntity<NotificationListResponseDTO> getNotifications(@RequestParam Long userId) {
        NotificationListResponseDTO response = notificationService.getNotifications(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 알림 읽음 처리
     * PATCH /notification/{id}/read?userId={userId}
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            @RequestParam Long userId) {
        User user = notificationService.findUserById(userId);
        notificationService.markAsRead(id, user);
        return ResponseEntity.noContent().build();
    }

    /**
     * 알림 삭제
     * DELETE /notification/{id}?userId={userId}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Long id,
            @RequestParam Long userId) {
        User user = notificationService.findUserById(userId);
        notificationService.delete(id, user);
        return ResponseEntity.noContent().build();
    }

    /**
     * SSE 연결 종료
     * DELETE /notification/subscribe?userId={userId}
     */
    @DeleteMapping("/subscribe")
    public ResponseEntity<Void> disconnect(@RequestParam Long userId) {
        notificationService.validateUser(userId);
        sseService.disconnect(userId);
        return ResponseEntity.noContent().build();
    }
}

