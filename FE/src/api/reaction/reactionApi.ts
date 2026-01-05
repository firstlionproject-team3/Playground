import { apiClient } from '../axios/instance';
import type {
  ReactionRequest,
  ReactionCountResponse,
  MyReactionResponse,
  ReactionCountsMap,
  MyReactionsMap,
  TargetType,
} from '@/types';

export const reactionApi = {
  like: async (data: ReactionRequest): Promise<void> => {
    await apiClient.post('/reaction/like', data);
  },

  dislike: async (data: ReactionRequest): Promise<void> => {
    await apiClient.post('/reaction/dislike', data);
  },

  getCount: async (targetType: TargetType, targetId: number): Promise<ReactionCountResponse> => {
    const response = await apiClient.get<ReactionCountResponse>(
      `/reaction/count?targetType=${targetType}&targetId=${targetId}`
    );
    return response.data;
  },

  getMyReaction: async (
    targetType: TargetType,
    targetId: number
  ): Promise<MyReactionResponse> => {
    const response = await apiClient.get<MyReactionResponse>(
      `/reaction/me?targetType=${targetType}&targetId=${targetId}`
    );
    return response.data;
  },

  delete: async (targetType: TargetType, targetId: number): Promise<void> => {
    await apiClient.delete(`/reaction?targetType=${targetType}&targetId=${targetId}`);
  },

  getLikeCounts: async (
    targetType: TargetType,
    targetIds: number[]
  ): Promise<ReactionCountsMap> => {
    const ids = targetIds.join(',');
    const response = await apiClient.get<ReactionCountsMap>(
      `/reaction/counts/likes?targetType=${targetType}&targetIds=${ids}`
    );
    return response.data;
  },

  getDislikeCounts: async (
    targetType: TargetType,
    targetIds: number[]
  ): Promise<ReactionCountsMap> => {
    const ids = targetIds.join(',');
    const response = await apiClient.get<ReactionCountsMap>(
      `/reaction/counts/dislikes?targetType=${targetType}&targetIds=${ids}`
    );
    return response.data;
  },

  getMyReactions: async (
    targetType: TargetType,
    targetIds: number[]
  ): Promise<MyReactionsMap> => {
    const ids = targetIds.join(',');
    const response = await apiClient.get<MyReactionsMap>(
      `/reaction/me/map?targetType=${targetType}&targetIds=${ids}`
    );
    return response.data;
  },
};

