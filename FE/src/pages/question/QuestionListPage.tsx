import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Layout } from '@/components/layout/Layout';
import { Button } from '@/components/common/Button';
import { Input } from '@/components/common/Input';
import { questionApi } from '@/api/question/questionApi';
import { formatDate } from '@/utils/date';
import type { SearchType } from '@/types';
import './QuestionListPage.css';

const QuestionListPage = () => {
  const [questions, setQuestions] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchType, setSearchType] = useState<SearchType>('all');
  const [keyword, setKeyword] = useState('');
  const [searchKeyword, setSearchKeyword] = useState('');
  const [isSearching, setIsSearching] = useState(false);
  const [isInitialLoad, setIsInitialLoad] = useState(true);

  // 초기 로딩: 전체 목록만 가져오기
  useEffect(() => {
    loadQuestions('all', '', 0);
    setIsInitialLoad(false);
  }, []);

  // 페이지네이션: 현재 검색 상태 유지하면서 페이지 변경
  useEffect(() => {
    // 초기 로딩이 아닐 때만 실행
    if (!isInitialLoad) {
      loadQuestions(searchType, searchKeyword, page);
    }
  }, [page]);

  const loadQuestions = async (type: SearchType, keyword: string, pageNum: number) => {
    setLoading(true);
    try {
      const data = await questionApi.getList(type, keyword, pageNum, 10);
      setQuestions(data.content);
      setTotalPages(data.totalPages);
    } catch (error) {
      console.error('Failed to load questions:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = () => {
    setIsInitialLoad(false); // 검색 후에는 페이지네이션이 작동하도록
    
    if (!keyword.trim()) {
      // 키워드가 없으면 전체 목록으로 초기화
      setIsSearching(false);
      setSearchKeyword('');
      setSearchType('all');
      setPage(0);
      loadQuestions('all', '', 0);
      return;
    }
    
    // 검색 실행
    setIsSearching(true);
    setSearchKeyword(keyword.trim());
    setPage(0);
    loadQuestions(searchType, keyword.trim(), 0);
  };

  const handleReset = () => {
    setKeyword('');
    setSearchType('all');
    setSearchKeyword('');
    setIsSearching(false);
    setPage(0);
    setIsInitialLoad(false); // 초기화 후에는 페이지네이션이 작동하도록
    loadQuestions('all', '', 0);
  };

  return (
    <Layout>
      <div className="question-list-container">
        <div className="question-list-header">
          <h1>질문 목록</h1>
          <Link to="/questions/create">
            <Button>질문 작성</Button>
          </Link>
        </div>
        <div className="question-list-search">
          <select
            value={searchType}
            onChange={(e) => setSearchType(e.target.value as SearchType)}
            className="question-search-select"
          >
            <option value="all">전체</option>
            <option value="title">제목</option>
            <option value="content">내용</option>
          </select>
          <Input
            type="text"
            name="keyword"
            placeholder="검색어를 입력하세요"
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            onKeyPress={(e) => {
              if (e.key === 'Enter') {
                handleSearch();
              }
            }}
          />
          <Button onClick={handleSearch}>검색</Button>
          {isSearching && (
            <Button variant="outline" onClick={handleReset}>
              초기화
            </Button>
          )}
        </div>
        {loading ? (
          <div className="question-list-loading">로딩 중...</div>
        ) : questions.length === 0 ? (
          <div className="question-list-empty">질문이 없습니다.</div>
        ) : (
          <>
            <div className="question-list">
              {questions.map((question) => (
                <Link
                  key={question.id}
                  to={`/questions/${question.id}`}
                  className="question-card"
                >
                  <h3 className="question-card-title">{question.title}</h3>
                  <div className="question-card-meta">
                    <span className="question-card-author">{question.nickname}</span>
                    <span className="question-card-date">{formatDate(question.createdAt)}</span>
                  </div>
                </Link>
              ))}
            </div>
            <div className="question-list-pagination">
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

export default QuestionListPage;

