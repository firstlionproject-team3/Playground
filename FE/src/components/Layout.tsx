import { Outlet, Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuthStore } from '@/store/authStore';
import { authApi } from '@/api/auth';
import { Bell, LogOut, User, PlusCircle, Trash2, Shield } from 'lucide-react';
import { useState, useEffect, useRef } from 'react';
import { notificationApi } from '@/api/notification';
import { Notification } from '@/types';
import toast from 'react-hot-toast';
import { userApi } from '@/api/user';

export default function Layout() {
  const { isAuthenticated, user, logout: logoutStore, setUser } = useAuthStore();
  const navigate = useNavigate();
  const location = useLocation();
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [showNotifications, setShowNotifications] = useState(false);
  const [isAdmin, setIsAdmin] = useState(false);
  const eventSourceRef = useRef<EventSource | null>(null);
  const reconnectTimeoutRef = useRef<NodeJS.Timeout | null>(null);
  const reconnectAttemptsRef = useRef(0);
  const MAX_RECONNECT_ATTEMPTS = 5;
  const RECONNECT_DELAY = 3000; // 3초

  // 새로고침 시 user 정보 로드
  useEffect(() => {
    if (isAuthenticated && !user) {
      loadUser();
    } else if (isAuthenticated && user) {
      checkAdmin();
    }
  }, [isAuthenticated, user]);

  useEffect(() => {
    if (isAuthenticated) {
      loadNotifications();
      setupSSE();
    } else {
      // 인증되지 않은 경우 SSE 연결 종료
      closeSSE();
    }

    return () => {
      closeSSE();
    };
  }, [isAuthenticated]);

  const loadUser = async () => {
    try {
      const userData = await userApi.getMyPage();
      setUser(userData);
      checkAdmin(userData);
    } catch (error) {
      console.error('사용자 정보 로드 실패:', error);
    }
  };

  const checkAdmin = async (userData?: any) => {
    try {
      const testUser = userData || user;
      if (testUser) {
        // 백엔드에서 role 정보를 제공하는 경우
        if (testUser.roles && Array.isArray(testUser.roles)) {
          setIsAdmin(testUser.roles.includes('ROLE_ADMIN'));
        } else if (testUser.loginId === 'admin') {
          // 임시로 loginId가 'admin'인 경우 관리자로 간주
          setIsAdmin(true);
        } else {
          setIsAdmin(false);
        }
      }
    } catch (error) {
      setIsAdmin(false);
    }
  };

  const loadNotifications = async () => {
    try {
      const data = await notificationApi.getNotifications();
      setNotifications(data.notifications);
      setUnreadCount(data.notifications.filter((n) => !n.isRead).length);
    } catch (error) {
      console.error('알림 로드 실패:', error);
    }
  };

  const closeSSE = () => {
    // 재연결 타이머 정리
    if (reconnectTimeoutRef.current) {
      clearTimeout(reconnectTimeoutRef.current);
      reconnectTimeoutRef.current = null;
    }

    // EventSource 연결 종료
    if (eventSourceRef.current) {
      eventSourceRef.current.close();
      eventSourceRef.current = null;
    }

    // 백엔드에 연결 종료 알림 (선택사항)
    if (isAuthenticated) {
      notificationApi.unsubscribe().catch(() => {});
    }
  };

  const setupSSE = () => {
    // 이미 연결되어 있으면 기존 연결 종료
    if (eventSourceRef.current) {
      eventSourceRef.current.close();
      eventSourceRef.current = null;
    }

    // 재연결 시도 횟수 초기화
    reconnectAttemptsRef.current = 0;

    const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://3.35.4.73:8080';
    const eventSource = new EventSource(
      `${apiBaseUrl}/notification/subscribe`,
      { withCredentials: true }
    );

    eventSourceRef.current = eventSource;

    eventSource.onopen = () => {
      // 연결 성공 시 재연결 시도 횟수 초기화
      reconnectAttemptsRef.current = 0;
      console.log('SSE 연결 성공');
    };

    eventSource.onmessage = (event) => {
      try {
        const notification = JSON.parse(event.data) as Notification;
        setNotifications((prev) => [notification, ...prev]);
        setUnreadCount((prev) => prev + 1);
        toast.success(notification.content, {
          icon: '🔔',
          duration: 3000,
        });
      } catch (error) {
        console.error('알림 파싱 실패:', error);
      }
    };

    eventSource.onerror = (error) => {
      console.error('SSE 연결 오류:', error);
      
      // EventSource가 이미 닫혔는지 확인
      if (eventSource.readyState === EventSource.CLOSED) {
        // 연결이 닫혔고, 인증된 상태이고, 재연결 시도 횟수가 최대값보다 적으면 재연결
        if (isAuthenticated && reconnectAttemptsRef.current < MAX_RECONNECT_ATTEMPTS) {
          reconnectAttemptsRef.current += 1;
          
          // 재연결 타이머 정리
          if (reconnectTimeoutRef.current) {
            clearTimeout(reconnectTimeoutRef.current);
          }

          // 지연 후 재연결
          reconnectTimeoutRef.current = setTimeout(() => {
            console.log(`SSE 재연결 시도 ${reconnectAttemptsRef.current}/${MAX_RECONNECT_ATTEMPTS}`);
            if (isAuthenticated && !eventSourceRef.current) {
              setupSSE();
            }
          }, RECONNECT_DELAY);
        } else if (reconnectAttemptsRef.current >= MAX_RECONNECT_ATTEMPTS) {
          console.error('SSE 재연결 최대 시도 횟수 초과');
          toast.error('알림 연결에 실패했습니다. 페이지를 새로고침해주세요.');
        }
      }
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
        // 알림 읽음 상태를 즉시 반영하기 위해 다시 로드
        await loadNotifications();
      } catch (error) {
        console.error('알림 읽음 처리 실패:', error);
      }
    }
    setShowNotifications(false);
    // 알림 타입에 따라 적절한 페이지로 이동
    if (notification.questionId) {
      navigate(`/questions/${notification.questionId}`);
    } else if (notification.type === 'NEW_ANSWER' || notification.type === 'ANSWER_ACCEPTED') {
      // 질문 상세 페이지로 이동
      if (notification.questionId) {
        navigate(`/questions/${notification.questionId}`);
      }
    } else if (notification.type === 'COMMENT_ADDED') {
      // 답변 상세 페이지로 이동
      if (notification.questionId) {
        navigate(`/questions/${notification.questionId}`);
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
                  {/* 질문 목록 페이지가 아닐 때만 상단바에 질문하기 버튼 표시 */}
                  {location.pathname !== '/' && (
                    <Link
                      to="/questions/create"
                      className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors flex items-center space-x-2"
                    >
                      <PlusCircle className="w-4 h-4" />
                      <span>질문하기</span>
                    </Link>
                  )}

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

                  {isAdmin && (
                    <Link
                      to="/admin"
                      className="px-3 py-2 rounded-md text-sm font-medium text-gray-700 hover:text-primary-600 hover:bg-gray-100 transition-colors flex items-center space-x-2"
                    >
                      <Shield className="w-4 h-4" />
                      <span>관리자</span>
                    </Link>
                  )}

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

