import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuthStore } from '@/store/authStore';
import { authApi } from '@/api/auth';
import { userApi } from '@/api/user';
import toast from 'react-hot-toast';

export default function OAuthCallback() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { setAuth } = useAuthStore();
  const code = searchParams.get('code');

  useEffect(() => {
    const handleOAuthCallback = async () => {
      if (!code) {
        toast.error('인증 코드가 없습니다.');
        navigate('/login');
        return;
      }

      try {
        const { accessToken } = await authApi.exchangeOAuthToken(code);
        const user = await userApi.getMyPage();
        setAuth(accessToken, user);
        toast.success('로그인되었습니다!');
        navigate('/');
      } catch (error: any) {
        toast.error(error.response?.data?.message || 'OAuth 로그인에 실패했습니다.');
        navigate('/login');
      }
    };

    handleOAuthCallback();
  }, [code, navigate, setAuth]);

  return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="text-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto"></div>
        <p className="mt-4 text-gray-600">로그인 처리 중...</p>
      </div>
    </div>
  );
}

