package org.example.playground.domain.notification.service;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.notification.dto.NotificationListResponseDTO;
import org.example.playground.domain.notification.dto.NotificationRequestDTO;
import org.example.playground.domain.notification.dto.NotificationResponseDTO;
import org.example.playground.domain.notification.entity.Notification;
import org.example.playground.domain.notification.entity.NotificationType;
import org.example.playground.domain.notification.exception.*;
import org.example.playground.domain.notification.repository.NotificationRepository;
import org.example.playground.domain.user.entity.User;
import org.example.playground.domain.user.exception.UserNotFoundException;
import org.example.playground.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.example.playground.domain.notification.exception.NotificationErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationSseService sseService;

    /**
     * 알림 생성 및 전송
     * @param request 알림 생성 요청 DTO
     * @return 저장된 알림 DTO
     */
    public NotificationResponseDTO createNotification(NotificationRequestDTO request) {
        // 수신자 조회
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new ReceiverNotFoundException(RECEIVER_NOT_FOUND, request.getReceiverId()));

        // 발신자 조회 (nullable)
        User sender = null;
        if (request.getSenderId() != null) {
            sender = userRepository.findById(request.getSenderId())
                    .orElseThrow(() -> new SenderNotFoundException(SENDER_NOT_FOUND, request.getSenderId()));
        }

        // 알림 생성
        Notification notification = Notification.create(
                request.getType(),
                receiver,
                sender,
                request.getContent()
        );

        // 알림 저장 및 전송
        Notification savedNotification = sseService.send(notification);

        return NotificationResponseDTO.from(savedNotification);
    }

    /**
     * 알림 목록 조회
     * @param userId 사용자 ID
     * @return 알림 목록 DTO
     */
    @Transactional(readOnly = true)
    public NotificationListResponseDTO getNotifications(Long userId) {
        // 사용자 존재 여부 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 회원입니다."));

        List<Notification> notifications = notificationRepository
                .findByReceiverIdOrderByCreatedAtDesc(userId);

        List<NotificationResponseDTO> responseList = notifications.stream()
                .map(NotificationResponseDTO::from)
                .collect(Collectors.toList());

        return new NotificationListResponseDTO(responseList, responseList.size());
    }

    /**
     * 사용자 존재 여부 확인
     * @param userId 사용자 ID
     */
    @Transactional(readOnly = true)
    public void validateUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 회원입니다."));
    }

    /**
     * 사용자 조회
     * @param userId 사용자 ID
     * @return User 엔티티
     */
    @Transactional(readOnly = true)
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 회원입니다."));
    }


    /**
     * 알림 읽음 처리
     * @param notificationId 알림 ID
     * @param user 현재 사용자 (권한 검증용)
     */
    public void markAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(NOTIFICATION_NOT_FOUND, notificationId));

        // 권한 검증: 알림 수신자만 읽음 처리 가능
        if (!notification.getReceiver().getId().equals(user.getId())) {
            throw new UnauthorizedReadAccessException(UNAUTHORIZED_READ_ACCESS);
        }

        notification.markAsRead();

        notificationRepository.save(notification);
    }

    /**
     * 알림 삭제
     * @param notificationId 알림 ID
     * @param user 현재 사용자 (권한 검증용)
     */
    public void delete(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(NOTIFICATION_NOT_FOUND, notificationId));

        // 권한 검증: 알림 수신자만 삭제 가능
        if (!notification.getReceiver().getId().equals(user.getId())) {
            throw new UnauthorizedDeleteAccessException(UNAUTHORIZED_DELETE_ACCESS);
        }

        notificationRepository.delete(notification);
    }
}

