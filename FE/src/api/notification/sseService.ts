import { authStore } from '@/store/auth/authStore';
import { notificationApi } from './notificationApi';
import type { Notification } from '@/types';

export class SSEService {
  private eventSource: EventSource | null = null;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectDelay = 3000;

  connect(onMessage: (notification: Notification) => void, onError?: (error: Event) => void) {
    const token = authStore.getState().accessToken;
    if (!token) {
      console.warn('No access token available for SSE connection');
      return;
    }

    const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
    const url = `${baseUrl}/notification/subscribe`;

    // EventSource는 Authorization 헤더를 직접 지원하지 않으므로
    // URL에 토큰을 포함하거나 서버에서 쿠키 기반 인증을 사용해야 함
    // 여기서는 쿠키 기반 인증을 가정
    this.eventSource = new EventSource(url, { withCredentials: true });

    this.eventSource.onmessage = (event) => {
      try {
        const notification = JSON.parse(event.data) as Notification;
        onMessage(notification);
        this.reconnectAttempts = 0; // 성공 시 재연결 시도 횟수 리셋
      } catch (error) {
        console.error('Failed to parse SSE message:', error);
      }
    };

    this.eventSource.onerror = (error) => {
      console.error('SSE connection error:', error);
      if (onError) {
        onError(error);
      }
      this.handleReconnect(onMessage, onError);
    };
  }

  private handleReconnect(
    onMessage: (notification: Notification) => void,
    onError?: (error: Event) => void
  ) {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.error('Max reconnection attempts reached');
      this.disconnect();
      return;
    }

    this.reconnectAttempts++;
    setTimeout(() => {
      if (this.eventSource?.readyState === EventSource.CLOSED) {
        this.connect(onMessage, onError);
      }
    }, this.reconnectDelay);
  }

  disconnect() {
    if (this.eventSource) {
      this.eventSource.close();
      this.eventSource = null;
      this.reconnectAttempts = 0;
      // 서버에 연결 종료 알림
      notificationApi.unsubscribe().catch(console.error);
    }
  }

  isConnected(): boolean {
    return this.eventSource?.readyState === EventSource.OPEN;
  }
}

// 싱글톤 인스턴스
export const sseService = new SSEService();

