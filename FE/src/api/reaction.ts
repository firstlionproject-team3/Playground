import apiClient from './client';
import { ReactionRequest, ReactionCount, MyReaction, TargetType } from '@/types';

export const reactionApi = {
  // 추천/비추천 토글
  toggleLike: async (data: ReactionRequest): Promise<void> => {
    await apiClient.post('/reaction/like', data);
  },

  toggleDislike: async (data: ReactionRequest): Promise<void> => {
    await apiClient.post('/reaction/dislike', data);
  },

  // 추천/비추천 개수 조회
  getCount: async (targetType: TargetType, targetId: number): Promise<ReactionCount> => {
    const response = await apiClient.get<ReactionCount>(
      `/reaction/count?targetType=${targetType}&targetId=${targetId}`
    );
    return response.data;
  },

  // 내 반응 상태 조회
  getMyReaction: async (targetType: TargetType, targetId: number): Promise<MyReaction> => {
    const response = await apiClient.get<MyReaction>(
      `/reaction/me?targetType=${targetType}&targetId=${targetId}`
    );
    return response.data;
  },

  // 반응 삭제
  delete: async (targetType: TargetType, targetId: number): Promise<void> => {
    await apiClient.delete(`/reaction?targetType=${targetType}&targetId=${targetId}`);
  },

  // 여러 대상의 반응 개수 조회 (배치)
  getLikeCounts: async (targetType: TargetType, targetIds: number[]): Promise<Record<string, number>> => {
    const ids = targetIds.join(',');
    const response = await apiClient.get<Record<string, number>>(
      `/reaction/counts/likes?targetType=${targetType}&targetIds=${ids}`
    );
    return response.data;
  },

  getDislikeCounts: async (targetType: TargetType, targetIds: number[]): Promise<Record<string, number>> => {
    const ids = targetIds.join(',');
    const response = await apiClient.get<Record<string, number>>(
      `/reaction/counts/dislikes?targetType=${targetType}&targetIds=${ids}`
    );
    return response.data;
  },

  // 여러 대상의 내 반응 상태 조회 (배치)
  getMyReactions: async (targetType: TargetType, targetIds: number[]): Promise<Record<string, string>> => {
    const ids = targetIds.join(',');
    const response = await apiClient.get<Record<string, string>>(
      `/reaction/me/map?targetType=${targetType}&targetIds=${ids}`
    );
    return response.data;
  },
};

