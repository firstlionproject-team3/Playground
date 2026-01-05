import { apiClient } from '../axios/instance';
import type { NotificationListResponse } from '@/types';

export const notificationApi = {
  getList: async (): Promise<NotificationListResponse> => {
    const response = await apiClient.get<NotificationListResponse>('/notification');
    return response.data;
  },

  markAsRead: async (id: number): Promise<void> => {
    await apiClient.patch(`/notification/${id}/read`);
  },

  delete: async (id: number): Promise<void> => {
    await apiClient.delete(`/notification/${id}`);
  },

  unsubscribe: async (): Promise<void> => {
    await apiClient.delete('/notification/subscribe');
  },
};

