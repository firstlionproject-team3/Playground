import { NotificationType } from '../enum.types';

export interface Notification {
  id: number;
  type: NotificationType;
  content: string;
  senderId: number | null;
  receiverId: number;
  isRead: boolean;
  createdAt: string;
}

export interface NotificationListResponse {
  notifications: Notification[];
  totalCount: number;
}

