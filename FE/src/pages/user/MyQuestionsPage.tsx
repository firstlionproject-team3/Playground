import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Layout } from '@/components/layout/Layout';
import { Button } from '@/components/common/Button';
import { userApi } from '@/api/user/userApi';
import { formatDate } from '@/utils/date';
import './MyQuestionsPage.css';

const MyQuestionsPage = () => {
  const [questions, setQuestions] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    loadQuestions();
  }, [page]);

  const loadQuestions = async () => {
    try {
      const data = await userApi.getMyQuestions(page, 20);
      setQuestions(data.content);
      setTotalPages(data.totalPages);
    } catch (error) {
      console.error('Failed to load questions:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout>
      <div className="my-questions-container">
        <h1>내 질문</h1>
        {loading ? (
          <div className="my-questions-loading">로딩 중...</div>
        ) : questions.length === 0 ? (
          <div className="my-questions-empty">작성한 질문이 없습니다.</div>
        ) : (
          <>
            <div className="my-questions-list">
              {questions.map((question) => (
                <Link
                  key={question.id}
                  to={`/questions/${question.id}`}
                  className="my-question-card"
                >
                  <h3>{question.title}</h3>
                  <span>{formatDate(question.createdAt)}</span>
                </Link>
              ))}
            </div>
            <div className="my-questions-pagination">
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

export default MyQuestionsPage;

