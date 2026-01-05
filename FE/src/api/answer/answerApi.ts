import { apiClient } from '../axios/instance';
import type {
  CreateAnswerRequest,
  AnswerDetail,
  AnswerPage,
  UpdateAnswerRequest,
} from '@/types';

export const answerApi = {
  create: async (questionId: number, data: CreateAnswerRequest): Promise<AnswerDetail> => {
    const response = await apiClient.post<AnswerDetail>(
      `/questions/${questionId}/answers`,
      data
    );
    return response.data;
  },

  getList: async (questionId: number, page = 0, size = 10): Promise<AnswerPage> => {
    const response = await apiClient.get<AnswerPage>(
      `/questions/${questionId}/answers?page=${page}&size=${size}`
    );
    return response.data;
  },

  update: async (answerId: number, data: UpdateAnswerRequest): Promise<AnswerDetail> => {
    const response = await apiClient.patch<AnswerDetail>(`/answers/${answerId}`, data);
    return response.data;
  },

  delete: async (answerId: number): Promise<void> => {
    await apiClient.delete(`/answers/${answerId}`);
  },

  report: async (questionId: number, answerId: number): Promise<void> => {
    await apiClient.patch(`/questions/${questionId}/answers/${answerId}/report`);
  },
};

