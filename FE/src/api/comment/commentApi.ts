import { apiClient } from '../axios/instance';
import type {
  CreateCommentRequest,
  CommentResponse,
  UpdateCommentRequest,
} from '@/types';

export const commentApi = {
  create: async (answerId: number, data: CreateCommentRequest): Promise<CommentResponse> => {
    const response = await apiClient.post<CommentResponse>(
      `/answers/${answerId}/comments`,
      data
    );
    return response.data;
  },

  getList: async (answerId: number): Promise<CommentResponse[]> => {
    const response = await apiClient.get<CommentResponse[]>(`/answers/${answerId}/comments`);
    return response.data;
  },

  update: async (commentId: number, data: UpdateCommentRequest): Promise<CommentResponse> => {
    const response = await apiClient.patch<CommentResponse>(`/comments/${commentId}`, data);
    return response.data;
  },

  delete: async (commentId: number): Promise<void> => {
    await apiClient.delete(`/comments/${commentId}`);
  },
};

