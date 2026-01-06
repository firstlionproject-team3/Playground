import apiClient from './client';
import {
  QuestionSummary,
  QuestionResponse,
  QuestionCreateRequest,
  QuestionUpdateRequest,
  IdResponse,
  PageResponse,
} from '@/types';

export const questionApi = {
  // 질문 생성
  create: async (data: QuestionCreateRequest): Promise<IdResponse> => {
    const response = await apiClient.post<IdResponse>('/questions', data);
    return response.data;
  },

  // 질문 목록 조회 (검색)
  getQuestions: async (
    page = 0,
    size = 10,
    type?: 'title' | 'content' | 'all',
    keyword?: string
  ): Promise<PageResponse<QuestionSummary>> => {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
    });
    if (type) params.append('type', type);
    if (keyword) params.append('keyword', keyword);

    const response = await apiClient.get<PageResponse<QuestionSummary>>(
      `/questions?${params.toString()}`
    );
    return response.data;
  },

  // 질문 상세 조회
  getQuestion: async (id: number): Promise<QuestionResponse> => {
    const response = await apiClient.get<QuestionResponse>(`/questions/${id}`);
    return response.data;
  },

  // 질문 수정
  update: async (id: number, data: QuestionUpdateRequest): Promise<void> => {
    await apiClient.patch(`/questions/${id}`, data);
  },

  // 질문 삭제
  delete: async (id: number): Promise<void> => {
    await apiClient.delete(`/questions/${id}`);
  },

  // 질문 신고
  report: async (id: number): Promise<void> => {
    await apiClient.patch(`/questions/${id}/report`);
  },

  // 답변 채택
  acceptAnswer: async (questionId: number, answerId: number): Promise<void> => {
    await apiClient.patch(`/questions/${questionId}/answers/${answerId}/accept`);
  },
};

