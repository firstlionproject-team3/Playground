import { useState, useEffect } from 'react';
import { userApi } from '@/api/user';
import { UserMyPageResponse, QuestionSummary, AnswerSummary, PageResponse } from '@/types';
import { formatDate } from '@/utils/date';
import { validateNickname, validateEmail } from '@/utils/validation';
import toast from 'react-hot-toast';
import { Edit, Save, X, Trash2 } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function MyPage() {
  const [user, setUser] = useState<UserMyPageResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [isEditing, setIsEditing] = useState(false);
  const [editData, setEditData] = useState({ nickname: '', email: '' });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [activeTab, setActiveTab] = useState<'questions' | 'answers'>('questions');
  const [questions, setQuestions] = useState<QuestionSummary[]>([]);
  const [answers, setAnswers] = useState<AnswerSummary[]>([]);
  const [questionsPage, setQuestionsPage] = useState(0);
  const [answersPage, setAnswersPage] = useState(0);

  useEffect(() => {
    loadUser();
  }, []);

  useEffect(() => {
    if (activeTab === 'questions') {
      loadQuestions();
    } else {
      loadAnswers();
    }
  }, [activeTab, questionsPage, answersPage]);

  const loadUser = async () => {
    setLoading(true);
    try {
      const data = await userApi.getMyPage();
      setUser(data);
      setEditData({ nickname: data.nickname, email: data.email || '' });
    } catch (error) {
      console.error('사용자 정보 로드 실패:', error);
      toast.error('사용자 정보를 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const loadQuestions = async () => {
    try {
      const data: PageResponse<QuestionSummary> = await userApi.getMyQuestions(questionsPage, 20);
      setQuestions(data.content);
    } catch (error) {
      console.error('질문 목록 로드 실패:', error);
    }
  };

  const loadAnswers = async () => {
    try {
      const data: PageResponse<AnswerSummary> = await userApi.getMyAnswers(answersPage, 20);
      setAnswers(data.content);
    } catch (error) {
      console.error('답변 목록 로드 실패:', error);
    }
  };

  const handleUpdate = async () => {
    const newErrors: Record<string, string> = {};
    const nicknameError = validateNickname(editData.nickname);
    const emailError = validateEmail(editData.email);

    if (nicknameError) newErrors.nickname = nicknameError;
    if (emailError) newErrors.email = emailError;

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    try {
      const updated = await userApi.updateMyPage(editData);
      setUser(updated);
      setIsEditing(false);
      toast.success('정보가 수정되었습니다.');
    } catch (error: any) {
      toast.error(error.response?.data?.message || '수정에 실패했습니다.');
    }
  };

  const handleDeleteAccount = async () => {
    if (!confirm('정말 탈퇴하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) {
      return;
    }
    try {
      await userApi.deleteAccount();
      toast.success('탈퇴되었습니다.');
      window.location.href = '/';
    } catch (error: any) {
      toast.error(error.response?.data?.message || '탈퇴에 실패했습니다.');
    }
  };

  if (loading) {
    return (
      <div className="text-center py-12">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto"></div>
        <p className="mt-4 text-gray-600">로딩 중...</p>
      </div>
    );
  }

  if (!user) {
    return null;
  }

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <h1 className="text-3xl font-bold text-gray-900">마이페이지</h1>

      {/* 사용자 정보 */}
      <div className="card">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-xl font-semibold">내 정보</h2>
          {!isEditing ? (
            <button
              onClick={() => setIsEditing(true)}
              className="flex items-center space-x-1 px-3 py-1.5 text-primary-600 hover:text-primary-700 hover:bg-primary-50 rounded-lg transition-colors"
            >
              <Edit className="w-4 h-4" />
              <span>수정</span>
            </button>
          ) : (
            <div className="flex space-x-2">
              <button
                onClick={handleUpdate}
                className="flex items-center space-x-1 px-3 py-1.5 text-green-600 hover:text-green-700 hover:bg-green-50 rounded-lg transition-colors"
              >
                <Save className="w-4 h-4" />
                <span>저장</span>
              </button>
              <button
                onClick={() => {
                  setIsEditing(false);
                  setEditData({ nickname: user.nickname, email: user.email || '' });
                  setErrors({});
                }}
                className="flex items-center space-x-1 px-3 py-1.5 text-gray-600 hover:text-gray-700 hover:bg-gray-100 rounded-lg transition-colors"
              >
                <X className="w-4 h-4" />
                <span>취소</span>
              </button>
            </div>
          )}
        </div>

        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              닉네임
            </label>
            {isEditing ? (
              <>
                <input
                  type="text"
                  value={editData.nickname}
                  onChange={(e) =>
                    setEditData((prev) => ({ ...prev, nickname: e.target.value }))
                  }
                  className={`input-field ${errors.nickname ? 'border-red-500' : ''}`}
                />
                {errors.nickname && (
                  <p className="mt-1 text-sm text-red-600">{errors.nickname}</p>
                )}
              </>
            ) : (
              <p className="text-gray-900">{user.nickname}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              이메일
            </label>
            {isEditing ? (
              <>
                <input
                  type="email"
                  value={editData.email}
                  onChange={(e) =>
                    setEditData((prev) => ({ ...prev, email: e.target.value }))
                  }
                  className={`input-field ${errors.email ? 'border-red-500' : ''}`}
                />
                {errors.email && (
                  <p className="mt-1 text-sm text-red-600">{errors.email}</p>
                )}
              </>
            ) : (
              <p className="text-gray-900">{user.email || '이메일 없음'}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              가입일
            </label>
            <p className="text-gray-900">{formatDate(user.joinedDate)}</p>
          </div>
        </div>

        <div className="mt-6 pt-6 border-t border-gray-200">
          <button
            onClick={handleDeleteAccount}
            className="flex items-center space-x-1 px-3 py-1.5 text-red-600 hover:text-red-700 hover:bg-red-50 rounded-lg transition-colors"
          >
            <Trash2 className="w-4 h-4" />
            <span>회원 탈퇴</span>
          </button>
        </div>
      </div>

      {/* 탭 */}
      <div className="card">
        <div className="border-b border-gray-200 mb-4">
          <nav className="flex space-x-4">
            <button
              onClick={() => setActiveTab('questions')}
              className={`px-4 py-2 font-medium ${
                activeTab === 'questions'
                  ? 'text-primary-600 border-b-2 border-primary-600'
                  : 'text-gray-500 hover:text-gray-700'
              }`}
            >
              내 질문
            </button>
            <button
              onClick={() => setActiveTab('answers')}
              className={`px-4 py-2 font-medium ${
                activeTab === 'answers'
                  ? 'text-primary-600 border-b-2 border-primary-600'
                  : 'text-gray-500 hover:text-gray-700'
              }`}
            >
              내 답변
            </button>
          </nav>
        </div>

        {activeTab === 'questions' ? (
          <div className="space-y-4">
            {questions.length === 0 ? (
              <p className="text-center text-gray-500 py-8">작성한 질문이 없습니다.</p>
            ) : (
              questions.map((question) => (
                <Link
                  key={question.id}
                  to={`/questions/${question.id}`}
                  className="block p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
                >
                  <h3 className="font-semibold text-gray-900 mb-2">{question.title}</h3>
                  <p className="text-sm text-gray-500">{formatDate(question.createdAt)}</p>
                </Link>
              ))
            )}
          </div>
        ) : (
          <div className="space-y-4">
            {answers.length === 0 ? (
              <p className="text-center text-gray-500 py-8">작성한 답변이 없습니다.</p>
            ) : (
              answers.map((answer) => (
                <div
                  key={answer.id}
                  className="p-4 border border-gray-200 rounded-lg"
                >
                  <p className="text-gray-700 mb-2 line-clamp-2">{answer.content}</p>
                  <div className="flex items-center justify-between text-sm text-gray-500">
                    <span>{formatDate(answer.createdAt)}</span>
                    <div className="flex items-center space-x-2">
                      <span>👍 {answer.likeCount}</span>
                      <span>👎 {answer.dislikeCount}</span>
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>
        )}
      </div>
    </div>
  );
}

