package org.example.playground.domain.reaction.repository;

import org.example.playground.domain.reaction.entity.Reaction;
import org.example.playground.domain.reaction.entity.ReactionType;
import org.example.playground.domain.reaction.entity.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    // 특정 사용자가 특정 대상에 대해 작성한 추천/비추천 조회
    Optional<Reaction> findByUserIdAndTargetTypeAndTargetId(Long userId, TargetType targetType, Long targetId);

    // 특정 대상에 대한 추천/비추천 개수 조회
    long countByTargetTypeAndTargetIdAndReactionType(TargetType targetType, Long targetId, ReactionType reactionType);

    // 특정 사용자가 특정 대상에 대해 추천/비추천을 남겼는지 확인
    boolean existsByUser_IdAndTargetTypeAndTargetId(Long userId, TargetType targetType, Long targetId);

    // 여러 대상에 대한 추천/비추천 조회 (N+1 문제 방지)
    List<Reaction> findAllByTargetTypeAndTargetIdIn(TargetType targetType, List<Long> targetIds);

    // 특정 사용자가 여러 대상에 대해 작성한 추천/비추천 조회 (N+1 문제 방지)
    List<Reaction> findAllByUser_IdAndTargetTypeAndTargetIdIn(Long userId, TargetType targetType, List<Long> targetIds);
}

