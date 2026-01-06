import apiClient from './client';
import { UserMyPageResponse, PageResponse } from '@/types';

export const adminApi = {
  // 사용자 목록 조회
  getUsers: async (page = 0, size = 20): Promise<PageResponse<any>> => {
    const response = await apiClient.get<PageResponse<any>>(
      `/admin/users?page=${page}&size=${size}`
    );
    return response.data;
  },

  // 사용자 상세 조회
  getUser: async (id: number): Promise<UserMyPageResponse> => {
    const response = await apiClient.get<UserMyPageResponse>(`/admin/users/${id}`);
    return response.data;
  },

  // 사용자 삭제
  deleteUser: async (id: number): Promise<void> => {
    await apiClient.delete(`/admin/users/${id}`);
  },
};

