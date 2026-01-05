import { useState, useEffect } from 'react';
import { Layout } from '@/components/layout/Layout';
import { Button } from '@/components/common/Button';
import { adminUserApi } from '@/api/user/adminUserApi';
import { getErrorMessage } from '@/utils/error';
import './AdminUserListPage.css';

const AdminUserListPage = () => {
  const [users, setUsers] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    loadUsers();
  }, [page]);

  const loadUsers = async () => {
    try {
      const data = await adminUserApi.getUserList(page, 20);
      setUsers(data.content);
      setTotalPages(data.totalPages);
    } catch (error) {
      console.error('Failed to load users:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('정말 이 사용자를 삭제하시겠습니까?')) return;

    try {
      await adminUserApi.deleteUser(id);
      loadUsers();
    } catch (error) {
      alert(`삭제 실패: ${getErrorMessage(error)}`);
    }
  };

  return (
    <Layout>
      <div className="admin-user-list-container">
        <h1>사용자 관리</h1>
        {loading ? (
          <div className="admin-user-list-loading">로딩 중...</div>
        ) : (
          <>
            <table className="admin-user-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>로그인 ID</th>
                  <th>닉네임</th>
                  <th>이메일</th>
                  <th>역할</th>
                  <th>상태</th>
                  <th>작업</th>
                </tr>
              </thead>
              <tbody>
                {users.map((user) => (
                  <tr key={user.id}>
                    <td>{user.id}</td>
                    <td>{user.loginId}</td>
                    <td>{user.nickname}</td>
                    <td>{user.email}</td>
                    <td>{user.role}</td>
                    <td>{user.status}</td>
                    <td>
                      <Button
                        variant="danger"
                        size="small"
                        onClick={() => handleDelete(user.id)}
                      >
                        삭제
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <div className="admin-user-pagination">
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

export default AdminUserListPage;

