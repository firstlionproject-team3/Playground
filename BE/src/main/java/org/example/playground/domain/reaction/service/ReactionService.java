package org.example.playground.domain.reaction.service;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.reaction.dto.ReactionRequestDto;
import org.example.playground.domain.reaction.entity.Reaction;
import org.example.playground.domain.reaction.entity.ReactionCountable;
import org.example.playground.domain.reaction.entity.ReactionType;
import org.example.playground.domain.reaction.entity.TargetType;
import org.example.playground.domain.reaction.repository.ReactionRepository;
import org.example.playground.domain.user.entity.User;
import org.example.playground.domain.user.exception.UserNotFoundException;
import org.example.playground.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final UserRepository userRepository;

    private final ReactionTargetFinder reactionTargetFinder;

    // 추천/비추천 생성 또는 변경
//    @Transactional
//    public void toggleReaction(Long userId, ReactionRequestDto request) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new UserNotFoundException("userID=" + userId));
//
//        // 기존 추천/비추천이 있는지 확인
//        Optional<Reaction> existingReaction = reactionRepository.findByUserIdAndTargetTypeAndTargetId(
//                userId,
//                request.targetType(),
//                request.targetId()
//        );
//
//        if (existingReaction.isPresent()) {
//            Reaction reaction = existingReaction.get();
//            // 같은 타입이면 삭제, 다른 타입이면 변경
//            if (reaction.getReactionType().equals(request.reactionType())) {
//                reactionRepository.delete(reaction);
//            } else {
//                reaction.update(request.reactionType());
//            }
//        } else {
//            // 새로운 추천/비추천 생성
//            Reaction reaction = Reaction.create(
//                    user,
//                    request.targetType(),
//                    request.targetId(),
//                    request.reactionType()
//            );
//            reactionRepository.save(reaction);
//        }
//    }

    @Transactional
    public void toggleReaction(Long userId, ReactionRequestDto request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("userID=" + userId));

        ReactionCountable target =
                reactionTargetFinder.find(request.targetType(), request.targetId());

        Optional<Reaction> existingReaction =
                reactionRepository.findByUserIdAndTargetTypeAndTargetId(
                        userId,
                        request.targetType(),
                        request.targetId()
                );

        if (existingReaction.isPresent()) {
            Reaction reaction = existingReaction.get();

            if (reaction.getReactionType() == request.reactionType()) {
                reactionRepository.delete(reaction);

                if (reaction.getReactionType() == ReactionType.LIKE) {
                    target.decreaseLike();
                } else {
                    target.decreaseDislike();
                }

            } else {
                if (reaction.getReactionType() == ReactionType.LIKE) {
                    target.decreaseLike();
                    target.increaseDislike();
                } else {
                    target.decreaseDislike();
                    target.increaseLike();
                }

                reaction.update(request.reactionType());
            }

        } else {
            Reaction reaction = Reaction.create(
                    user,
                    request.targetType(),
                    request.targetId(),
                    request.reactionType()
            );
            reactionRepository.save(reaction);

            if (request.reactionType() == ReactionType.LIKE) {
                target.increaseLike();
            } else {
                target.increaseDislike();
            }
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

    // 여러 대상의 추천 수 맵 조회 (N+1 문제 방지)
    // 반환값 예시:
    // {
    //   1L: 5L,   // 질문 ID 1번은 추천 5개
    //   2L: 3L,   // 질문 ID 2번은 추천 3개
    //   3L: 0L    // 질문 ID 3번은 추천 0개 (반응이 없으면 Map에 포함되지 않을 수도 있음)
    // }
    @Transactional(readOnly = true)
    public Map<Long, Long> getLikeCountMap(TargetType targetType, List<Long> targetIds) {
        if (targetIds == null || targetIds.isEmpty()) {
            return Map.of();
        }

        List<Reaction> reactions = reactionRepository.findAllByTargetTypeAndTargetIdIn(targetType, targetIds);
        return reactions.stream()
                .filter(reaction -> reaction.getReactionType() == ReactionType.LIKE)
                .collect(Collectors.groupingBy(
                        Reaction::getTargetId,
                        Collectors.counting()
                ));
    }

    // 여러 대상의 비추천 수 맵 조회 (N+1 문제 방지)
    // 반환값 예시:
    // {
    //   10L: 2L,  // 답변 ID 10번은 비추천 2개
    //   11L: 0L,  // 답변 ID 11번은 비추천 0개
    //   12L: 1L   // 답변 ID 12번은 비추천 1개
    // }
    @Transactional(readOnly = true)
    public Map<Long, Long> getDislikeCountMap(TargetType targetType, List<Long> targetIds) {
        if (targetIds == null || targetIds.isEmpty()) {
            return Map.of();
        }

        List<Reaction> reactions = reactionRepository.findAllByTargetTypeAndTargetIdIn(targetType, targetIds);
        return reactions.stream()
                .filter(reaction -> reaction.getReactionType() == ReactionType.DISLIKE)
                .collect(Collectors.groupingBy(
                        Reaction::getTargetId,
                        Collectors.counting()
                ));
    }

    // 특정 사용자가 여러 대상에 대해 남긴 추천/비추천 타입 맵 조회 (N+1 문제 방지)
    // 반환값 예시:
    // {
    //   20L: ReactionType.LIKE,    // 사용자가 댓글 20번에 추천을 남김
    //   21L: ReactionType.DISLIKE, // 사용자가 댓글 21번에 비추천을 남김
    //   22L: (없음)                // 사용자가 댓글 22번에 반응하지 않음 (Map에 포함되지 않음)
    // }
    @Transactional(readOnly = true)
    public Map<Long, ReactionType> getMyReactionMap(Long userId, TargetType targetType, List<Long> targetIds) {
        if (targetIds == null || targetIds.isEmpty()) {
            return Map.of();
        }

        List<Reaction> reactions = reactionRepository.findAllByUser_IdAndTargetTypeAndTargetIdIn(
                userId,
                targetType,
                targetIds
        );

        return reactions.stream()
                .collect(Collectors.toMap(
                        Reaction::getTargetId,
                        Reaction::getReactionType
                ));
    }
}

