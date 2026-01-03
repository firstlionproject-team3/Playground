package org.example.playground.domain.reaction.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.playground.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
@Table(
        name = "reaction",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"user_id", "target_type", "target_id"}
                )
        }
)
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private TargetType targetType; // QUESTION, ANSWER, COMMENT

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false)
    private ReactionType reactionType; // LIKE, DISLIKE

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 추천/비추천 생성
    public static Reaction create(User user, TargetType targetType, Long targetId, ReactionType reactionType) {
        LocalDateTime now = LocalDateTime.now();
        return Reaction.builder()
                .user(user)
                .targetType(targetType)
                .targetId(targetId)
                .reactionType(reactionType)
                .createdAt(now)
                .build();
    }

    // 추천/비추천 변경 (추천 -> 비추천, 비추천 -> 추천)
    public void update(ReactionType reactionType) {
        this.reactionType = reactionType;
    }
}