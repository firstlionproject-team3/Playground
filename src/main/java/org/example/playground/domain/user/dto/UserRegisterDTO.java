package org.example.playground.domain.user.dto;

import lombok.*;
import org.example.playground.domain.user.entity.Role;
import org.example.playground.domain.user.entity.User;
import org.example.playground.domain.user.entity.UserRole;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
//회원가입 성공했을때 결과 출력용
// 유저 상세 정보 보기?
public class UserRegisterDTO {
    private Long id;
    private String name;
    private String loginId;
    private String email;
    private LocalDateTime joinedDate;
    private Set<UserRole> roles;

    public static UserRegisterDTO userRegisterDTOfromEntity(User user){
        return UserRegisterDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .loginId(user.getLoginId())
                .email(user.getEmail())
                .joinedDate(user.getJoinedDate())
                .roles(user.getRoles())
                .build();
    }
}