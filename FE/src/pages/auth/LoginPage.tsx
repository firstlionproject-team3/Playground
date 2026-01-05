import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Layout } from '@/components/layout/Layout';
import { Input } from '@/components/common/Input';
import { Button } from '@/components/common/Button';
import { useLogin } from '@/hooks/auth/useLogin';
import { oauthApi } from '@/api/auth/oauthApi';
import { validation } from '@/utils/validation';
import './LoginPage.css';

const LoginPage = () => {
  const navigate = useNavigate();
  const { login, loading, error } = useLogin();
  const [formData, setFormData] = useState({
    loginId: '',
    password: '',
  });
  const [errors, setErrors] = useState<Record<string, string>>({});

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: '' }));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const newErrors: Record<string, string> = {};

    if (!formData.loginId) {
      newErrors.loginId = '로그인 ID를 입력해주세요.';
    }
    if (!formData.password) {
      newErrors.password = '비밀번호를 입력해주세요.';
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    try {
      await login(formData);
    } catch (err) {
      // 에러는 useLogin에서 처리됨
    }
  };

  const handleOAuthLogin = (provider: 'github' | 'naver') => {
    window.location.href = oauthApi.getOAuthUrl(provider);
  };

  return (
    <Layout>
      <div className="login-container">
        <div className="login-card">
          <h1 className="login-title">로그인</h1>
          <form onSubmit={handleSubmit} className="login-form">
            <Input
              type="text"
              name="loginId"
              label="로그인 ID"
              placeholder="로그인 ID를 입력하세요"
              value={formData.loginId}
              onChange={handleChange}
              error={errors.loginId}
              required
            />
            <Input
              type="password"
              name="password"
              label="비밀번호"
              placeholder="비밀번호를 입력하세요"
              value={formData.password}
              onChange={handleChange}
              error={errors.password}
              required
            />
            {error && <div className="login-error">{error}</div>}
            <Button type="submit" fullWidth disabled={loading}>
              {loading ? '로그인 중...' : '로그인'}
            </Button>
          </form>
          <div className="login-divider">
            <span>또는</span>
          </div>
          <div className="login-oauth">
            <Button
              variant="outline"
              fullWidth
              onClick={() => handleOAuthLogin('github')}
            >
              GitHub로 로그인
            </Button>
            <Button
              variant="outline"
              fullWidth
              onClick={() => handleOAuthLogin('naver')}
            >
              Naver로 로그인
            </Button>
          </div>
          <div className="login-footer">
            <span>계정이 없으신가요? </span>
            <button
              type="button"
              className="login-link"
              onClick={() => navigate('/register')}
            >
              회원가입
            </button>
          </div>
        </div>
      </div>
    </Layout>
  );
};

export default LoginPage;

