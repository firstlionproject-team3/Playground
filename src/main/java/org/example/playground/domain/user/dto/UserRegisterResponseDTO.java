package org.example.playground.domain.user.dto;

import lombok.*;
import org.example.playground.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
//회원가입 성공했을때 결과 출력용
public class UserRegisterResponseDTO {
    private String name;
    private String loginId;
    private String email;
    private LocalDateTime joinedDate;
    private Set<String> roles;

    public static UserRegisterResponseDTO userRegisterResponseDTOfromEntity(User user){
        return UserRegisterResponseDTO.builder()
                .name(user.getName())
                .loginId(user.getLoginId())
                .email(user.getEmail())
                .joinedDate(user.getJoinedDate())
                .roles(user.getRoles().stream()
                        .map(ur -> ur.getRole().getName())
                        .collect(Collectors.toSet()))
                .build();
    }
}