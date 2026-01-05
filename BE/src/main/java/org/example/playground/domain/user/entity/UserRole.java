package org.example.playground.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "user_roles",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//유저에게 권한이 있다는 내역을 가진 테이블
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    public static UserRole of(User user, Role role) {
        //새로운 내역 생성하는 팩토리 메서드
        UserRole ur = new UserRole();
        ur.user = user;
        ur.role = role;
        return ur;
    }
}
