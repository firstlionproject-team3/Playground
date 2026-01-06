# Playground 프론트엔드

Q&A 플랫폼 Playground의 프론트엔드 애플리케이션입니다.

## 기술 스택

- **React 18** - UI 라이브러리
- **TypeScript** - 타입 안정성
- **Vite** - 빌드 도구
- **React Router** - 라우팅
- **Zustand** - 상태 관리
- **Axios** - HTTP 클라이언트
- **Tailwind CSS** - 스타일링
- **React Hot Toast** - 알림
- **Lucide React** - 아이콘
- **date-fns** - 날짜 처리

## 시작하기

### 1. 의존성 설치

```bash
npm install
```

### 2. 개발 서버 실행

```bash
npm run dev
```

개발 서버는 `http://localhost:3000`에서 실행됩니다.

### 3. 빌드

```bash
npm run build
```

빌드된 파일은 `dist` 디렉토리에 생성됩니다.

## 프로젝트 구조

```
FE/
├── src/
│   ├── api/           # API 클라이언트
│   │   ├── client.ts  # Axios 설정 및 인터셉터
│   │   ├── auth.ts    # 인증 API
│   │   ├── user.ts    # 사용자 API
│   │   ├── question.ts # 질문 API
│   │   ├── answer.ts  # 답변 API
│   │   ├── comment.ts # 댓글 API
│   │   ├── reaction.ts # 반응 API
│   │   ├── notification.ts # 알림 API
│   │   ├── report.ts  # 신고 API
│   │   └── admin.ts   # 관리자 API
│   ├── components/    # 재사용 컴포넌트
│   │   ├── Layout.tsx # 레이아웃
│   │   ├── QuestionCard.tsx # 질문 카드
│   │   └── ReactionButton.tsx # 반응 버튼
│   ├── pages/         # 페이지 컴포넌트
│   │   ├── HomePage.tsx # 홈
│   │   ├── LoginPage.tsx # 로그인
│   │   ├── RegisterPage.tsx # 회원가입
│   │   ├── QuestionDetailPage.tsx # 질문 상세
│   │   ├── QuestionCreatePage.tsx # 질문 작성
│   │   ├── MyPage.tsx # 마이페이지
│   │   ├── NotificationPage.tsx # 알림
│   │   ├── AdminPage.tsx # 관리자
│   │   └── OAuthCallback.tsx # OAuth 콜백
│   ├── store/         # 상태 관리
│   │   └── authStore.ts # 인증 상태
│   ├── types/         # TypeScript 타입
│   │   └── index.ts
│   ├── utils/         # 유틸리티 함수
│   │   ├── date.ts    # 날짜 포맷팅
│   │   └── validation.ts # 폼 검증
│   ├── App.tsx        # 메인 앱 컴포넌트
│   ├── main.tsx       # 진입점
│   └── index.css      # 글로벌 스타일
├── package.json
├── tsconfig.json
├── vite.config.ts
└── tailwind.config.js
```

## 주요 기능

### 인증
- 일반 로그인 (아이디/비밀번호)
- OAuth2 소셜 로그인 (GitHub, Naver)
- JWT 토큰 기반 인증
- 자동 토큰 갱신

### 질문/답변
- 질문 작성, 수정, 삭제
- 답변 작성, 수정, 삭제
- 댓글 작성, 수정, 삭제
- 답변 채택
- 추천/비추천 (Reaction)

### 사용자
- 회원가입
- 마이페이지
- 내 질문/답변 목록
- 프로필 수정
- 회원 탈퇴

### 알림
- 실시간 알림 (SSE)
- 알림 목록 조회
- 읽음 처리
- 알림 삭제

### 관리자
- 사용자 목록 조회
- 사용자 삭제
- 신고 관리

## 환경 설정

백엔드 서버가 `http://localhost:8080`에서 실행 중이어야 합니다.

Vite 프록시 설정으로 `/api` 경로가 백엔드로 자동 전달됩니다.

## 주의사항

1. **CORS**: 백엔드에서 `http://localhost:3000`을 허용해야 합니다.
2. **쿠키**: Refresh Token은 HttpOnly 쿠키로 관리됩니다.
3. **SSE**: EventSource는 헤더를 직접 설정할 수 없으므로, 백엔드에서 쿠키 기반 인증을 사용해야 합니다.

## 라이선스

MIT

