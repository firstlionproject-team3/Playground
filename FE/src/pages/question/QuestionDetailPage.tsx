import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { Layout } from '@/components/layout/Layout';
import { Button } from '@/components/common/Button';
import { questionApi } from '@/api/question/questionApi';
import { formatDate } from '@/utils/date';
import { useAuthContext } from '@/contexts/AuthContext';
import './QuestionDetailPage.css';

const QuestionDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuthContext();
  const [question, setQuestion] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    if (id) {
      loadQuestion();
    }
  }, [id]);

  const loadQuestion = async () => {
    try {
      const data = await questionApi.getDetail(Number(id));
      setQuestion(data);
    } catch (error) {
      console.error('Failed to load question:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!confirm('정말 삭제하시겠습니까?')) return;

    setDeleting(true);
    try {
      await questionApi.delete(Number(id));
      navigate('/questions');
    } catch (error) {
      console.error('Failed to delete question:', error);
      alert('삭제에 실패했습니다.');
    } finally {
      setDeleting(false);
    }
  };

  const handleReport = async () => {
    if (!confirm('이 질문을 신고하시겠습니까?')) return;

    try {
      await questionApi.report(Number(id));
      alert('신고가 접수되었습니다.');
    } catch (error) {
      console.error('Failed to report question:', error);
      alert('신고에 실패했습니다.');
    }
  };

  if (loading) {
    return (
      <Layout>
        <div className="question-detail-loading">로딩 중...</div>
      </Layout>
    );
  }

  if (!question) {
    return (
      <Layout>
        <div className="question-detail-error">질문을 찾을 수 없습니다.</div>
      </Layout>
    );
  }

  const isOwner = isAuthenticated && user?.nickname === question.nickname;

  return (
    <Layout>
      <div className="question-detail-container">
        <div className="question-detail-header">
          <h1 className="question-detail-title">{question.title}</h1>
          <div className="question-detail-meta">
            <span className="question-detail-author">{question.nickname}</span>
            <span className="question-detail-date">{formatDate(question.createdAt)}</span>
            <span className="question-detail-views">조회 {question.viewCount}</span>
          </div>
        </div>
        <div className="question-detail-content">{question.content}</div>
        {isOwner && (
          <div className="question-detail-actions">
            <Link to={`/questions/${id}/edit`}>
              <Button variant="outline">수정</Button>
            </Link>
            <Button variant="danger" onClick={handleDelete} disabled={deleting}>
              {deleting ? '삭제 중...' : '삭제'}
            </Button>
          </div>
        )}
        {!isOwner && isAuthenticated && (
          <div className="question-detail-actions">
            <Button variant="outline" onClick={handleReport}>
              신고
            </Button>
          </div>
        )}
        <div className="question-detail-answers">
          <h2>답변 ({question.answers?.length || 0})</h2>
          {question.answers?.map((answer: any) => (
            <div key={answer.id} className="answer-card">
              <div className="answer-header">
                <span className="answer-author">{answer.nickname}</span>
                <span className="answer-date">{formatDate(answer.createdAt)}</span>
                {answer.accepted && <span className="answer-accepted">✓ 채택됨</span>}
              </div>
              <div className="answer-content">{answer.content}</div>
              <div className="answer-reactions">
                <span>👍 {answer.likeCount}</span>
                <span>👎 {answer.dislikeCount}</span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </Layout>
  );
};

export default QuestionDetailPage;

