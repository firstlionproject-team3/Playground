import apiClient from './client';
import {
  UserRegisterRequest,
  UserRegisterResponse,
  UserMyPageResponse,
  UserUpdateRequest,
  PageResponse,
  QuestionSummary,
  AnswerSummary,
} from '@/types';

export const userApi = {
  // 회원가입
  register: async (data: UserRegisterRequest): Promise<UserRegisterResponse> => {
    const response = await apiClient.post<UserRegisterResponse>('/users', data);
    return response.data;
  },

  // 마이페이지 조회
  getMyPage: async (): Promise<UserMyPageResponse> => {
    const response = await apiClient.get<UserMyPageResponse>('/users/me');
    return response.data;
  },

  // 마이페이지 수정
  updateMyPage: async (data: UserUpdateRequest): Promise<UserMyPageResponse> => {
    const response = await apiClient.patch<UserMyPageResponse>('/users/me', data);
    return response.data;
  },

  // 내 질문 목록
  getMyQuestions: async (page = 0, size = 20): Promise<PageResponse<QuestionSummary>> => {
    const response = await apiClient.get<PageResponse<QuestionSummary>>(
      `/users/me/questions?page=${page}&size=${size}&sort=createdAt,desc`
    );
    return response.data;
  },

  // 내 답변 목록
  getMyAnswers: async (page = 0, size = 20): Promise<PageResponse<AnswerSummary>> => {
    const response = await apiClient.get<PageResponse<AnswerSummary>>(
      `/users/me/answers?page=${page}&size=${size}&sort=createdAt,desc`
    );
    return response.data;
  },

  // 회원 탈퇴
  deleteAccount: async (): Promise<void> => {
    await apiClient.delete('/users/me');
  },
};

