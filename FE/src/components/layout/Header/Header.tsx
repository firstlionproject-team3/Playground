import { Link, useNavigate } from 'react-router-dom';
import { useAuthContext } from '@/contexts/AuthContext';
import { useNotificationContext } from '@/contexts/NotificationContext';
import { useLogout } from '@/hooks/auth/useLogout';
import { Button } from '@/components/common/Button';
import './Header.css';

export const Header = () => {
  const { isAuthenticated, user, isAdmin } = useAuthContext();
  const { unreadCount } = useNotificationContext();
  const { logout } = useLogout();
  const navigate = useNavigate();

  return (
    <header className="header">
      <div className="header-container">
        <Link to="/" className="header-logo">
          Playground
        </Link>
        <nav className="header-nav">
          <Link to="/questions" className="header-nav-link">
            질문 목록
          </Link>
          {isAuthenticated && (
            <>
              <Link to="/questions/create" className="header-nav-link">
                질문 작성
              </Link>
              <Link to="/mypage" className="header-nav-link">
                마이페이지
              </Link>
              {isAdmin && (
                <Link to="/admin" className="header-nav-link">
                  관리자
                </Link>
              )}
              <div className="header-notification">
                <Link to="/mypage" className="header-notification-bell">
                  🔔
                  {unreadCount > 0 && (
                    <span className="header-notification-badge">{unreadCount}</span>
                  )}
                </Link>
              </div>
              <span className="header-user">{user?.nickname || '사용자'}</span>
              <Button variant="outline" size="small" onClick={logout}>
                로그아웃
              </Button>
            </>
          )}
          {!isAuthenticated && (
            <>
              <Button variant="outline" size="small" onClick={() => navigate('/login')}>
                로그인
              </Button>
              <Button size="small" onClick={() => navigate('/register')}>
                회원가입
              </Button>
            </>
          )}
        </nav>
      </div>
    </header>
  );
};

