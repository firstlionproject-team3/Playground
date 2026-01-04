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
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void toggleReaction(
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
}

