package org.example.playground.domain.refreshtoken.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private Date expiredAt;

    public static RefreshToken from(Long userId, String token, Date expiredAt) {
        return RefreshToken.builder()
                .userId(userId)
                .token(token)
                .expiredAt(expiredAt)
                .build();
    }
}
