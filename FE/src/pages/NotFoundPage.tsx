import { Link } from 'react-router-dom';
import { Layout } from '@/components/layout/Layout';
import { Button } from '@/components/common/Button';
import './NotFoundPage.css';

const NotFoundPage = () => {
  return (
    <Layout>
      <div className="not-found-container">
        <h1 className="not-found-title">404</h1>
        <p className="not-found-message">페이지를 찾을 수 없습니다.</p>
        <Link to="/">
          <Button>홈으로 가기</Button>
        </Link>
      </div>
    </Layout>
  );
};

export default NotFoundPage;

