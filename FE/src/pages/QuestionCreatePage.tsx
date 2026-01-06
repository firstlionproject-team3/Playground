import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { questionApi } from '@/api/question';
import { validateTitle, validateContent } from '@/utils/validation';
import toast from 'react-hot-toast';

export default function QuestionCreatePage() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    title: '',
    content: '',
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [loading, setLoading] = useState(false);

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
    const titleError = validateTitle(formData.title);
    const contentError = validateContent(formData.content);

    if (titleError) newErrors.title = titleError;
    if (contentError) newErrors.content = contentError;

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    setLoading(true);
    try {
      const { id } = await questionApi.create(formData);
      toast.success('질문이 등록되었습니다!');
      navigate(`/questions/${id}`);
    } catch (error: any) {
      toast.error(error.response?.data?.message || '질문 등록에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gray-900">질문 작성</h1>
        <p className="mt-2 text-gray-600">궁금한 점을 질문해보세요</p>
      </div>

      <div className="card">
        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label htmlFor="title" className="block text-sm font-medium text-gray-700 mb-1">
              제목
            </label>
            <input
              id="title"
              name="title"
              type="text"
              required
              value={formData.title}
              onChange={handleChange}
              className={`input-field text-lg ${errors.title ? 'border-red-500' : ''}`}
              placeholder="질문 제목을 입력하세요"
            />
            {errors.title && (
              <p className="mt-1 text-sm text-red-600">{errors.title}</p>
            )}
          </div>

          <div>
            <label htmlFor="content" className="block text-sm font-medium text-gray-700 mb-1">
              내용
            </label>
            <textarea
              id="content"
              name="content"
              required
              rows={15}
              value={formData.content}
              onChange={handleChange}
              className={`input-field font-mono text-sm ${errors.content ? 'border-red-500' : ''}`}
              placeholder="질문 내용을 상세히 입력하세요&#10;&#10;코드블럭은 다음과 같이 작성하세요:&#10;```&#10;코드 내용&#10;```"
            />
            <p className="mt-1 text-xs text-gray-500">
              코드블럭은 ```로 감싸서 작성하세요
            </p>
            {errors.content && (
              <p className="mt-1 text-sm text-red-600">{errors.content}</p>
            )}
          </div>

          <div className="flex justify-end space-x-3">
            <button
              type="button"
              onClick={() => navigate(-1)}
              className="btn-secondary"
            >
              취소
            </button>
            <button
              type="submit"
              disabled={loading}
              className="btn-primary disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? '등록 중...' : '질문 등록'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

