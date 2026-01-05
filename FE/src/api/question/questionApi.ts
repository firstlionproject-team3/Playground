import { apiClient } from '../axios/instance';
import type {
  CreateQuestionRequest,
  CreateQuestionResponse,
  QuestionPage,
  QuestionDetail,
  UpdateQuestionRequest,
  SearchType,
} from '@/types';

export const questionApi = {
  create: async (data: CreateQuestionRequest): Promise<CreateQuestionResponse> => {
    const response = await apiClient.post<CreateQuestionResponse>('/questions', data);
    return response.data;
  },

  getList: async (
    type: SearchType = 'all',
    keyword = '',
    page = 0,
    size = 10
  ): Promise<QuestionPage> => {
    const params = new URLSearchParams({
      type,
      keyword,
      page: page.toString(),
      size: size.toString(),
    });
    const response = await apiClient.get<QuestionPage>(`/questions?${params}`);
    return response.data;
  },

  getDetail: async (id: number): Promise<QuestionDetail> => {
    const response = await apiClient.get<QuestionDetail>(`/questions/${id}`);
    return response.data;
  },

  update: async (id: number, data: UpdateQuestionRequest): Promise<void> => {
    await apiClient.patch(`/questions/${id}`, data);
  },

  delete: async (id: number): Promise<void> => {
    await apiClient.delete(`/questions/${id}`);
  },

  report: async (id: number): Promise<void> => {
    await apiClient.patch(`/questions/${id}/report`);
  },

  acceptAnswer: async (questionId: number, answerId: number): Promise<void> => {
    await apiClient.patch(`/questions/${questionId}/answers/${answerId}/accept`);
  },
};

