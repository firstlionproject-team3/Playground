# Playground 프론트엔드 디렉토리 구조

## 📁 권장 디렉토리 구조

```
playground-frontend/
├── public/                          # 정적 파일
│   ├── favicon.ico
│   ├── logo.svg
│   └── images/
│
├── src/
│   ├── api/                         # API 통신 관련
│   │   ├── axios/
│   │   │   ├── instance.ts         # Axios 인스턴스 설정
│   │   │   ├── interceptors.ts    # Request/Response 인터셉터
│   │   │   └── types.ts            # API 타입 정의
│   │   │
│   │   ├── auth/                   # 인증 관련 API
│   │   │   ├── authApi.ts          # 로그인, 로그아웃, 토큰 재발급
│   │   │   └── oauthApi.ts         # OAuth2 소셜 로그인
│   │   │
│   │   ├── user/                   # 사용자 관련 API
│   │   │   ├── userApi.ts          # 회원가입, 마이페이지, 회원정보 수정
│   │   │   └── adminUserApi.ts    # 관리자 사용자 관리
│   │   │
│   │   ├── question/               # 질문 관련 API
│   │   │   └── questionApi.ts     # 질문 CRUD, 검색, 신고, 채택
│   │   │
│   │   ├── answer/                 # 답변 관련 API
│   │   │   └── answerApi.ts       # 답변 CRUD, 신고
│   │   │
│   │   ├── comment/                # 댓글 관련 API
│   │   │   └── commentApi.ts      # 댓글 CRUD
│   │   │
│   │   ├── reaction/               # 추천/비추천 관련 API
│   │   │   └── reactionApi.ts     # 반응 토글, 조회
│   │   │
│   │   ├── notification/           # 알림 관련 API
│   │   │   ├── notificationApi.ts # 알림 목록, 읽음, 삭제
│   │   │   └── sseService.ts      # SSE 연결 관리
│   │   │
│   │   └── report/                 # 신고 관련 API
│   │       ├── reportApi.ts        # 신고 생성
│   │       └── adminReportApi.ts   # 관리자 신고 관리
│   │
│   ├── components/                  # 재사용 가능한 컴포넌트
│   │   ├── common/                 # 공통 컴포넌트
│   │   │   ├── Button/
│   │   │   │   ├── Button.tsx
│   │   │   │   ├── Button.module.css
│   │   │   │   └── index.ts
│   │   │   ├── Input/
│   │   │   ├── Textarea/
│   │   │   ├── Modal/
│   │   │   ├── Loading/
│   │   │   ├── ErrorBoundary/
│   │   │   ├── Pagination/
│   │   │   └── Alert/
│   │   │
│   │   ├── layout/                 # 레이아웃 컴포넌트
│   │   │   ├── Header/
│   │   │   │   ├── Header.tsx
│   │   │   │   └── index.ts
│   │   │   ├── Footer/
│   │   │   ├── Sidebar/
│   │   │   └── Layout.tsx
│   │   │
│   │   ├── question/               # 질문 관련 컴포넌트
│   │   │   ├── QuestionCard/
│   │   │   ├── QuestionList/
│   │   │   ├── QuestionDetail/
│   │   │   ├── QuestionForm/
│   │   │   └── QuestionSearch/
│   │   │
│   │   ├── answer/                 # 답변 관련 컴포넌트
│   │   │   ├── AnswerCard/
│   │   │   ├── AnswerList/
│   │   │   ├── AnswerForm/
│   │   │   └── AcceptButton/
│   │   │
│   │   ├── comment/                # 댓글 관련 컴포넌트
│   │   │   ├── CommentCard/
│   │   │   ├── CommentList/
│   │   │   └── CommentForm/
│   │   │
│   │   ├── reaction/               # 반응 관련 컴포넌트
│   │   │   ├── LikeButton/
│   │   │   ├── DislikeButton/
│   │   │   └── ReactionCount/
│   │   │
│   │   └── notification/           # 알림 관련 컴포넌트
│   │       ├── NotificationList/
│   │       ├── NotificationItem/
│   │       └── NotificationBell/
│   │
│   ├── pages/                       # 페이지 컴포넌트
│   │   ├── auth/                   # 인증 페이지
│   │   │   ├── LoginPage.tsx
│   │   │   ├── RegisterPage.tsx
│   │   │   └── OAuthCallbackPage.tsx
│   │   │
│   │   ├── home/                   # 홈 페이지
│   │   │   └── HomePage.tsx
│   │   │
│   │   ├── question/               # 질문 관련 페이지
│   │   │   ├── QuestionListPage.tsx
│   │   │   ├── QuestionDetailPage.tsx
│   │   │   └── QuestionCreatePage.tsx
│   │   │
│   │   ├── user/                   # 사용자 관련 페이지
│   │   │   ├── MyPage.tsx
│   │   │   ├── MyQuestionsPage.tsx
│   │   │   └── MyAnswersPage.tsx
│   │   │
│   │   ├── admin/                  # 관리자 페이지
│   │   │   ├── AdminDashboard.tsx
│   │   │   ├── AdminUserListPage.tsx
│   │   │   └── AdminReportListPage.tsx
│   │   │
│   │   └── NotFoundPage.tsx        # 404 페이지
│   │
│   ├── hooks/                       # Custom Hooks
│   │   ├── auth/
│   │   │   ├── useAuth.ts          # 인증 상태 관리
│   │   │   ├── useLogin.ts         # 로그인 훅
│   │   │   ├── useLogout.ts        # 로그아웃 훅
│   │   │   └── useTokenRefresh.ts # 토큰 재발급 훅
│   │   │
│   │   ├── question/
│   │   │   ├── useQuestions.ts     # 질문 목록 조회
│   │   │   ├── useQuestionDetail.ts # 질문 상세 조회
│   │   │   ├── useCreateQuestion.ts # 질문 생성
│   │   │   └── useSearchQuestions.ts # 질문 검색
│   │   │
│   │   ├── answer/
│   │   │   ├── useAnswers.ts        # 답변 목록 조회
│   │   │   ├── useCreateAnswer.ts   # 답변 생성
│   │   │   └── useAcceptAnswer.ts   # 답변 채택
│   │   │
│   │   ├── comment/
│   │   │   ├── useComments.ts      # 댓글 목록 조회
│   │   │   └── useCreateComment.ts # 댓글 생성
│   │   │
│   │   ├── reaction/
│   │   │   ├── useReaction.ts      # 반응 토글
│   │   │   └── useReactionCount.ts # 반응 개수 조회
│   │   │
│   │   ├── notification/
│   │   │   ├── useNotifications.ts # 알림 목록 조회
│   │   │   └── useSSE.ts           # SSE 연결 훅
│   │   │
│   │   └── common/
│   │       ├── usePagination.ts     # 페이지네이션 훅
│   │       ├── useDebounce.ts      # 디바운스 훅
│   │       └── useLocalStorage.ts  # 로컬스토리지 훅
│   │
│   ├── store/                       # 상태 관리 (Zustand/Redux)
│   │   ├── auth/
│   │   │   ├── authStore.ts        # 인증 상태 스토어
│   │   │   └── authSlice.ts        # Redux slice (Redux 사용 시)
│   │   │
│   │   ├── user/
│   │   │   └── userStore.ts        # 사용자 정보 스토어
│   │   │
│   │   ├── notification/
│   │   │   └── notificationStore.ts # 알림 상태 스토어
│   │   │
│   │   └── index.ts                # 스토어 통합
│   │
│   ├── types/                       # TypeScript 타입 정의
│   │   ├── api/                     # API 응답 타입
│   │   │   ├── auth.types.ts
│   │   │   ├── user.types.ts
│   │   │   ├── question.types.ts
│   │   │   ├── answer.types.ts
│   │   │   ├── comment.types.ts
│   │   │   ├── reaction.types.ts
│   │   │   ├── notification.types.ts
│   │   │   └── report.types.ts
│   │   │
│   │   ├── common.types.ts         # 공통 타입
│   │   ├── enum.types.ts           # Enum 타입
│   │   └── index.ts
│   │
│   ├── utils/                       # 유틸리티 함수
│   │   ├── date.ts                 # 날짜 포맷팅
│   │   ├── validation.ts           # 폼 검증 함수
│   │   ├── format.ts               # 문자열 포맷팅
│   │   ├── error.ts                # 에러 처리 유틸
│   │   └── constants.ts            # 상수 정의
│   │
│   ├── contexts/                    # React Context
│   │   ├── AuthContext.tsx         # 인증 컨텍스트
│   │   ├── NotificationContext.tsx # 알림 컨텍스트
│   │   └── ThemeContext.tsx        # 테마 컨텍스트 (선택)
│   │
│   ├── routes/                      # 라우팅 설정
│   │   ├── routes.tsx              # 라우트 정의
│   │   ├── PrivateRoute.tsx        # 인증 필요 라우트
│   │   ├── AdminRoute.tsx          # 관리자 전용 라우트
│   │   └── index.tsx
│   │
│   ├── styles/                      # 전역 스타일
│   │   ├── globals.css
│   │   ├── variables.css           # CSS 변수
│   │   └── reset.css               # CSS 리셋
│   │
│   ├── App.tsx                      # 루트 컴포넌트
│   ├── main.tsx                    # 진입점
│   └── vite-env.d.ts              # Vite 타입 정의 (Vite 사용 시)
│
├── .env                            # 환경 변수
├── .env.local                      # 로컬 환경 변수
├── .env.development                # 개발 환경 변수
├── .env.production                 # 프로덕션 환경 변수
│
├── .gitignore
├── package.json
├── tsconfig.json                   # TypeScript 설정
├── vite.config.ts                  # Vite 설정 (또는 webpack.config.js)
├── tailwind.config.js              # Tailwind CSS 설정 (사용 시)
├── eslint.config.js                # ESLint 설정
├── prettier.config.js              # Prettier 설정
└── README.md
```

