package org.example.playground.domain.user.dto;

import lombok.*;
import org.example.playground.domain.user.entity.User;
import org.example.playground.domain.user.entity.UserRole;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
//회원가입 성공했을때 결과 출력용
public class UserRegisterSuccessDTO {
    private Long id;
    private String name;
    private String loginId;
    private String email;
    private LocalDateTime joinedDate;
    private Set<UserRole> roles;

    public static UserRegisterSuccessDTO userRegisterSuccessDTOfromEntity(User user){
        return UserRegisterSuccessDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .loginId(user.getLoginId())
                .email(user.getEmail())
                .joinedDate(user.getJoinedDate())
                .roles(user.getRoles())
                .build();
    }
}