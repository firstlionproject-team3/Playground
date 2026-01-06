import { useState, useEffect } from 'react';
import { adminApi } from '@/api/admin';
import { reportApi } from '@/api/report';
import { PageResponse, ReportResponse } from '@/types';
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

  useEffect(() => {
    if (activeTab === 'users') {
      loadUsers();
    } else {
      loadReports();
    }
  }, [page, reportsPage, activeTab]);

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
    try {
      await reportApi.reject(reportId);
      toast.success('신고가 거부되었습니다.');
      loadReports();
    } catch (error: any) {
      toast.error(error.response?.data?.message || '거부에 실패했습니다.');
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
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">유형</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">카테고리</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">사유</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">상태</th>
                        <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">작업</th>
                      </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                      {reports.map((report) => (
                        <tr key={report.id} className="hover:bg-gray-50">
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{report.id}</td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{report.entityType}</td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{report.category}</td>
                          <td className="px-6 py-4 text-sm text-gray-900 max-w-xs truncate">{report.reasonDetail || '-'}</td>
                          <td className="px-6 py-4 whitespace-nowrap">
                            <span className={`px-2 py-1 text-xs rounded-full ${
                              report.status === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                              report.status === 'APPROVED' ? 'bg-green-100 text-green-800' :
                              'bg-red-100 text-red-800'
                            }`}>
                              {report.status === 'PENDING' ? '대기중' :
                               report.status === 'APPROVED' ? '승인됨' : '거부됨'}
                            </span>
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                            {report.status === 'PENDING' && (
                              <div className="flex justify-end space-x-2">
                                <button
                                  onClick={() => handleApproveReport(report.id)}
                                  className="p-2 text-green-600 hover:text-green-700 hover:bg-green-50 rounded-lg transition-colors"
                                  title="승인"
                                >
                                  <CheckCircle className="w-4 h-4" />
                                </button>
                                <button
                                  onClick={() => handleRejectReport(report.id)}
                                  className="p-2 text-red-600 hover:text-red-700 hover:bg-red-50 rounded-lg transition-colors"
                                  title="거부"
                                >
                                  <X className="w-4 h-4" />
                                </button>
                              </div>
                            )}
                            {report.entityType === 'QUESTION' && (
                              <Link
                                to={`/questions/${report.entityId}`}
                                className="text-primary-600 hover:text-primary-700"
                              >
                                보기
                              </Link>
                            )}
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
    </div>
  );
}

