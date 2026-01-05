import { createContext, useContext, useEffect, ReactNode } from 'react';
import { notificationStore } from '@/store/notification/notificationStore';
import { notificationApi } from '@/api/notification/notificationApi';
import { sseService } from '@/api/notification/sseService';
import { useAuthContext } from './AuthContext';
import type { Notification } from '@/types';

interface NotificationContextType {
  notifications: Notification[];
  unreadCount: number;
  markAsRead: (id: number) => Promise<void>;
  removeNotification: (id: number) => Promise<void>;
  refreshNotifications: () => Promise<void>;
}

const NotificationContext = createContext<NotificationContextType | undefined>(undefined);

export const NotificationProvider = ({ children }: { children: ReactNode }) => {
  const { isAuthenticated } = useAuthContext();
  const notifications = notificationStore((state) => state.notifications);
  const unreadCount = notificationStore((state) => state.unreadCount);
  const addNotification = notificationStore((state) => state.addNotification);
  const setNotifications = notificationStore((state) => state.setNotifications);
  const markAsReadStore = notificationStore((state) => state.markAsRead);
  const removeNotificationStore = notificationStore((state) => state.removeNotification);

  // SSE 연결 관리
  useEffect(() => {
    if (!isAuthenticated) {
      sseService.disconnect();
      return;
    }

    const handleNotification = (notification: Notification) => {
      addNotification(notification);
    };

    const handleError = (error: Event) => {
      console.error('SSE error:', error);
    };

    sseService.connect(handleNotification, handleError);

    // 초기 알림 목록 로드
    refreshNotifications();

    return () => {
      sseService.disconnect();
    };
  }, [isAuthenticated, addNotification]);

  const refreshNotifications = async () => {
    if (!isAuthenticated) return;
    try {
      const data = await notificationApi.getList();
      setNotifications(data.notifications);
    } catch (error) {
      console.error('Failed to fetch notifications:', error);
    }
  };

  const markAsRead = async (id: number) => {
    try {
      await notificationApi.markAsRead(id);
      markAsReadStore(id);
    } catch (error) {
      console.error('Failed to mark notification as read:', error);
    }
  };

  const removeNotification = async (id: number) => {
    try {
      await notificationApi.delete(id);
      removeNotificationStore(id);
    } catch (error) {
      console.error('Failed to delete notification:', error);
    }
  };

  return (
    <NotificationContext.Provider
      value={{
        notifications,
        unreadCount,
        markAsRead,
        removeNotification,
        refreshNotifications,
      }}
    >
      {children}
    </NotificationContext.Provider>
  );
};

export const useNotificationContext = () => {
  const context = useContext(NotificationContext);
  if (context === undefined) {
    throw new Error('useNotificationContext must be used within a NotificationProvider');
  }
  return context;
};

