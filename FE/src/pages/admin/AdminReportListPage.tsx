import { useState, useEffect } from 'react';
import { Layout } from '@/components/layout/Layout';
import { Button } from '@/components/common/Button';
import { reportApi } from '@/api/report/reportApi';
import { getErrorMessage } from '@/utils/error';
import { formatDate } from '@/utils/date';
import './AdminReportListPage.css';

const AdminReportListPage = () => {
  const [reports, setReports] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    loadReports();
  }, [page]);

  const loadReports = async () => {
    try {
      const data = await reportApi.getList(page, 10);
      setReports(data.content);
      setTotalPages(data.totalPages);
    } catch (error) {
      console.error('Failed to load reports:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (id: number) => {
    if (!confirm('이 신고를 승인하시겠습니까?')) return;

    try {
      await reportApi.approve(id);
      loadReports();
    } catch (error) {
      alert(`승인 실패: ${getErrorMessage(error)}`);
    }
  };

  const handleReject = async (id: number) => {
    if (!confirm('이 신고를 거부하시겠습니까?')) return;

    try {
      await reportApi.reject(id);
      loadReports();
    } catch (error) {
      alert(`거부 실패: ${getErrorMessage(error)}`);
    }
  };

  return (
    <Layout>
      <div className="admin-report-list-container">
        <h1>신고 관리</h1>
        {loading ? (
          <div className="admin-report-list-loading">로딩 중...</div>
        ) : (
          <>
            <div className="admin-report-list">
              {reports.map((report) => (
                <div key={report.id} className="admin-report-card">
                  <div className="admin-report-header">
                    <span>신고 ID: {report.id}</span>
                    <span>상태: {report.status}</span>
                  </div>
                  <div className="admin-report-info">
                    <p>대상 타입: {report.entityType}</p>
                    <p>대상 ID: {report.entityId}</p>
                    <p>카테고리: {report.category}</p>
                    {report.reasonDetail && <p>상세: {report.reasonDetail}</p>}
                    <p>신고일: {formatDate(report.createdAt)}</p>
                  </div>
                  {report.status === 'PENDING' && (
                    <div className="admin-report-actions">
                      <Button
                        variant="danger"
                        size="small"
                        onClick={() => handleApprove(report.id)}
                      >
                        승인
                      </Button>
                      <Button
                        variant="outline"
                        size="small"
                        onClick={() => handleReject(report.id)}
                      >
                        거부
                      </Button>
                    </div>
                  )}
                </div>
              ))}
            </div>
            <div className="admin-report-pagination">
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

export default AdminReportListPage;

