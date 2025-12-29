package org.example.playground.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.notification.dto.NotificationRequestDTO;
import org.example.playground.domain.notification.dto.NotificationListResponseDTO;
import org.example.playground.domain.notification.dto.NotificationResponseDTO;
import org.example.playground.domain.notification.entity.Notification;
import org.example.playground.domain.notification.entity.NotificationType;
import org.example.playground.domain.notification.repository.NotificationRepository;
import org.example.playground.domain.notification.service.NotificationCommandService;
import org.example.playground.domain.notification.service.NotificationSseService;
import org.example.playground.domain.user.entity.User;
import org.example.playground.domain.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationSseService sseService;
    private final NotificationCommandService commandService;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * 알림 생성 및 전송
     * POST /notification
     */
    @PostMapping
    public ResponseEntity<NotificationResponseDTO> createNotification(
            @RequestBody NotificationRequestDTO request) {
        // 수신자 조회
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("수신자를 찾을 수 없습니다: " + request.getReceiverId()));

        // 발신자 조회
        User sender = null;
        if (request.getSenderId() != null) {
            sender = userRepository.findById(request.getSenderId())
                    .orElseThrow(() -> new IllegalArgumentException("발신자를 찾을 수 없습니다: " + request.getSenderId()));
        }

        // 알림 생성
        Notification notification;
        if (request.getType() == NotificationType.ACCEPTED_ANSWER) {
            notification = Notification.createAnswerAccepted(receiver, sender, request.getContent());
        } else if (request.getType() == NotificationType.REPORT_RECEIVED) {
            notification = Notification.createReport(receiver, sender, request.getContent());
        } else {
            throw new IllegalArgumentException("지원하지 않는 알림 유형입니다: " + request.getType());
        }

        // 알림 저장 및 전송
        Notification savedNotification = sseService.send(notification);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(NotificationResponseDTO.from(savedNotification));
    }

    /**
     * SSE 구독 엔드포인트
     * GET /notification/subscribe?userId={userId}
     */
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam Long userId) {
        // 사용자 존재 여부 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        return sseService.createEmitter(userId);
    }

    /**
     * 알림 목록 조회
     * GET /notification?userId={userId}
     */
    @GetMapping
    public ResponseEntity<NotificationListResponseDTO> getNotifications(@RequestParam Long userId) {
        List<Notification> notifications = notificationRepository
                .findByReceiverIdOrderByCreatedAtDesc(userId);

        List<NotificationResponseDTO> responseList = notifications.stream()
                .map(NotificationResponseDTO::from)
                .collect(Collectors.toList());

        NotificationListResponseDTO response = new NotificationListResponseDTO(
                responseList,
                responseList.size()
        );

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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        commandService.markAsRead(id, user);
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        commandService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}