## 📦 주요 패키지 추천

### 필수 패키지
```json
{
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.20.0",
    "axios": "^1.6.0",
    "zustand": "^4.4.0",  // 또는 "redux": "^4.2.0"
    "@tanstack/react-query": "^5.0.0"  // 선택 (API 상태 관리)
  },
  "devDependencies": {
    "@types/react": "^18.2.0",
    "@types/react-dom": "^18.2.0",
    "typescript": "^5.3.0",
    "vite": "^5.0.0",  // 또는 "webpack": "^5.0.0"
    "eslint": "^8.50.0",
    "prettier": "^3.0.0"
  }
}
```

### UI 라이브러리 (선택)
- **Tailwind CSS**: 유틸리티 기반 CSS
- **Material-UI (MUI)**: React 컴포넌트 라이브러리
- **Ant Design**: 엔터프라이즈급 UI 라이브러리
- **Chakra UI**: 간단하고 모던한 컴포넌트 라이브러리

## 🔧 주요 파일 예시

### `src/api/axios/instance.ts`
```typescript
import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true, // 쿠키 포함
  headers: {
    'Content-Type': 'application/json',
  },
});
```

### `src/api/axios/interceptors.ts`
```typescript
import { apiClient } from './instance';
import { authStore } from '@/store/auth/authStore';

// Request Interceptor: Access Token 추가
apiClient.interceptors.request.use(
  (config) => {
    const token = authStore.getState().accessToken;
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor: 401 에러 처리 및 토큰 재발급
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        // 토큰 재발급
        await authStore.getState().refreshToken();
        // 원래 요청 재시도
        return apiClient(originalRequest);
      } catch (refreshError) {
        // 재발급 실패 시 로그아웃
        authStore.getState().logout();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    
    return Promise.reject(error);
  }
);
```

