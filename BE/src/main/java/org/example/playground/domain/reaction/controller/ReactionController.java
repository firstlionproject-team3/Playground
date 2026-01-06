package org.example.playground.domain.reaction.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.playground.domain.reaction.dto.ReactionRequestDto;
import org.example.playground.domain.reaction.entity.ReactionType;
import org.example.playground.domain.reaction.entity.TargetType;
import org.example.playground.domain.reaction.service.ReactionService;
import org.example.playground.global.security.user.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reaction")
public class ReactionController {

    private final ReactionService reactionService;

    /**
     * 추천/비추천 생성 또는 변경
     * 같은 타입이면 삭제, 다른 타입이면 변경
     */
    @PostMapping("/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void toggleLikeReaction(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody ReactionRequestDto request
    ) {
        reactionService.toggleReaction(principal.getId(), request);
    }

    @PostMapping("/dislike")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void toggleDisLikeReaction(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody ReactionRequestDto request
    ) {
        reactionService.toggleReaction(principal.getId(), request);
    }

    /**
     * 특정 대상의 추천/비추천 개수 조회
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getReactionCount(
            @RequestParam TargetType targetType,
            @RequestParam Long targetId
    ) {
        long likeCount = reactionService.getLikeCount(targetType, targetId);
        long dislikeCount = reactionService.getDislikeCount(targetType, targetId);
        return ResponseEntity.ok(Map.of(
                "likeCount", likeCount,
                "dislikeCount", dislikeCount
        ));
    }

    /**
     * 현재 사용자의 추천/비추천 상태 조회
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMyReactionStatus(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam TargetType targetType,
            @RequestParam Long targetId
    ) {
        boolean hasReaction = reactionService.hasReaction(principal.getId(), targetType, targetId);
        String reactionType = reactionService.getUserReactionType(principal.getId(), targetType, targetId)
                .map(ReactionType::name)
                .orElse("NONE");

        return ResponseEntity.ok(Map.of(
                "hasReaction", hasReaction,
                "reactionType", reactionType
        ));
    }

    /**
     * 추천/비추천 삭제
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReaction(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam TargetType targetType,
            @RequestParam Long targetId
    ) {
        reactionService.deleteReaction(principal.getId(), targetType, targetId);
    }

    /**
     * 여러 대상의 추천 수 맵 조회 (N+1 문제 방지)
     * GET /reaction/counts/likes?targetType=QUESTION&targetIds=1,2,3
     */
    @GetMapping("/counts/likes")
    public ResponseEntity<Map<Long, Long>> getLikeCountMap(
            @RequestParam TargetType targetType,
            @RequestParam List<Long> targetIds
    ) {
        Map<Long, Long> likeCountMap = reactionService.getLikeCountMap(targetType, targetIds);
        return ResponseEntity.ok(likeCountMap);
    }

    /**
     * 여러 대상의 비추천 수 맵 조회 (N+1 문제 방지)
     * GET /reaction/counts/dislikes?targetType=ANSWER&targetIds=10,11,12
     */
    @GetMapping("/counts/dislikes")
    public ResponseEntity<Map<Long, Long>> getDislikeCountMap(
            @RequestParam TargetType targetType,
            @RequestParam List<Long> targetIds
    ) {
        Map<Long, Long> dislikeCountMap = reactionService.getDislikeCountMap(targetType, targetIds);
        return ResponseEntity.ok(dislikeCountMap);
    }

    /**
     * 현재 사용자가 여러 대상에 대해 남긴 추천/비추천 타입 맵 조회 (N+1 문제 방지)
     * GET /reaction/me/map?targetType=COMMENT&targetIds=20,21,22
     */
    @GetMapping("/me/map")
    public ResponseEntity<Map<Long, String>> getMyReactionMap(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam TargetType targetType,
            @RequestParam List<Long> targetIds
    ) {
        Map<Long, ReactionType> reactionMap = reactionService.getMyReactionMap(
                principal.getId(),
                targetType,
                targetIds
        );

        // ReactionType을 String으로 변환
        Map<Long, String> result = reactionMap.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().name()
                ));

        return ResponseEntity.ok(result);
    }
}

