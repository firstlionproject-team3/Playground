import { Routes, Route } from 'react-router-dom';
import { PrivateRoute } from './PrivateRoute';
import { AdminRoute } from './AdminRoute';
import HomePage from '@/pages/home/HomePage';
import LoginPage from '@/pages/auth/LoginPage';
import RegisterPage from '@/pages/auth/RegisterPage';
import OAuthCallbackPage from '@/pages/auth/OAuthCallbackPage';
import QuestionListPage from '@/pages/question/QuestionListPage';
import QuestionDetailPage from '@/pages/question/QuestionDetailPage';
import QuestionCreatePage from '@/pages/question/QuestionCreatePage';
import MyPage from '@/pages/user/MyPage';
import MyQuestionsPage from '@/pages/user/MyQuestionsPage';
import MyAnswersPage from '@/pages/user/MyAnswersPage';
import AdminDashboard from '@/pages/admin/AdminDashboard';
import AdminUserListPage from '@/pages/admin/AdminUserListPage';
import AdminReportListPage from '@/pages/admin/AdminReportListPage';
import NotFoundPage from '@/pages/NotFoundPage';

export const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/oauth/callback" element={<OAuthCallbackPage />} />
      
      <Route path="/questions" element={<QuestionListPage />} />
      <Route path="/questions/:id" element={<QuestionDetailPage />} />
      
      <Route
        path="/questions/create"
        element={
          <PrivateRoute>
            <QuestionCreatePage />
          </PrivateRoute>
        }
      />
      
      <Route
        path="/mypage"
        element={
          <PrivateRoute>
            <MyPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/mypage/questions"
        element={
          <PrivateRoute>
            <MyQuestionsPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/mypage/answers"
        element={
          <PrivateRoute>
            <MyAnswersPage />
          </PrivateRoute>
        }
      />
      
      <Route
        path="/admin"
        element={
          <AdminRoute>
            <AdminDashboard />
          </AdminRoute>
        }
      />
      <Route
        path="/admin/users"
        element={
          <AdminRoute>
            <AdminUserListPage />
          </AdminRoute>
        }
      />
      <Route
        path="/admin/reports"
        element={
          <AdminRoute>
            <AdminReportListPage />
          </AdminRoute>
        }
      />
      
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
};

