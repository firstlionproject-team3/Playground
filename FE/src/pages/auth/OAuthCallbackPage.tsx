import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { oauthApi } from '@/api/auth/oauthApi';
import { authStore } from '@/store/auth/authStore';
import { getErrorMessage } from '@/utils/error';

const OAuthCallbackPage = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const code = searchParams.get('code');

  useEffect(() => {
    const handleOAuthCallback = async () => {
      if (!code) {
        navigate('/login');
        return;
      }

      try {
        const response = await oauthApi.getToken(code);
        authStore.getState().login(response.accessToken);
        navigate('/');
      } catch (error) {
        console.error('OAuth callback error:', error);
        alert(`로그인 실패: ${getErrorMessage(error)}`);
        navigate('/login');
      }
    };

    handleOAuthCallback();
  }, [code, navigate]);

  return (
    <div style={{ textAlign: 'center', padding: '2rem' }}>
      <p>로그인 처리 중...</p>
    </div>
  );
};

export default OAuthCallbackPage;

