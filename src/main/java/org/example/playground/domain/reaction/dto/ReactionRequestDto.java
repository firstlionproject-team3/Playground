package org.example.playground.domain.reaction.dto;

import jakarta.validation.constraints.NotNull;
import org.example.playground.domain.reaction.entity.ReactionType;
import org.example.playground.domain.reaction.entity.TargetType;

public record ReactionRequestDto(
        @NotNull TargetType targetType,     // QUESTION | ANSWER | COMMENT
        @NotNull Long targetId,              // 대상 ID
        @NotNull ReactionType reactionType   // LIKE | DISLIKE
) {
}

