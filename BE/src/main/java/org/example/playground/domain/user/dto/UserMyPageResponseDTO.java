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
//마이페이지 용 자세한 정보 조회
public class UserMyPageResponseDTO {
    private String nickname;
    private String email;
    private LocalDateTime joinedDate;

    public static UserMyPageResponseDTO userMyPageDTOFromEntity(User user){
        return UserMyPageResponseDTO.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .joinedDate(user.getJoinedDate())
                .build();
    }
}
