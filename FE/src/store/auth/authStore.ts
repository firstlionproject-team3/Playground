import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { authApi } from '@/api/auth/authApi';
import type { User } from '@/types';

interface AuthState {
  accessToken: string | null;
  user: User | null;
  isAuthenticated: boolean;
  login: (token: string, user?: User) => void;
  logout: () => Promise<void>;
  refreshToken: () => Promise<string>;
  setUser: (user: User | null) => void;
}

export const authStore = create<AuthState>()(
  persist(
    (set, get) => ({
      accessToken: null,
      user: null,
      isAuthenticated: false,

      login: (token: string, user?: User) => {
        set({
          accessToken: token,
          user: user || null,
          isAuthenticated: true,
        });
      },

      logout: async () => {
        try {
          await authApi.logout();
        } catch (error) {
          console.error('Logout error:', error);
        } finally {
          set({
            accessToken: null,
            user: null,
            isAuthenticated: false,
          });
        }
      },

      refreshToken: async () => {
        try {
          const response = await authApi.refreshToken();
          const newToken = response.accessToken;
          set({ accessToken: newToken, isAuthenticated: true });
          return newToken;
        } catch (error) {
          set({
            accessToken: null,
            user: null,
            isAuthenticated: false,
          });
          throw error;
        }
      },

      setUser: (user: User | null) => {
        set({ user });
      },
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({
        accessToken: state.accessToken,
        user: state.user,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
);

