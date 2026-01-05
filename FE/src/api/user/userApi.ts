import { apiClient } from '../axios/instance';
import type {
  RegisterRequest,
  RegisterResponse,
  MyPageResponse,
  UpdateMyPageRequest,
  QuestionPage,
  AnswerPage,
} from '@/types';

export const userApi = {
  register: async (data: RegisterRequest): Promise<RegisterResponse> => {
    const response = await apiClient.post<RegisterResponse>('/users', data);
    return response.data;
  },

  getMyPage: async (): Promise<MyPageResponse> => {
    const response = await apiClient.get<MyPageResponse>('/users/me');
    return response.data;
  },

  updateMyPage: async (data: UpdateMyPageRequest): Promise<MyPageResponse> => {
    const response = await apiClient.patch<MyPageResponse>('/users/me', data);
    return response.data;
  },

  getMyQuestions: async (page = 0, size = 20): Promise<QuestionPage> => {
    const response = await apiClient.get<QuestionPage>(
      `/users/me/questions?page=${page}&size=${size}&sort=createdAt,desc`
    );
    return response.data;
  },

  getMyAnswers: async (page = 0, size = 20): Promise<AnswerPage> => {
    const response = await apiClient.get<AnswerPage>(
      `/users/me/answers?page=${page}&size=${size}&sort=createdAt,desc`
    );
    return response.data;
  },

  deleteAccount: async (): Promise<void> => {
    await apiClient.delete('/users/me');
  },
};

