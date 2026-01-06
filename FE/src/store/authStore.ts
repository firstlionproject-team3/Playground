import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import { setAccessToken, getAccessToken } from '@/api/client';
import { UserMyPageResponse } from '@/types';

interface AuthState {
  isAuthenticated: boolean;
  user: UserMyPageResponse | null;
  accessToken: string | null;
  setAuth: (token: string, user: UserMyPageResponse | null) => void;
  setUser: (user: UserMyPageResponse | null) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      isAuthenticated: false,
      user: null,
      accessToken: null,
      setAuth: (token, user) => {
        setAccessToken(token);
        set({
          accessToken: token,
          isAuthenticated: !!token,
          user,
        });
      },
      setUser: (user) => {
        set({ user });
      },
      logout: () => {
        setAccessToken(null);
        set({
          accessToken: null,
          isAuthenticated: false,
          user: null,
        });
      },
    }),
    {
      name: 'auth-storage',
      storage: createJSONStorage(() => localStorage),
      partialize: (state) => ({
        accessToken: state.accessToken,
        isAuthenticated: state.isAuthenticated,
        user: state.user,
      }),
    }
  )
);

// 초기화 시 토큰 동기화
if (typeof window !== 'undefined') {
  const token = getAccessToken();
  if (token) {
    useAuthStore.getState().setAuth(token, null);
  }
}

