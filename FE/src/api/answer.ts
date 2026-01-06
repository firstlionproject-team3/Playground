import apiClient from './client';
import {
  AnswerSummary,
  AnswerDetail,
  AnswerCreateRequest,
  AnswerUpdateRequest,
  PageResponse,
} from '@/types';

export const answerApi = {
  // 답변 생성
  create: async (questionId: number, data: AnswerCreateRequest): Promise<AnswerDetail> => {
    const response = await apiClient.post<AnswerDetail>(
      `/questions/${questionId}/answers`,
      data
    );
    return response.data;
  },

  // 답변 목록 조회
  getAnswers: async (questionId: number, page = 0, size = 10): Promise<PageResponse<AnswerSummary>> => {
    const response = await apiClient.get<PageResponse<AnswerSummary>>(
      `/questions/${questionId}/answers?page=${page}&size=${size}`
    );
    return response.data;
  },

  // 답변 수정
  update: async (answerId: number, data: AnswerUpdateRequest): Promise<AnswerDetail> => {
    const response = await apiClient.patch<AnswerDetail>(`/answers/${answerId}`, data);
    return response.data;
  },

  // 답변 삭제
  delete: async (answerId: number): Promise<void> => {
    await apiClient.delete(`/answers/${answerId}`);
  },

  // 답변 신고
  report: async (questionId: number, answerId: number): Promise<void> => {
    await apiClient.patch(`/questions/${questionId}/answers/${answerId}/report`);
  },

  // 답변 단건 조회
  getAnswer: async (answerId: number): Promise<AnswerDetail> => {
    const response = await apiClient.get<AnswerDetail>(`/answers/${answerId}`);
    return response.data;
  },
};

