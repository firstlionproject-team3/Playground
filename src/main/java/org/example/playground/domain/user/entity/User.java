package org.example.playground.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.playground.domain.user.dto.UserDTO;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    private String provider; // 소셜 로그인 제공자 (없으면 일반 회원)
    private String providerId; // 소셜 계정과 연결될 때 쓰는 ID

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> roles = new HashSet<>();

    //“저장하기 전에 딱 한 번, 하고 싶은 행동 있어?”
    //“registrationDate 안 들어있으면 지금 시간 넣어줘.”
    @PrePersist
    public void prePersist() {
        if(this.joinedDate == null){
            this.joinedDate = LocalDateTime.now();
        }
    }

    public static User userFromDTO(UserDTO userDTO, String encodingPW){
        return User.builder()
                .name(userDTO.getName())
                .loginId(userDTO.getLoginId())
                .password(encodingPW)
                .email(userDTO.getEmail())
                .build();
    }

    public void addRole(Role role) {
        //User에게 새로운 내역을 갖게 함.
        UserRole userRole = UserRole.of(this, role);
        roles.add(userRole);
    }
}
