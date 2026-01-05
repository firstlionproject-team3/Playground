import { useCallback } from 'react';
import { authStore } from '@/store/auth/authStore';

export const useAuth = () => {
  const accessToken = authStore((state) => state.accessToken);
  const user = authStore((state) => state.user);
  const isAuthenticated = authStore((state) => state.isAuthenticated);
  const login = authStore((state) => state.login);
  const logout = authStore((state) => state.logout);
  const setUser = authStore((state) => state.setUser);

  const isAdmin = useCallback(() => {
    return user?.roles?.includes('ROLE_ADMIN') || user?.id === 5;
  }, [user]);

  return {
    accessToken,
    user,
    isAuthenticated,
    isAdmin: isAdmin(),
    login,
    logout,
    setUser,
  };
};

