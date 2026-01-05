import { apiClient } from '../axios/instance';
import type { OAuthTokenRequest, OAuthTokenResponse } from '@/types';

export const oauthApi = {
  getOAuthUrl: (provider: 'github' | 'naver'): string => {
    const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
    return `${baseUrl}/oauth2/authorization/${provider}`;
  },

  getToken: async (code: string): Promise<OAuthTokenResponse> => {
    const response = await apiClient.get<OAuthTokenResponse>(`/oauth/token?code=${code}`);
    return response.data;
  },
};

