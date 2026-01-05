import { Link } from 'react-router-dom';
import { Layout } from '@/components/layout/Layout';
import { Button } from '@/components/common/Button';
import './AdminDashboard.css';

const AdminDashboard = () => {
  return (
    <Layout>
      <div className="admin-dashboard-container">
        <h1>관리자 대시보드</h1>
        <div className="admin-dashboard-links">
          <Link to="/admin/users">
            <Button size="large">사용자 관리</Button>
          </Link>
          <Link to="/admin/reports">
            <Button size="large">신고 관리</Button>
          </Link>
        </div>
      </div>
    </Layout>
  );
};

export default AdminDashboard;

