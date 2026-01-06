import { Outlet, Link, useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/store/authStore';
import { authApi } from '@/api/auth';
import { Bell, LogOut, User, PlusCircle, Trash2 } from 'lucide-react';
import { useState, useEffect } from 'react';
import { notificationApi } from '@/api/notification';
import { Notification } from '@/types';
import toast from 'react-hot-toast';

export default function Layout() {
  const { isAuthenticated, user, logout: logoutStore } = useAuthStore();
  const navigate = useNavigate();
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [showNotifications, setShowNotifications] = useState(false);

  useEffect(() => {
    if (isAuthenticated) {
      loadNotifications();
      setupSSE();
    }

    return () => {
      if (isAuthenticated) {
        notificationApi.unsubscribe().catch(() => {});
      }
    };
  }, [isAuthenticated]);

  const loadNotifications = async () => {
    try {
      const data = await notificationApi.getNotifications();
      setNotifications(data.notifications);
      setUnreadCount(data.notifications.filter((n) => !n.isRead).length);
    } catch (error) {
      console.error('알림 로드 실패:', error);
    }
  };

  const setupSSE = () => {
    // EventSource는 헤더를 직접 설정할 수 없으므로, 
    // 백엔드에서 쿠키 기반 인증을 사용하거나 별도의 인증 방식이 필요합니다.
    // 여기서는 기본 구조만 제공합니다.
    const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://3.35.4.73:8080';
    const eventSource = new EventSource(
      `${apiBaseUrl}/notification/subscribe`,
      { withCredentials: true }
    );

    eventSource.onmessage = (event) => {
      const notification = JSON.parse(event.data) as Notification;
      setNotifications((prev) => [notification, ...prev]);
      setUnreadCount((prev) => prev + 1);
      toast.success(notification.content, {
        icon: '🔔',
        duration: 3000,
      });
    };

    eventSource.onerror = () => {
      eventSource.close();
      // 재연결 로직은 필요시 구현
    };
  };

  const handleLogout = async () => {
    try {
      await authApi.logout();
      logoutStore();
      navigate('/');
      toast.success('로그아웃되었습니다.');
    } catch (error) {
      console.error('로그아웃 실패:', error);
      logoutStore();
      navigate('/');
    }
  };

  const handleNotificationClick = async (notification: Notification) => {
    if (!notification.isRead) {
      try {
        await notificationApi.markAsRead(notification.id);
        setNotifications((prev) =>
          prev.map((n) =>
            n.id === notification.id ? { ...n, isRead: true } : n
          )
        );
        setUnreadCount((prev) => Math.max(0, prev - 1));
      } catch (error) {
        console.error('알림 읽음 처리 실패:', error);
      }
    }
    setShowNotifications(false);
    // 알림 타입에 따라 적절한 페이지로 이동
    if (notification.questionId) {
      navigate(`/questions/${notification.questionId}`);
    } else if (notification.type === 'NEW_ANSWER' || notification.type === 'ANSWER_ACCEPTED') {
      // 질문 상세 페이지로 이동 (senderId를 임시로 사용)
      if (notification.senderId) {
        navigate(`/questions/${notification.senderId}`);
      }
    } else if (notification.type === 'COMMENT_ADDED') {
      // 답변 상세 페이지로 이동
      if (notification.senderId) {
        navigate(`/questions/${notification.senderId}`);
      }
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow-sm border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <Link to="/" className="flex items-center space-x-2">
                <div className="w-8 h-8 bg-gradient-to-br from-primary-500 to-primary-700 rounded-lg flex items-center justify-center">
                  <span className="text-white font-bold text-lg">P</span>
                </div>
                <span className="text-xl font-bold text-gray-900">Playground</span>
              </Link>
            </div>

            <div className="flex items-center space-x-4">

              {isAuthenticated ? (
                <>
                  <Link
                    to="/questions/create"
                    className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors flex items-center space-x-2"
                  >
                    <PlusCircle className="w-4 h-4" />
                    <span>질문하기</span>
                  </Link>

                  <div className="relative">
                    <button
                      onClick={() => setShowNotifications(!showNotifications)}
                      className="relative p-2 text-gray-700 hover:text-primary-600 hover:bg-gray-100 rounded-lg transition-colors"
                    >
                      <Bell className="w-5 h-5" />
                      {unreadCount > 0 && (
                        <span className="absolute top-0 right-0 w-4 h-4 bg-red-500 text-white text-xs rounded-full flex items-center justify-center">
                          {unreadCount > 9 ? '9+' : unreadCount}
                        </span>
                      )}
                    </button>

                    {showNotifications && (
                      <>
                        <div 
                          className="fixed inset-0 z-40" 
                          onClick={() => setShowNotifications(false)}
                        />
                        <div className="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-lg border border-gray-200 z-50 max-h-96 overflow-y-auto">
                        <div className="p-4 border-b border-gray-200">
                          <h3 className="font-semibold text-gray-900">알림</h3>
                        </div>
                        <div className="divide-y divide-gray-200">
                          {notifications.length === 0 ? (
                            <div className="p-4 text-center text-gray-500">
                              알림이 없습니다.
                            </div>
                          ) : (
                            notifications.map((notification) => (
                              <div
                                key={notification.id}
                                className={`flex items-start justify-between p-4 hover:bg-gray-50 transition-colors ${
                                  !notification.isRead ? 'bg-primary-50' : ''
                                }`}
                              >
                                <button
                                  onClick={() => handleNotificationClick(notification)}
                                  className="flex-1 text-left"
                                >
                                  <p className="text-sm text-gray-900">
                                    {notification.content}
                                  </p>
                                  <p className="text-xs text-gray-500 mt-1">
                                    {new Date(notification.createdAt).toLocaleString('ko-KR')}
                                  </p>
                                </button>
                                <button
                                  onClick={async (e) => {
                                    e.stopPropagation();
                                    try {
                                      await notificationApi.delete(notification.id);
                                      setNotifications((prev) => prev.filter((n) => n.id !== notification.id));
                                      if (!notification.isRead) {
                                        setUnreadCount((prev) => Math.max(0, prev - 1));
                                      }
                                    } catch (error) {
                                      console.error('알림 삭제 실패:', error);
                                    }
                                  }}
                                  className="ml-2 p-1 text-gray-400 hover:text-red-600 transition-colors"
                                >
                                  <Trash2 className="w-4 h-4" />
                                </button>
                              </div>
                            ))
                          )}
                        </div>
                        </div>
                      </>
                    )}
                  </div>

                  <Link
                    to="/me"
                    className="px-3 py-2 rounded-md text-sm font-medium text-gray-700 hover:text-primary-600 hover:bg-gray-100 transition-colors flex items-center space-x-2"
                  >
                    <User className="w-4 h-4" />
                    <span>{user?.nickname || '마이페이지'}</span>
                  </Link>

                  <button
                    onClick={handleLogout}
                    className="px-3 py-2 rounded-md text-sm font-medium text-gray-700 hover:text-red-600 hover:bg-gray-100 transition-colors flex items-center space-x-2"
                  >
                    <LogOut className="w-4 h-4" />
                    <span>로그아웃</span>
                  </button>
                </>
              ) : (
                <>
                  <Link
                    to="/login"
                    className="px-4 py-2 text-gray-700 hover:text-primary-600 transition-colors"
                  >
                    로그인
                  </Link>
                  <Link
                    to="/register"
                    className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors"
                  >
                    회원가입
                  </Link>
                </>
              )}
            </div>
          </div>
        </div>
      </nav>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Outlet />
      </main>

      <footer className="bg-white border-t border-gray-200 mt-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <p className="text-center text-gray-500 text-sm">
            © 2026 Playground. All rights reserved.
          </p>
        </div>
      </footer>
    </div>
  );
}

