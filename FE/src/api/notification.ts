import apiClient from './client';
import { NotificationListResponse } from '@/types';

export const notificationApi = {
  // 알림 목록 조회
  getNotifications: async (): Promise<NotificationListResponse> => {
    const response = await apiClient.get<NotificationListResponse>('/notification');
    return response.data;
  },

  // 알림 읽음 처리
  markAsRead: async (id: number): Promise<void> => {
    await apiClient.patch(`/notification/${id}/read`);
  },

  // 알림 삭제
  delete: async (id: number): Promise<void> => {
    await apiClient.delete(`/notification/${id}`);
  },

  // SSE 연결 종료
  unsubscribe: async (): Promise<void> => {
    await apiClient.delete('/notification/subscribe');
  },
};

