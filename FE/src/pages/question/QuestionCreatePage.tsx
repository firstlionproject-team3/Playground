import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Layout } from '@/components/layout/Layout';
import { Input } from '@/components/common/Input';
import { Textarea } from '@/components/common/Textarea';
import { Button } from '@/components/common/Button';
import { questionApi } from '@/api/question/questionApi';
import { validation } from '@/utils/validation';
import { getErrorMessage } from '@/utils/error';
import './QuestionCreatePage.css';

const QuestionCreatePage = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    title: '',
    content: '',
  });
  const [errors, setErrors] = useState<Record<string, string>>({});

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: '' }));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const newErrors: Record<string, string> = {};
    const titleError = validation.title(formData.title);
    if (titleError) newErrors.title = titleError;

    const contentError = validation.content(formData.content);
    if (contentError) newErrors.content = contentError;

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    setLoading(true);
    try {
      const response = await questionApi.create(formData);
      navigate(`/questions/${response.id}`);
    } catch (error) {
      alert(`질문 작성 실패: ${getErrorMessage(error)}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout>
      <div className="question-create-container">
        <div className="question-create-card">
          <h1 className="question-create-title">질문 작성</h1>
          <form onSubmit={handleSubmit} className="question-create-form">
            <Input
              name="title"
              label="제목"
              placeholder="질문 제목을 입력하세요"
              value={formData.title}
              onChange={handleChange}
              error={errors.title}
              required
            />
            <Textarea
              name="content"
              label="내용"
              placeholder="질문 내용을 입력하세요"
              value={formData.content}
              onChange={handleChange}
              error={errors.content}
              rows={10}
              required
            />
            <div className="question-create-actions">
              <Button
                type="button"
                variant="outline"
                onClick={() => navigate('/questions')}
              >
                취소
              </Button>
              <Button type="submit" disabled={loading}>
                {loading ? '작성 중...' : '작성하기'}
              </Button>
            </div>
          </form>
        </div>
      </div>
    </Layout>
  );
};

export default QuestionCreatePage;