### `src/routes/PrivateRoute.tsx`
```typescript
import { Navigate } from 'react-router-dom';
import { useAuth } from '@/hooks/auth/useAuth';

interface PrivateRouteProps {
  children: React.ReactNode;
}

export const PrivateRoute = ({ children }: PrivateRouteProps) => {
  const { isAuthenticated } = useAuth();
  
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }
  
  return <>{children}</>;
};
```

### `src/routes/AdminRoute.tsx`
```typescript
import { Navigate } from 'react-router-dom';
import { useAuth } from '@/hooks/auth/useAuth';

interface AdminRouteProps {
  children: React.ReactNode;
}

export const AdminRoute = ({ children }: AdminRouteProps) => {
  const { isAuthenticated, user } = useAuth();
  
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }
  
  if (!user?.roles?.includes('ROLE_ADMIN')) {
    return <Navigate to="/" replace />;
  }
  
  return <>{children}</>;
};
```

## 🎯 디렉토리 구조 선택 가이드

### 1. **도메인 중심 구조** (현재 제안)
- 장점: 기능별로 명확하게 분리, 확장 용이
- 단점: 초기 구조가 복잡할 수 있음
- 적합: 중대형 프로젝트

### 2. **기능 중심 구조** (간단한 버전)
```
src/
├── features/
│   ├── auth/
│   ├── question/
│   └── answer/
├── shared/
│   ├── components/
│   ├── hooks/
│   └── utils/
```

### 3. **Atomic Design 구조**
```
src/
├── atoms/
├── molecules/
├── organisms/
├── templates/
└── pages/
```

## 📝 환경 변수 예시

### `.env.development`
```env
VITE_API_BASE_URL=http://localhost:8080
VITE_OAUTH_REDIRECT_URL=http://localhost:3000
```

### `.env.production`
```env
VITE_API_BASE_URL=https://api.playground.com
VITE_OAUTH_REDIRECT_URL=https://playground.com
```

## 🚀 빠른 시작 스크립트

### `package.json` 스크립트 예시
```json
{
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "preview": "vite preview",
    "lint": "eslint . --ext ts,tsx",
    "format": "prettier --write \"src/**/*.{ts,tsx,css}\""
  }
}
```

---

이 구조를 기반으로 프로젝트를 시작하시면 됩니다. 프로젝트 규모와 팀 선호도에 따라 조정하시면 됩니다!

