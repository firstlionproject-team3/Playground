import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Layout } from '@/components/layout/Layout';
import { Input } from '@/components/common/Input';
import { Button } from '@/components/common/Button';
import { userApi } from '@/api/user/userApi';
import { validation } from '@/utils/validation';
import { getErrorMessage } from '@/utils/error';
import './RegisterPage.css';

const RegisterPage = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [formData, setFormData] = useState({
    loginId: '',
    password: '',
    confirmPassword: '',
    email: '',
  });
  const [errors, setErrors] = useState<Record<string, string>>({});

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: '' }));
    }
  };

  const validate = () => {
    const newErrors: Record<string, string> = {};

    const loginIdError = validation.loginId(formData.loginId);
    if (loginIdError) newErrors.loginId = loginIdError;

    const passwordError = validation.password(formData.password);
    if (passwordError) newErrors.password = passwordError;

    if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = '비밀번호가 일치하지 않습니다.';
    }

    const emailError = validation.email(formData.email);
    if (emailError) newErrors.email = emailError;

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validate()) {
      return;
    }

    setLoading(true);
    setError(null);

    try {
      await userApi.register({
        loginId: formData.loginId,
        password: formData.password,
        email: formData.email || undefined,
      });
      navigate('/login');
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout>
      <div className="register-container">
        <div className="register-card">
          <h1 className="register-title">회원가입</h1>
          <form onSubmit={handleSubmit} className="register-form">
            <Input
              type="text"
              name="loginId"
              label="로그인 ID"
              placeholder="영문과 숫자만 사용 가능"
              value={formData.loginId}
              onChange={handleChange}
              error={errors.loginId}
              required
            />
            <Input
              type="password"
              name="password"
              label="비밀번호"
              placeholder="8-64자, 영문+숫자+특수문자 포함"
              value={formData.password}
              onChange={handleChange}
              error={errors.password}
              required
            />
            <Input
              type="password"
              name="confirmPassword"
              label="비밀번호 확인"
              placeholder="비밀번호를 다시 입력하세요"
              value={formData.confirmPassword}
              onChange={handleChange}
              error={errors.confirmPassword}
              required
            />
            <Input
              type="email"
              name="email"
              label="이메일 (선택)"
              placeholder="이메일을 입력하세요"
              value={formData.email}
              onChange={handleChange}
              error={errors.email}
            />
            {error && <div className="register-error">{error}</div>}
            <Button type="submit" fullWidth disabled={loading}>
              {loading ? '가입 중...' : '회원가입'}
            </Button>
          </form>
          <div className="register-footer">
            <span>이미 계정이 있으신가요? </span>
            <button
              type="button"
              className="register-link"
              onClick={() => navigate('/login')}
            >
              로그인
            </button>
          </div>
        </div>
      </div>
    </Layout>
  );
};

export default RegisterPage;

