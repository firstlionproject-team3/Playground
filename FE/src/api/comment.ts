import apiClient from './client';
import { CommentResponse, CommentCreateRequest, CommentUpdateRequest } from '@/types';

export const commentApi = {
  // 댓글 생성
  create: async (answerId: number, data: CommentCreateRequest): Promise<CommentResponse> => {
    const response = await apiClient.post<CommentResponse>(`/answers/${answerId}/comments`, data);
    return response.data;
  },

  // 댓글 목록 조회
  getComments: async (answerId: number): Promise<CommentResponse[]> => {
    const response = await apiClient.get<CommentResponse[]>(`/answers/${answerId}/comments`);
    return response.data;
  },

  // 댓글 수정
  update: async (commentId: number, data: CommentUpdateRequest): Promise<CommentResponse> => {
    const response = await apiClient.patch<CommentResponse>(`/comments/${commentId}`, data);
    return response.data;
  },

  // 댓글 삭제
  delete: async (commentId: number): Promise<void> => {
    await apiClient.delete(`/comments/${commentId}`);
  },

  // 댓글 단건 조회
  getComment: async (commentId: number): Promise<CommentResponse> => {
    const response = await apiClient.get<CommentResponse>(`/comments/${commentId}`);
    return response.data;
  },
};

