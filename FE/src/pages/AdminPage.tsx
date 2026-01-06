import { useState, useEffect } from 'react';
import { adminApi } from '@/api/admin';
import { reportApi } from '@/api/report';
import { questionApi } from '@/api/question';
import { answerApi } from '@/api/answer';
import { commentApi } from '@/api/comment';
import { PageResponse, ReportResponse, QuestionResponse, AnswerDetail, CommentResponse, ReportCategory } from '@/types';
import { formatDate } from '@/utils/date';
import toast from 'react-hot-toast';
import { Trash2, Flag, CheckCircle, X } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function AdminPage() {
  const [users, setUsers] = useState<any[]>([]);
  const [reports, setReports] = useState<ReportResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [reportsLoading, setReportsLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [reportsPage, setReportsPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [reportsTotalPages, setReportsTotalPages] = useState(0);
  const [activeTab, setActiveTab] = useState<'users' | 'reports'>('users');
  const [selectedReport, setSelectedReport] = useState<ReportResponse | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [entityContent, setEntityContent] = useState<{ type: 'QUESTION' | 'ANSWER' | 'COMMENT'; data: QuestionResponse | AnswerDetail | CommentResponse | null } | null>(null);
  const [isEntityModalOpen, setIsEntityModalOpen] = useState(false);
  const [entityLoading, setEntityLoading] = useState(false);

  useEffect(() => {
    if (activeTab === 'users') {
      loadUsers();
    } else {
      loadReports();
    }
  }, [page, reportsPage, activeTab]);

  useEffect(() => {
    const handleEscape = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        if (isModalOpen) handleCloseModal();
        if (isEntityModalOpen) handleCloseEntityModal();
      }
    };
    window.addEventListener('keydown', handleEscape);
    return () => window.removeEventListener('keydown', handleEscape);
  }, [isModalOpen, isEntityModalOpen]);

  const loadUsers = async () => {
    setLoading(true);
    try {
      const data: PageResponse<any> = await adminApi.getUsers(page, 20);
      setUsers(data.content);
      setTotalPages(data.totalPages);
    } catch (error: any) {
      console.error('사용자 목록 로드 실패:', error);
      toast.error(error.response?.data?.message || '사용자 목록을 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const loadReports = async () => {
    setReportsLoading(true);
    try {
      const data = await reportApi.getReports(reportsPage, 10);
      setReports(data.content);
      setReportsTotalPages(data.totalPages);
    } catch (error: any) {
      console.error('신고 목록 로드 실패:', error);
      toast.error(error.response?.data?.message || '신고 목록을 불러오는데 실패했습니다.');
    } finally {
      setReportsLoading(false);
    }
  };

  const handleDeleteUser = async (id: number) => {
    if (!confirm('정말 이 사용자를 삭제하시겠습니까?')) return;
    try {
      await adminApi.deleteUser(id);
      toast.success('사용자가 삭제되었습니다.');
      loadUsers();
    } catch (error: any) {
      toast.error(error.response?.data?.message || '삭제에 실패했습니다.');
    }
  };

  const handleApproveReport = async (reportId: number) => {
    try {
      await reportApi.approve(reportId);
      toast.success('신고가 승인되었습니다.');
      loadReports();
    } catch (error: any) {
      toast.error(error.response?.data?.message || '승인에 실패했습니다.');
    }
  };

  const handleRejectReport = async (reportId: number) => {
    if (!confirm('이 신고를 거부하시겠습니까?')) return;
    try {
      await reportApi.reject(reportId);
      toast.success('신고가 거부되었습니다.');
      loadReports();
    } catch (error: any) {
      toast.error(error.response?.data?.message || '거부에 실패했습니다.');
    }
  };

  const handleOpenModal = (report: ReportResponse) => {
    setSelectedReport(report);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedReport(null);
  };

  const handleOpenEntityModal = async (report: ReportResponse) => {
    if (report.entityType === 'USER') {
      toast.info('사용자 신고는 내용을 볼 수 없습니다.');
      return;
    }

    setEntityLoading(true);
    setIsEntityModalOpen(true);
    
    try {
      if (report.entityType === 'QUESTION') {
        const data = await questionApi.getQuestion(report.entityId);
        setEntityContent({ type: 'QUESTION', data });
      } else if (report.entityType === 'ANSWER') {
        const data = await answerApi.getAnswer(report.entityId);
        setEntityContent({ type: 'ANSWER', data });
      } else if (report.entityType === 'COMMENT') {
        const data = await commentApi.getComment(report.entityId);
        setEntityContent({ type: 'COMMENT', data });
      }
    } catch (error: any) {
      console.error('항목 조회 실패:', error);
      toast.error(error.response?.data?.message || '항목을 불러오는데 실패했습니다.');
      setIsEntityModalOpen(false);
      setEntityContent(null);
    } finally {
      setEntityLoading(false);
    }
  };

  const handleCloseEntityModal = () => {
    setIsEntityModalOpen(false);
    setEntityContent(null);
  };

  const getReportCategoryLabel = (category: ReportCategory): string => {
    const categoryMap: Record<ReportCategory, string> = {
      SPAM: '스팸/광고',
      ABUSE: '욕설/비방',
      INAPPROPRIATE: '부적절한 내용',
      COPYRIGHT: '저작권 침해',
      MISINFORMATION: '허위 정보',
      ETC: '기타',
    };
    return categoryMap[category] || category;
  };

  if (loading) {
    return (
      <div className="text-center py-12">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto"></div>
        <p className="mt-4 text-gray-600">로딩 중...</p>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto">
      <h1 className="text-3xl font-bold text-gray-900 mb-6">관리자 페이지</h1>

      <div className="card">
        <div className="border-b border-gray-200 mb-4">
          <nav className="flex space-x-4">
            <button
              onClick={() => setActiveTab('users')}
              className={`px-4 py-2 font-medium ${
                activeTab === 'users'
                  ? 'text-primary-600 border-b-2 border-primary-600'
                  : 'text-gray-500 hover:text-gray-700'
              }`}
            >
              사용자 관리
            </button>
            <button
              onClick={() => setActiveTab('reports')}
              className={`px-4 py-2 font-medium flex items-center space-x-2 ${
                activeTab === 'reports'
                  ? 'text-primary-600 border-b-2 border-primary-600'
                  : 'text-gray-500 hover:text-gray-700'
              }`}
            >
              <Flag className="w-4 h-4" />
              <span>신고 목록</span>
            </button>
          </nav>
        </div>

        {activeTab === 'users' ? (
          <>
            <h2 className="text-xl font-semibold mb-4">사용자 관리</h2>

            {users.length === 0 ? (
              <p className="text-center text-gray-500 py-8">사용자가 없습니다.</p>
            ) : (
              <>
                <div className="overflow-x-auto">
                  <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      ID
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      닉네임
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      이메일
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      가입일
                    </th>
                    <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                      작업
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {users.map((user) => (
                    <tr key={user.id} className="hover:bg-gray-50">
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {user.id}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {user.nickname}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {user.email || '-'}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {user.joinedDate
                          ? new Date(user.joinedDate).toLocaleDateString('ko-KR')
                          : '-'}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                        <div className="flex justify-end space-x-2">
                          <button
                            onClick={() => handleDeleteUser(user.id)}
                            className="p-2 text-red-600 hover:text-red-700 hover:bg-red-50 rounded-lg transition-colors"
                            title="삭제"
                          >
                            <Trash2 className="w-4 h-4" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                  </tbody>
                </table>
              </div>

              {totalPages > 1 && (
                <div className="mt-4 flex justify-center space-x-2">
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
          </>
        ) : (
          <>
            <h2 className="text-xl font-semibold mb-4">신고 목록</h2>
            {reportsLoading ? (
              <div className="text-center py-12">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto"></div>
                <p className="mt-4 text-gray-600">로딩 중...</p>
              </div>
            ) : reports.length === 0 ? (
              <p className="text-center text-gray-500 py-8">신고가 없습니다.</p>
            ) : (
              <>
                <div className="overflow-x-auto">
                  <table className="min-w-full divide-y divide-gray-200">
                    <thead className="bg-gray-50">
                      <tr>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">신고자</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">신고당한 사람</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">대상</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">유형</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">내용</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">상태</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">날짜</th>
                        <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">작업</th>
                      </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                      {reports.map((report) => (
                        <tr key={report.reportId} className="hover:bg-gray-50">
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{report.reporterNickname}</td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{report.reportedNickname}</td>
                          <td 
                            className="px-6 py-4 whitespace-nowrap text-sm text-gray-900 cursor-pointer hover:text-primary-600 hover:underline transition-colors"
                            onClick={() => handleOpenEntityModal(report)}
                            title="클릭하여 신고된 항목 내용 보기"
                          >
                            {report.entityType === 'QUESTION' ? '질문' :
                             report.entityType === 'ANSWER' ? '답변' :
                             report.entityType === 'COMMENT' ? '댓글' : '사용자'}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{getReportCategoryLabel(report.reportCategory)}</td>
                          <td 
                            className="px-6 py-4 text-sm text-gray-900 max-w-xs truncate cursor-pointer hover:text-primary-600 hover:underline transition-colors"
                            onClick={() => handleOpenModal(report)}
                            title="클릭하여 전체 내용 보기"
                          >
                            {report.detail || '-'}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap">
                            <span className={`px-2 py-1 text-xs rounded-full ${
                              report.reportStatus === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                              report.reportStatus === 'APPROVED' ? 'bg-green-100 text-green-800' :
                              'bg-red-100 text-red-800'
                            }`}>
                              {report.reportStatus === 'PENDING' ? '대기중' :
                               report.reportStatus === 'APPROVED' ? '승인됨' : '거부됨'}
                            </span>
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                            {formatDate(report.reportDate)}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                            <div className="flex justify-end items-center space-x-2">
                              {report.reportStatus === 'PENDING' && (
                                <>
                                  <button
                                    onClick={() => handleApproveReport(report.reportId)}
                                    className="p-2 text-green-600 hover:text-green-700 hover:bg-green-50 rounded-lg transition-colors"
                                    title="승인"
                                  >
                                    <CheckCircle className="w-4 h-4" />
                                  </button>
                                  <button
                                    onClick={() => handleRejectReport(report.reportId)}
                                    className="p-2 text-red-600 hover:text-red-700 hover:bg-red-50 rounded-lg transition-colors"
                                    title="거부"
                                  >
                                    <X className="w-4 h-4" />
                                  </button>
                                </>
                              )}
                              {(report.entityType === 'QUESTION' || report.entityType === 'ANSWER' || report.entityType === 'COMMENT') && (
                                <Link
                                  to={report.entityType === 'QUESTION' ? `/questions/${report.entityId}` : '#'}
                                  className="text-primary-600 hover:text-primary-700 text-sm"
                                >
                                  보기
                                </Link>
                              )}
                            </div>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
                {reportsTotalPages > 1 && (
                  <div className="mt-4 flex justify-center space-x-2">
                    <button
                      onClick={() => setReportsPage((p) => Math.max(0, p - 1))}
                      disabled={reportsPage === 0}
                      className="px-4 py-2 border border-gray-300 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                    >
                      이전
                    </button>
                    <span className="px-4 py-2 text-gray-700">
                      {reportsPage + 1} / {reportsTotalPages}
                    </span>
                    <button
                      onClick={() => setReportsPage((p) => Math.min(reportsTotalPages - 1, p + 1))}
                      disabled={reportsPage >= reportsTotalPages - 1}
                      className="px-4 py-2 border border-gray-300 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                    >
                      다음
                    </button>
                  </div>
                )}
              </>
            )}
          </>
        )}
      </div>

      {/* 신고된 항목 내용 모달 */}
      {isEntityModalOpen && (
        <div 
          className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50"
          onClick={handleCloseEntityModal}
        >
          <div 
            className="bg-white rounded-lg shadow-xl max-w-3xl w-full mx-4 max-h-[90vh] overflow-y-auto"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="p-6">
              <div className="flex justify-between items-center mb-4">
                <h3 className="text-xl font-bold text-gray-900">
                  {entityContent?.type === 'QUESTION' ? '질문 내용' :
                   entityContent?.type === 'ANSWER' ? '답변 내용' :
                   entityContent?.type === 'COMMENT' ? '댓글 내용' : '내용'}
                </h3>
                <button
                  onClick={handleCloseEntityModal}
                  className="text-gray-400 hover:text-gray-600 transition-colors"
                >
                  <X className="w-6 h-6" />
                </button>
              </div>

              {entityLoading ? (
                <div className="text-center py-12">
                  <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto"></div>
                  <p className="mt-4 text-gray-600">로딩 중...</p>
                </div>
              ) : entityContent?.data ? (
                <div className="space-y-4">
                  {entityContent.type === 'QUESTION' && (
                    <div>
                      <p className="text-sm font-medium text-gray-500 mb-2">제목</p>
                      <p className="text-lg font-semibold text-gray-900 mb-4">
                        {(entityContent.data as QuestionResponse).title}
                      </p>
                      <p className="text-sm font-medium text-gray-500 mb-2">내용</p>
                      <div className="bg-gray-50 rounded-lg p-4 min-h-[100px]">
                        <p className="text-sm text-gray-900 whitespace-pre-wrap">
                          {(entityContent.data as QuestionResponse).content}
                        </p>
                      </div>
                    </div>
                  )}
                  {entityContent.type === 'ANSWER' && (
                    <div>
                      <p className="text-sm font-medium text-gray-500 mb-2">작성자</p>
                      <p className="text-sm text-gray-900 mb-4">
                        {(entityContent.data as AnswerDetail).nickname}
                      </p>
                      <p className="text-sm font-medium text-gray-500 mb-2">내용</p>
                      <div className="bg-gray-50 rounded-lg p-4 min-h-[100px]">
                        <p className="text-sm text-gray-900 whitespace-pre-wrap">
                          {(entityContent.data as AnswerDetail).content}
                        </p>
                      </div>
                    </div>
                  )}
                  {entityContent.type === 'COMMENT' && (
                    <div>
                      <p className="text-sm font-medium text-gray-500 mb-2">작성자</p>
                      <p className="text-sm text-gray-900 mb-4">
                        {(entityContent.data as CommentResponse).nickname}
                      </p>
                      <p className="text-sm font-medium text-gray-500 mb-2">내용</p>
                      <div className="bg-gray-50 rounded-lg p-4 min-h-[100px]">
                        <p className="text-sm text-gray-900 whitespace-pre-wrap">
                          {(entityContent.data as CommentResponse).content}
                        </p>
                      </div>
                    </div>
                  )}
                </div>
              ) : (
                <p className="text-center text-gray-500 py-8">내용을 불러올 수 없습니다.</p>
              )}

              <div className="mt-6 flex justify-end">
                <button
                  onClick={handleCloseEntityModal}
                  className="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors"
                >
                  닫기
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* 신고 내용 모달 */}
      {isModalOpen && selectedReport && (
        <div 
          className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50"
          onClick={handleCloseModal}
        >
          <div 
            className="bg-white rounded-lg shadow-xl max-w-2xl w-full mx-4 max-h-[90vh] overflow-y-auto"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="p-6">
              <div className="flex justify-between items-center mb-4">
                <h3 className="text-xl font-bold text-gray-900">신고 내용</h3>
                <button
                  onClick={handleCloseModal}
                  className="text-gray-400 hover:text-gray-600 transition-colors"
                >
                  <X className="w-6 h-6" />
                </button>
              </div>

              <div className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <p className="text-sm font-medium text-gray-500 mb-1">신고 ID</p>
                    <p className="text-sm text-gray-900">{selectedReport.reportId}</p>
                  </div>
                  <div>
                    <p className="text-sm font-medium text-gray-500 mb-1">상태</p>
                    <span className={`inline-block px-2 py-1 text-xs rounded-full ${
                      selectedReport.reportStatus === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                      selectedReport.reportStatus === 'APPROVED' ? 'bg-green-100 text-green-800' :
                      'bg-red-100 text-red-800'
                    }`}>
                      {selectedReport.reportStatus === 'PENDING' ? '대기중' :
                       selectedReport.reportStatus === 'APPROVED' ? '승인됨' : '거부됨'}
                    </span>
                  </div>
                  <div>
                    <p className="text-sm font-medium text-gray-500 mb-1">신고자</p>
                    <p className="text-sm text-gray-900">{selectedReport.reporterNickname}</p>
                  </div>
                  <div>
                    <p className="text-sm font-medium text-gray-500 mb-1">신고당한 사람</p>
                    <p className="text-sm text-gray-900">{selectedReport.reportedNickname}</p>
                  </div>
                  <div>
                    <p className="text-sm font-medium text-gray-500 mb-1">대상</p>
                    <p className="text-sm text-gray-900">
                      {selectedReport.entityType === 'QUESTION' ? '질문' :
                       selectedReport.entityType === 'ANSWER' ? '답변' :
                       selectedReport.entityType === 'COMMENT' ? '댓글' : '사용자'}
                    </p>
                  </div>
                  <div>
                    <p className="text-sm font-medium text-gray-500 mb-1">대상 ID</p>
                    <p className="text-sm text-gray-900">{selectedReport.entityId}</p>
                  </div>
                  <div>
                    <p className="text-sm font-medium text-gray-500 mb-1">신고 유형</p>
                    <p className="text-sm text-gray-900">{getReportCategoryLabel(selectedReport.reportCategory)}</p>
                  </div>
                  <div>
                    <p className="text-sm font-medium text-gray-500 mb-1">신고 날짜</p>
                    <p className="text-sm text-gray-900">{formatDate(selectedReport.reportDate)}</p>
                  </div>
                </div>

                <div className="border-t pt-4">
                  <p className="text-sm font-medium text-gray-500 mb-2">신고 내용</p>
                  <div className="bg-gray-50 rounded-lg p-4 min-h-[100px]">
                    <p className="text-sm text-gray-900 whitespace-pre-wrap">
                      {selectedReport.detail || '내용이 없습니다.'}
                    </p>
                  </div>
                </div>
              </div>

              <div className="mt-6 flex justify-end space-x-3">
                {selectedReport.reportStatus === 'PENDING' && (
                  <>
                    <button
                      onClick={() => {
                        handleApproveReport(selectedReport.reportId);
                        handleCloseModal();
                      }}
                      className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors flex items-center space-x-2"
                    >
                      <CheckCircle className="w-4 h-4" />
                      <span>승인</span>
                    </button>
                    <button
                      onClick={() => {
                        handleRejectReport(selectedReport.reportId);
                        handleCloseModal();
                      }}
                      className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors flex items-center space-x-2"
                    >
                      <X className="w-4 h-4" />
                      <span>거부</span>
                    </button>
                  </>
                )}
                <button
                  onClick={handleCloseModal}
                  className="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors"
                >
                  닫기
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

