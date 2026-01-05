# Playground 프론트엔드

Playground Q&A 플랫폼의 프론트엔드 애플리케이션입니다.

## 기술 스택

- **React 18** - UI 라이브러리
- **TypeScript** - 타입 안정성
- **Vite** - 빌드 도구
- **React Router** - 라우팅
- **Axios** - HTTP 클라이언트
- **Zustand** - 상태 관리
- **React Query** - 서버 상태 관리 (선택)

## 시작하기

### 1. 의존성 설치

```bash
npm install
```

### 2. 환경 변수 설정

`.env.development` 파일을 생성하고 다음 내용을 추가하세요:

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_OAUTH_REDIRECT_URL=http://localhost:3000
```

### 3. 개발 서버 실행

```bash
npm run dev
```

개발 서버는 `http://localhost:3000`에서 실행됩니다.

### 4. 빌드

```bash
npm run build
```

빌드된 파일은 `dist` 디렉토리에 생성됩니다.

## 프로젝트 구조

```
src/
├── api/              # API 클라이언트
├── components/       # 재사용 가능한 컴포넌트
├── contexts/        # React Context
├── hooks/           # Custom Hooks
├── pages/           # 페이지 컴포넌트
├── routes/          # 라우팅 설정
├── store/           # 상태 관리 (Zustand)
├── types/           # TypeScript 타입 정의
├── utils/           # 유틸리티 함수
└── styles/          # 전역 스타일
```

## 주요 기능

- ✅ 사용자 인증 (일반 로그인, OAuth2)
- ✅ 질문/답변/댓글 CRUD
- ✅ 추천/비추천 시스템
- ✅ 실시간 알림 (SSE)
- ✅ 신고 시스템
- ✅ 관리자 기능

## API 통신

모든 API 요청은 `src/api/axios/instance.ts`에서 생성된 `apiClient`를 사용합니다.

- Access Token은 자동으로 Authorization 헤더에 추가됩니다.
- 401 에러 발생 시 자동으로 토큰을 재발급하고 요청을 재시도합니다.
- Refresh Token은 HttpOnly 쿠키로 관리됩니다.

## 인증 플로우

1. **일반 로그인**: `POST /auth/login` → Access Token 저장
2. **OAuth 로그인**: 소셜 로그인 → 코드 받기 → `GET /oauth/token` → Access Token 저장
3. **토큰 재발급**: 401 에러 발생 시 자동으로 `POST /user/refreshToken` 호출

## 라우팅

- `/` - 홈 페이지
- `/login` - 로그인
- `/register` - 회원가입
- `/questions` - 질문 목록
- `/questions/:id` - 질문 상세
- `/questions/create` - 질문 작성 (인증 필요)
- `/mypage` - 마이페이지 (인증 필요)
- `/admin` - 관리자 대시보드 (관리자만)

## 상태 관리

- **Zustand**: 인증 상태, 알림 상태 관리
- **React Context**: 전역 컨텍스트 (AuthContext, NotificationContext)

## 스타일링

CSS 모듈을 사용하여 컴포넌트별로 스타일을 관리합니다.

## 개발 가이드

### 새 페이지 추가

1. `src/pages/` 디렉토리에 새 페이지 컴포넌트 생성
2. `src/routes/routes.tsx`에 라우트 추가

### 새 API 추가

1. `src/api/` 디렉토리에 해당 도메인 폴더 생성
2. API 함수 작성
3. 타입은 `src/types/api/`에 정의

### 새 컴포넌트 추가

1. `src/components/` 디렉토리에 컴포넌트 생성
2. 필요시 CSS 파일 추가

## 문제 해결

### CORS 에러

백엔드에서 CORS 설정이 올바른지 확인하세요. `withCredentials: true` 설정이 필요합니다.

### 토큰 재발급 실패

Refresh Token이 만료되었거나 쿠키가 제대로 설정되지 않았을 수 있습니다. 브라우저 개발자 도구에서 쿠키를 확인하세요.

### SSE 연결 실패

SSE는 쿠키 기반 인증을 사용합니다. 백엔드에서 SSE 엔드포인트가 쿠키를 올바르게 처리하는지 확인하세요.

## 라이선스

이 프로젝트는 개인 프로젝트입니다.
