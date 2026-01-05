import { createContext, useContext, useEffect, ReactNode } from 'react';
import { useAuth } from '@/hooks/auth/useAuth';
import { userApi } from '@/api/user/userApi';

interface AuthContextType {
  accessToken: string | null;
  user: ReturnType<typeof useAuth>['user'];
  isAuthenticated: boolean;
  isAdmin: boolean;
  login: ReturnType<typeof useAuth>['login'];
  logout: ReturnType<typeof useAuth>['logout'];
  setUser: ReturnType<typeof useAuth>['setUser'];
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const auth = useAuth();

  useEffect(() => {
    // 인증된 상태에서 사용자 정보가 없으면 가져오기
    if (auth.isAuthenticated && !auth.user && auth.accessToken) {
      userApi
        .getMyPage()
        .then((data) => {
          auth.setUser({
            id: 0, // API에서 제공하지 않으므로 임시값
            loginId: '',
            nickname: data.nickname,
            email: data.email,
          });
        })
        .catch(console.error);
    }
  }, [auth.isAuthenticated, auth.user, auth.accessToken, auth.setUser]);

  return <AuthContext.Provider value={auth}>{children}</AuthContext.Provider>;
};

export const useAuthContext = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuthContext must be used within an AuthProvider');
  }
  return context;
};

