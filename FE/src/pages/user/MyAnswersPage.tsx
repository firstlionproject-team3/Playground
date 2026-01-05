import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Layout } from '@/components/layout/Layout';
import { Button } from '@/components/common/Button';
import { userApi } from '@/api/user/userApi';
import { formatDate } from '@/utils/date';
import './MyAnswersPage.css';

const MyAnswersPage = () => {
  const [answers, setAnswers] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    loadAnswers();
  }, [page]);

  const loadAnswers = async () => {
    try {
      const data = await userApi.getMyAnswers(page, 20);
      setAnswers(data.content);
      setTotalPages(data.totalPages);
    } catch (error) {
      console.error('Failed to load answers:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout>
      <div className="my-answers-container">
        <h1>내 답변</h1>
        {loading ? (
          <div className="my-answers-loading">로딩 중...</div>
        ) : answers.length === 0 ? (
          <div className="my-answers-empty">작성한 답변이 없습니다.</div>
        ) : (
          <>
            <div className="my-answers-list">
              {answers.map((answer) => (
                <div key={answer.id} className="my-answer-card">
                  <div className="my-answer-content">{answer.content}</div>
                  <div className="my-answer-meta">
                    <span>{formatDate(answer.createdAt)}</span>
                    {answer.accepted && <span className="my-answer-accepted">✓ 채택됨</span>}
                  </div>
                </div>
              ))}
            </div>
            <div className="my-answers-pagination">
              <Button
                variant="outline"
                disabled={page === 0}
                onClick={() => setPage((p) => p - 1)}
              >
                이전
              </Button>
              <span>
                {page + 1} / {totalPages || 1}
              </span>
              <Button
                variant="outline"
                disabled={page >= totalPages - 1}
                onClick={() => setPage((p) => p + 1)}
              >
                다음
              </Button>
            </div>
          </>
        )}
      </div>
    </Layout>
  );
};

export default MyAnswersPage;

