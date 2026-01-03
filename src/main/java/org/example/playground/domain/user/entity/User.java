package org.example.playground.domain.user.entity;

import jakarta.persistence.*;

import lombok.*;
import org.example.playground.domain.answer.entity.Answer;
import org.example.playground.domain.question.entity.Question;
import org.example.playground.domain.user.dto.OAuth2UserInfo;
import org.example.playground.domain.user.dto.UserRegisterRequestDTO;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Builder
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_provider_providerId", columnNames = {"provider", "provider_id"}),
                @UniqueConstraint(name= "uk_user_login_id", columnNames = {"login_id"}),
                @UniqueConstraint(name = "uk_user_nickname", columnNames = {"nickname"})
        }
)
@SQLDelete(sql = "UPDATE users SET status='DELETED', deleted_at=now() WHERE id=?") // userRepository.delete시 update가 나감.
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", nullable = false, length = 100)
    private String nickname; // 닉네임

    @Column(name = "login_id", nullable = false)
    private String loginId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "joined_date", updatable = false)
    private LocalDateTime joinedDate;

    @Column(name = "current_points", nullable = false)
    private long currentPoints = 0L;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    private LocalDateTime deletedAt;

    public boolean isDeleted() {
        return status == UserStatus.DELETED;
    }
    //소프트 삭제 후 로그인 불가능!
    public void softDeleteAndAnonymize() {
        this.status = UserStatus.DELETED;
        this.deletedAt = LocalDateTime.now();

        this.nickname = "deleted#" + this.nickname;
        this.loginId = "deleted_" + this.id; // 재로그인 방지
        this.email = null;
        this.providerId = "deleted_" + this.providerId; // 재로그인 방지
    }

    //소셜 로그인 관련 필드
    @Column(name = "provider")
    private String provider; // 소셜 로그인 제공자 (없으면 일반 회원)
    @Column(name = "provider_id")
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

    public static User createLocalUser(UserRegisterRequestDTO userDTO, String nickname, String encodingPW){
        return User.builder()
                .nickname(nickname)
                .loginId(userDTO.getLoginId())
                .password(encodingPW)
                .email(userDTO.getEmail())
                .build();
    }

    public static User createOAuthUser(OAuth2UserInfo info,String loginId, String nickname, String encodingPW) {

        return User.builder()
                .nickname(nickname)
                .loginId(loginId)
                .password(encodingPW)
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

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changeEmail(String email) {
        this.email = email;
    }
}
