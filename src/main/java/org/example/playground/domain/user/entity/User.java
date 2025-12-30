package org.example.playground.domain.user.entity;

import jakarta.persistence.*;

import lombok.*;
import org.example.playground.domain.user.dto.OAuth2UserInfo;
import org.example.playground.domain.user.dto.UserRegisterRequestDTO;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Builder
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_id"})
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name; // 닉네임

    @Column(name = "login_id", nullable = false, unique = true, length = 100)
    private String loginId;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "joined_date", updatable = false)
    private LocalDateTime joinedDate;

    //소셜 로그인 관련 필드
    @Column(name = "provider", length = 100)
    private String provider; // 소셜 로그인 제공자 (없으면 일반 회원)
    @Column(name = "provider_id", length = 100)
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

    public static User userFromDTO(UserRegisterRequestDTO userDTO, String encodingPW){
        return User.builder()
                .name(userDTO.getName())
                .loginId(userDTO.getLoginId())
                .password(encodingPW)
                .email(userDTO.getEmail())
                .build();
    }

    public static User userFromOAuthUser(OAuth2UserInfo info, PasswordEncoder passwordEncoder) {
        String loginId = info.getProvider() + "_" + info.getProviderId();

        // 소셜 유저는 password 로그인에 쓰지 않으므로 더미 생성 (NOT NULL 만족용)
        String dummyPassword = passwordEncoder.encode(UUID.randomUUID().toString());

        return User.builder()
                .name(info.getName() != null ? "("+ info.getProvider() +")" + info.getName() : info.getProvider() + "_user")
                .loginId(loginId)
                .password(dummyPassword)
                .email(info.getEmail())
                .provider(info.getProvider())
                .providerId(info.getProviderId())
                .build();
    }

    public void addRole(Role role) {
        //한 유저는 같은 Role을 중복으로 가질 수 없다.
        boolean exists = roles.stream()
                .anyMatch(ur -> ur.getRole().getName().equals(role.getName()));

        if (exists) return;

        //User에게 새로운 내역을 갖게 함.
        UserRole userRole = UserRole.of(this, role);
        roles.add(userRole);
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeEmail(String email) {
        this.email = email;
    }
}
