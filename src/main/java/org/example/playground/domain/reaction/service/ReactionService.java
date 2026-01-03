package org.example.playground.domain.reaction.service;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.reaction.dto.ReactionRequestDto;
import org.example.playground.domain.reaction.entity.Reaction;
import org.example.playground.domain.reaction.entity.ReactionType;
import org.example.playground.domain.reaction.entity.TargetType;
import org.example.playground.domain.reaction.repository.ReactionRepository;
import org.example.playground.domain.user.entity.User;
import org.example.playground.domain.user.exception.UserNotFoundException;
import org.example.playground.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final UserRepository userRepository;

    // 추천/비추천 생성 또는 변경
    @Transactional
    public void toggleReaction(Long userId, ReactionRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("userID=" + userId));

        // 기존 추천/비추천이 있는지 확인
        Optional<Reaction> existingReaction = reactionRepository.findByUserIdAndTargetTypeAndTargetId(
                userId,
                request.targetType(),
                request.targetId()
        );

        if (existingReaction.isPresent()) {
            Reaction reaction = existingReaction.get();
            // 같은 타입이면 삭제, 다른 타입이면 변경
            if (reaction.getReactionType().equals(request.reactionType())) {
                reactionRepository.delete(reaction);
            } else {
                reaction.update(request.reactionType());
            }
        } else {
            // 새로운 추천/비추천 생성
            Reaction reaction = Reaction.create(
                    user,
                    request.targetType(),
                    request.targetId(),
                    request.reactionType()
            );
            reactionRepository.save(reaction);
        }
    }

    // 특정 대상의 추천 개수 조회
    @Transactional(readOnly = true)
    public long getLikeCount(TargetType targetType, Long targetId) {
        return reactionRepository.countByTargetTypeAndTargetIdAndReactionType(
                targetType,
                targetId,
                ReactionType.LIKE
        );
    }

    // 특정 대상의 비추천 개수 조회
    @Transactional(readOnly = true)
    public long getDislikeCount(TargetType targetType, Long targetId) {
        return reactionRepository.countByTargetTypeAndTargetIdAndReactionType(
                targetType,
                targetId,
                ReactionType.DISLIKE
        );
    }

    // 사용자가 특정 대상에 추천/비추천을 남겼는지 확인
    @Transactional(readOnly = true)
    public boolean hasReaction(Long userId, TargetType targetType, Long targetId) {
        return reactionRepository.existsByUser_IdAndTargetTypeAndTargetId(
                userId,
                targetType,
                targetId
        );
    }

    // 사용자가 특정 대상에 남긴 추천/비추천 타입 조회
    @Transactional(readOnly = true)
    public Optional<ReactionType> getUserReactionType(Long userId, TargetType targetType, Long targetId) {
        return reactionRepository.findByUserIdAndTargetTypeAndTargetId(
                userId,
                targetType,
                targetId
        ).map(Reaction::getReactionType);
    }

    // 추천/비추천 삭제
    @Transactional
    public void deleteReaction(Long userId, TargetType targetType, Long targetId) {
        Reaction reaction = reactionRepository.findByUserIdAndTargetTypeAndTargetId(
                userId,
                targetType,
                targetId
        ).orElseThrow(() -> new RuntimeException("추천/비추천을 찾을 수 없습니다."));

        reactionRepository.delete(reaction);
    }
}

