package org.example.playground.domain.user.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.playground.domain.user.entity.User;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
public class ForAdminDTO {
    //관리자가 보기 편한 정보만 모은 dto
    private Long id;
    private String loginId;
    private String nickname;
    private String provider;
    private LocalDateTime joinedDate;

    public static ForAdminDTO forAdminDTOFromEntity(User user){
        return ForAdminDTO.builder()
                .id(user.getId())
                .loginId(user.getLoginId())
                .nickname(user.getNickname())
                .provider(user.getProvider())
                .joinedDate(user.getJoinedDate())
                .build();
    }
}
