package org.example.playground.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.playground.domain.user.dto.UserDTO;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Builder
@Table(name = "member")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "login_id", nullable = false, unique = true, length = 20)
    private String loginId;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @CreationTimestamp
    @Column(name = "joined_date", updatable = false)
    private LocalDateTime joinedDate;

    //소셜 로그인 관련 필드
    private String provider;

    private String providerId;

    public static User userFromDTO(UserDTO userDTO, String encodingPW){
        return User.builder()
                .name(userDTO.getName())
                .loginId(userDTO.getLoginId())
                .password(encodingPW)
                .email(userDTO.getEmail())
                .build();
    }
}
