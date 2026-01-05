import { TargetType, ReactionType } from '../enum.types';

export interface ReactionRequest {
  targetType: TargetType;
  targetId: number;
  reactionType: 'LIKE' | 'DISLIKE';
}

export interface ReactionCountResponse {
  likeCount: number;
  dislikeCount: number;
}

export interface MyReactionResponse {
  hasReaction: boolean;
  reactionType: ReactionType;
}

export interface ReactionCountsMap {
  [key: string]: number;
}

export interface MyReactionsMap {
  [key: string]: ReactionType;
}

