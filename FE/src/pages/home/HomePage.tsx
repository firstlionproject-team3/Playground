import { useNavigate } from 'react-router-dom';
import { Layout } from '@/components/layout/Layout';
import { Button } from '@/components/common/Button';
import './HomePage.css';

const HomePage = () => {
  const navigate = useNavigate();

  return (
    <Layout>
      <div className="home-container">
        <div className="home-hero">
          <h1 className="home-title">Playground에 오신 것을 환영합니다</h1>
          <p className="home-description">
            질문하고 답변하며 함께 성장하는 커뮤니티입니다.
          </p>
          <div className="home-actions">
            <Button size="large" onClick={() => navigate('/questions')}>
              질문 보기
            </Button>
            <Button
              variant="outline"
              size="large"
              onClick={() => navigate('/questions/create')}
            >
              질문 작성하기
            </Button>
          </div>
        </div>
      </div>
    </Layout>
  );
};

export default HomePage;

