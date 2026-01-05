import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Layout } from '@/components/layout/Layout';
import { Button } from '@/components/common/Button';
import { Input } from '@/components/common/Input';
import { userApi } from '@/api/user/userApi';
import { useAuthContext } from '@/contexts/AuthContext';
import { getErrorMessage } from '@/utils/error';
import { validation } from '@/utils/validation';
import './MyPage.css';

const MyPage = () => {
  const { setUser } = useAuthContext();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [myPage, setMyPage] = useState<any>(null);
  const [formData, setFormData] = useState({
    nickname: '',
    email: '',
  });
  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    loadMyPage();
  }, []);

  const loadMyPage = async () => {
    try {
      const data = await userApi.getMyPage();
      setMyPage(data);
      setFormData({
        nickname: data.nickname,
        email: data.email || '',
      });
    } catch (error) {
      console.error('Failed to load my page:', error);
    } finally {
      setLoading(false);
    }
  };

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
    const emailError = validation.email(formData.email);
    if (emailError) newErrors.email = emailError;

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    setSaving(true);
    try {
      const data = await userApi.updateMyPage(formData);
      setMyPage(data);
      setUser({
        id: 0,
        loginId: '',
        nickname: data.nickname,
        email: data.email,
      });
      alert('정보가 수정되었습니다.');
    } catch (error) {
      alert(`수정 실패: ${getErrorMessage(error)}`);
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <Layout>
        <div className="mypage-loading">로딩 중...</div>
      </Layout>
    );
  }

  return (
    <Layout>
      <div className="mypage-container">
        <div className="mypage-card">
          <h1 className="mypage-title">마이페이지</h1>
          <form onSubmit={handleSubmit} className="mypage-form">
            <Input
              name="nickname"
              label="닉네임"
              value={formData.nickname}
              onChange={handleChange}
              required
            />
            <Input
              type="email"
              name="email"
              label="이메일"
              value={formData.email}
              onChange={handleChange}
              error={errors.email}
            />
            <div className="mypage-meta">
              <p>가입일: {myPage?.joinedDate ? new Date(myPage.joinedDate).toLocaleDateString('ko-KR') : ''}</p>
            </div>
            <div className="mypage-actions">
              <Button type="submit" disabled={saving}>
                {saving ? '저장 중...' : '저장'}
              </Button>
            </div>
          </form>
          <div className="mypage-links">
            <Link to="/mypage/questions">
              <Button variant="outline" fullWidth>
                내 질문 보기
              </Button>
            </Link>
            <Link to="/mypage/answers">
              <Button variant="outline" fullWidth>
                내 답변 보기
              </Button>
            </Link>
          </div>
        </div>
      </div>
    </Layout>
  );
};

export default MyPage;

