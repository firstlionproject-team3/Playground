package org.example.playground.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationListResponseDTO {
    private List<NotificationResponseDTO> notifications;
    private long totalCount;
}

