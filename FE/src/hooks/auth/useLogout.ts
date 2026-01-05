import { useNavigate } from 'react-router-dom';
import { authStore } from '@/store/auth/authStore';
import { notificationStore } from '@/store/notification/notificationStore';
import { sseService } from '@/api/notification/sseService';

export const useLogout = () => {
  const navigate = useNavigate();

  const logout = async () => {
    try {
      sseService.disconnect();
      notificationStore.getState().clearAll();
      await authStore.getState().logout();
      navigate('/login');
    } catch (error) {
      console.error('Logout error:', error);
      // 에러가 발생해도 로컬 상태는 초기화
      authStore.getState().logout();
      navigate('/login');
    }
  };

  return { logout };
};

