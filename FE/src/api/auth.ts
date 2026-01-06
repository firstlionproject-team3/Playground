import apiClient from './client';
import { LoginRequest, LoginResponse } from '@/types';

export const authApi = {
  // 일반 로그인
  login: async (data: LoginRequest): Promise<LoginResponse> => {
    const response = await apiClient.post<LoginResponse>('/auth/login', data);
    return response.data;
  },

  // 로그아웃
  logout: async (): Promise<void> => {
    await apiClient.post('/auth/logout');
  },

  // OAuth 토큰 교환
  exchangeOAuthToken: async (code: string): Promise<LoginResponse> => {
    const response = await apiClient.post<LoginResponse>(`/oauth/token?code=${code}`);
    return response.data;
  },

  // OAuth 로그인 시작
  startOAuthLogin: (provider: 'github' | 'naver') => {
    const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://3.35.4.73:8080';
    window.location.href = `${apiBaseUrl}/oauth2/authorization/${provider}`;
  },
};

