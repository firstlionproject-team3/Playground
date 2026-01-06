import { useState, useEffect } from 'react';
import { questionApi } from '@/api/question';
import { QuestionSummary, PageResponse } from '@/types';
import QuestionCard from '@/components/QuestionCard';
import { Search, Plus } from 'lucide-react';
import { Link } from 'react-router-dom';
import { useAuthStore } from '@/store/authStore';

export default function HomePage() {
  const { isAuthenticated } = useAuthStore();
  const [questions, setQuestions] = useState<QuestionSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchType, setSearchType] = useState<'title' | 'content'>('title');
  const [keyword, setKeyword] = useState('');
  const [searchInput, setSearchInput] = useState('');

  // 페이지 변경 시에만 질문 목록 로드 (searchType 변경 시에는 리셋만)
  useEffect(() => {
    loadQuestions();
  }, [page, keyword]);

  const loadQuestions = async () => {
    setLoading(true);
    try {
      const data: PageResponse<QuestionSummary> = await questionApi.getQuestions(
        page,
        10,
        keyword ? searchType : undefined,
        keyword || undefined
      );
      setQuestions(data.content);
      setTotalPages(data.totalPages);
    } catch (error) {
      console.error('질문 목록 로드 실패:', error);
    } finally {
      setLoading(false);
    }
  };

  // searchType이 변경될 때 검색 실행 (keyword가 있을 때만)
  useEffect(() => {
    if (keyword) {
      setPage(0);
      loadQuestions();
    }
  }, [searchType]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setKeyword(searchInput);
    setPage(0); // 검색 시 첫 페이지로 리셋
  };

  const handleSearchTypeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const newType = e.target.value as 'title' | 'content';
    setSearchType(newType);
    // 타입 변경 시 첫 페이지로 리셋 (useEffect에서 자동으로 검색 실행됨)
    if (keyword) {
      setPage(0);
    }
  };

  const handleClearSearch = () => {
    setKeyword('');
    setSearchInput('');
    setPage(0);
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">질문 목록</h1>
          <p className="mt-2 text-gray-600">다양한 질문들을 탐색해보세요</p>
        </div>
        {isAuthenticated && (
          <Link
            to="/questions/create"
            className="btn-primary flex items-center space-x-2"
          >
            <Plus className="w-5 h-5" />
            <span>질문하기</span>
          </Link>
        )}
      </div>

      <div className="card">
        <form onSubmit={handleSearch} className="flex space-x-4">
          <div className="flex-1 flex space-x-2">
            <select
              value={searchType}
              onChange={handleSearchTypeChange}
              className="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
            >
              <option value="title">제목</option>
              <option value="content">내용</option>
            </select>
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
              <input
                type="text"
                value={searchInput}
                onChange={(e) => setSearchInput(e.target.value)}
                placeholder="검색어를 입력하세요..."
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
              />
            </div>
          </div>
          <button type="submit" className="btn-primary">
            검색
          </button>
          {keyword && (
            <button 
              type="button"
              onClick={handleClearSearch}
              className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
            >
              전체 글 보기
            </button>
          )}
        </form>
      </div>

      {loading ? (
        <div className="text-center py-12">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">로딩 중...</p>
        </div>
      ) : questions.length === 0 ? (
        <div className="card text-center py-12">
          <p className="text-gray-500">
            {keyword ? '검색 결과가 없습니다.' : '질문이 없습니다.'}
          </p>
          {isAuthenticated && !keyword && (
            <Link to="/questions/create" className="mt-4 inline-block btn-primary">
              첫 질문 작성하기
            </Link>
          )}
        </div>
      ) : (
        <>
          <div className="space-y-4">
            {questions.map((question) => (
              <QuestionCard key={question.id} question={question} />
            ))}
          </div>

          {totalPages > 0 && (
            <div className="flex justify-center space-x-2">
              <button
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                disabled={page === 0}
                className="px-4 py-2 border border-gray-300 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
              >
                이전
              </button>
              <span className="px-4 py-2 text-gray-700">
                {page + 1} / {totalPages}
              </span>
              <button
                onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                disabled={page >= totalPages - 1}
                className="px-4 py-2 border border-gray-300 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
              >
                다음
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
}

