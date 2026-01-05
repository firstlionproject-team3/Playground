import { apiClient } from '../axios/instance';
import type { MyPageResponse, Page } from '@/types';

export interface AdminUserDTO {
  id: number;
  loginId: string;
  nickname: string;
  email: string;
  role: string;
  status: string;
  joinedDate: string;
}

export const adminUserApi = {
  getUserList: async (page = 0, size = 20): Promise<Page<AdminUserDTO>> => {
    const response = await apiClient.get<Page<AdminUserDTO>>(
      `/admin/users?page=${page}&size=${size}`
    );
    return response.data;
  },

  getUserDetail: async (id: number): Promise<MyPageResponse> => {
    const response = await apiClient.get<MyPageResponse>(`/admin/users/${id}`);
    return response.data;
  },

  deleteUser: async (id: number): Promise<void> => {
    await apiClient.delete(`/admin/users/${id}`);
  },
};

