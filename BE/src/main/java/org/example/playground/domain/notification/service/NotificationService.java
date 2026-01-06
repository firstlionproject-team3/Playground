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
import org.example.playground.domain.user.entity.UserStatus;
import org.example.playground.domain.user.exception.UserNotFoundException;
import org.example.playground.domain.user.repository.UserRepository;
import org.example.playground.global.exception.BusinessException;
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
                .orElseThrow(() -> new BusinessException(
                        RECEIVER_NOT_FOUND,
                        "수신자를 찾을 수 없습니다. receiverId = " + request.getReceiverId()
                ));

        // 발신자 조회 (nullable)
        User sender = null;
        if (request.getSenderId() != null) {
            sender = userRepository.findById(request.getSenderId())
                    .orElseThrow(() -> new BusinessException(
                            SENDER_NOT_FOUND,
                            "발신자를 찾을 수 없습니다. senderId = " + request.getSenderId()
                    ));
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
                .orElseThrow(() -> new BusinessException(
                        RECEIVER_NOT_FOUND,
                        "존재하지 않는 회원입니다. userId = " + userId
                ));

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
                .orElseThrow(() -> new BusinessException(
                        RECEIVER_NOT_FOUND,
                        "존재하지 않는 회원입니다. userId = " + userId
                ));
    }

    /**
     * 사용자 조회
     * @param userId 사용자 ID
     * @return User 엔티티
     */
    @Transactional(readOnly = true)
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        RECEIVER_NOT_FOUND,
                        "존재하지 않는 회원입니다. userId = " + userId
                ));
    }


    /**
     * 알림 읽음 처리
     * @param notificationId 알림 ID
     * @param user 현재 사용자 (권한 검증용)
     */
    public void markAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException(
                        NOTIFICATION_NOT_FOUND,
                        "알림을 찾을 수 없습니다. notificationId = " + notificationId
                ));

        // 권한 검증: 알림 수신자만 읽음 처리 가능
        if (!notification.getReceiver().getId().equals(user.getId())) {
            throw new BusinessException(UNAUTHORIZED_READ_ACCESS);
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
                .orElseThrow(() -> new BusinessException(
                        NOTIFICATION_NOT_FOUND,
                        "알림을 찾을 수 없습니다. notificationId = " + notificationId
                ));

        // 권한 검증: 알림 수신자만 삭제 가능
        if (!notification.getReceiver().getId().equals(user.getId())) {
            throw new BusinessException(UNAUTHORIZED_DELETE_ACCESS);
        }

        notificationRepository.delete(notification);
    }

    /**
     * 모든 관리자에게 알림 전송 (SSE)
     * @param type 알림 유형
     * @param sender 발신자 (nullable)
     * @param content 알림 내용
     * @return 생성된 알림 목록
     */
    public List<NotificationResponseDTO> sendToAllAdmins(NotificationType type, User sender, String content) {
        // ACTIVE 상태인 관리자 ID 목록 조회
        List<Long> adminIds = userRepository.findActiveAdminIds(
                UserStatus.ACTIVE,
                "ROLE_ADMIN"
        );

        // 각 관리자에게 알림 생성
        List<Notification> notifications = adminIds.stream()
                .map(adminId -> {
                    User admin = userRepository.getReferenceById(adminId);
                    return Notification.create(type, admin, sender, content);
                })
                .toList();

        // DB 일괄 저장
        List<Notification> savedNotifications = notificationRepository.saveAll(notifications);

        // SSE로 실시간 전송
        savedNotifications.forEach(sseService::send);

        // DTO로 변환하여 반환
        return savedNotifications.stream()
                .map(NotificationResponseDTO::from)
                .collect(Collectors.toList());
    }
}

