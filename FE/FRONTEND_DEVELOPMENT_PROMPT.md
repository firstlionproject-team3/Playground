# Playground 프론트엔드 개발 프롬프트

## 📋 프로젝트 개요

**Playground**는 Q&A 플랫폼으로, 사용자가 질문을 작성하고 답변을 받을 수 있는 커뮤니티 서비스입니다.

### 주요 기능
- 사용자 인증 (일반 로그인, OAuth2 소셜 로그인 - GitHub, Naver)
- 질문/답변/댓글 CRUD
- 추천/비추천 (Reaction) 시스템
- 실시간 알림 (SSE)
- 신고 시스템
- 관리자 기능

---

## 🔐 인증/인가 시스템

### 인증 방식
- **JWT 기반 인증** (Stateless)
- **Access Token**: Authorization 헤더에 `Bearer {token}` 형식으로 전송
- **Refresh Token**: HttpOnly 쿠키에 저장 (자동 관리)

### 인증 흐름

#### 1. 일반 로그인
```
POST /auth/login
Body: { "loginId": string, "password": string }
Response: { "accessToken": string }
→ Refresh Token은 자동으로 쿠키에 저장됨
```

#### 2. OAuth2 소셜 로그인
```
1. 사용자가 소셜 로그인 버튼 클릭
2. 백엔드로 리다이렉트: GET /oauth2/authorization/{provider} (github 또는 naver)
3. 소셜 인증 완료 후 백엔드가 임시 코드와 함께 프론트엔드로 리다이렉트
   → 리다이렉트 URL: http://localhost:3000?code={temporaryCode} // 수정 예정
4. 프론트엔드에서 코드를 받아 POST /oauth/token?code={code} 호출
5. Response: { "accessToken": string }
→ Refresh Token은 자동으로 쿠키에 저장됨
```

#### 3. 토큰 재발급
```
POST /user/refreshToken
→ 쿠키의 refreshToken을 자동으로 사용
Response: { "accessToken": string }
→ 새로운 refreshToken도 쿠키에 자동 저장됨
```

#### 4. 로그아웃
```
POST /auth/logout
→ 쿠키의 refreshToken을 자동으로 삭제
```

### 인증이 필요한 API
- 대부분의 API는 인증 필요
- 인증 실패 시: 401 Unauthorized
- Access Token은 매 요청마다 `Authorization: Bearer {token}` 헤더에 포함

### 인증 불필요한 API
- `POST /users` (회원가입)
- `POST /auth/login` (로그인)
- `POST /user/refreshToken` (토큰 재발급)
- `GET /oauth2/authorization/**` (OAuth 시작)
- `GET /login/oauth2/code/**` (OAuth 콜백)

---

## 📡 API 엔드포인트 전체 목록

### Base URL
```
http://localhost:8080
```

### CORS 설정
- 허용된 Origin: `http://localhost:3000` // 수정 예정
- Credentials: true (쿠키 포함)

---

## 👤 사용자 (User)

### 회원가입
```
POST /users
Body: {
  "loginId": string (영문+숫자만, 최대 100자),
  "password": string (8-64자, 영문+숫자+특수문자 각 1개 이상, 공백 불가),
  "email": string (이메일 형식, 최대 100자, optional)
}
Response: 201 Created
{
  "id": number,
  "loginId": string,
  "nickname": string (자동 생성),
  "email": string
}
```

### 마이페이지 조회
```
GET /users/me
Headers: Authorization: Bearer {token}
Response: 200 OK
{
  "nickname": string,
  "email": string,
  "joinedDate": string (ISO 8601)
}
```

### 마이페이지 수정
```
PATCH /users/me
Headers: Authorization: Bearer {token}
Body: {
  "nickname": string,
  "email": string
}
Response: 200 OK (UserMyPageResponseDTO)
```

### 내 질문 목록
```
GET /users/me/questions?page=0&size=20&sort=createdAt,desc
Headers: Authorization: Bearer {token}
Response: 200 OK (Page<QuestionSummaryResponseDTO>)
```

### 내 답변 목록
```
GET /users/me/answers?page=0&size=20&sort=createdAt,desc
Headers: Authorization: Bearer {token}
Response: 200 OK (Page<AnswerSummaryResponseDTO>)
```

### 회원 탈퇴
```
DELETE /users/me
Headers: Authorization: Bearer {token}
Response: 204 No Content
```

---

## ❓ 질문 (Question)

### 질문 생성
```
POST /questions
Headers: Authorization: Bearer {token}
Body: {
  "title": string (required, not blank),
  "content": string (required, not blank)
}
Response: 201 Created
{
  "id": number
}
```

### 질문 목록 조회 (검색)
```
GET /questions?type={type}&keyword={keyword}&page=0&size=10
Query Parameters:
  - type: "title" | "content" | "all" (optional, default: "all")
  - keyword: string (optional)
  - page: number (default: 0)
  - size: number (default: 10)
Response: 200 OK (Page<QuestionSummaryResponseDTO>)
{
  "content": [
    {
      "id": number,
      "nickname": string,
      "title": string,
      "createdAt": string
    }
  ],
  "totalElements": number,
  "totalPages": number,
  "size": number,
  "number": number
}
```

### 질문 상세 조회
```
GET /questions/{id}
Response: 200 OK (QuestionResponseDTO)
{
  "id": number,
  "nickname": string,
  "title": string,
  "content": string,
  "viewCount": number,
  "createdAt": string,
  "updatedAt": string,
  "answers": [
    {
      "id": number,
      "nickname": string,
      "content": string,
      "accepted": boolean,
      "createdAt": string,
      "comments": [...]
    }
  ]
}
```

### 질문 수정
```
PATCH /questions/{id}
Headers: Authorization: Bearer {token}
Body: {
  "title": string (optional),
  "content": string (optional)
}
Response: 204 No Content
→ 작성자만 수정 가능
```

### 질문 삭제
```
DELETE /questions/{id}
Headers: Authorization: Bearer {token}
Response: 204 No Content
→ 작성자만 삭제 가능
```

### 질문 신고
```
PATCH /questions/{id}/report
Headers: Authorization: Bearer {token}
Response: 204 No Content
```

### 답변 채택
```
PATCH /questions/{questionId}/answers/{answerId}/accept
Headers: Authorization: Bearer {token}
Response: 204 No Content
→ 질문 작성자만 가능, 자기 답변은 채택 불가
```

---

## 💬 답변 (Answer)

### 답변 생성
```
POST /questions/{questionId}/answers
Headers: Authorization: Bearer {token}
Body: {
  "content": string (required, not blank)
}
Response: 201 Created (AnswerDetailResponseDTO)
{
  "id": number,
  "nickname": string,
  "content": string,
  "accepted": boolean,
  "likeCount": number,
  "dislikeCount": number,
  "myReactionType": "LIKE" | "DISLIKE" | "NONE",
  "createdAt": string
}
→ 자기 질문에는 답변 불가
```

### 답변 목록 조회
```
GET /questions/{questionId}/answers?page=0&size=10
Headers: Authorization: Bearer {token} (optional, 있으면 myReactionType 포함)
Response: 200 OK (Page<AnswerSummaryResponseDTO>)
{
  "content": [
    {
      "id": number,
      "nickname": string,
      "content": string,
      "accepted": boolean,
      "likeCount": number,
      "dislikeCount": number,
      "myReactionType": "LIKE" | "DISLIKE" | "NONE",
      "createdAt": string
    }
  ],
  ...
}
→ 채택된 답변이 최상단, 그 다음 최신순
```

### 답변 수정
```
PATCH /answers/{answerId}
Headers: Authorization: Bearer {token}
Body: {
  "content": string
}
Response: 200 OK (AnswerDetailResponseDTO)
→ 작성자만 수정 가능
```

### 답변 삭제
```
DELETE /answers/{answerId}
Headers: Authorization: Bearer {token}
Response: 204 No Content
→ 작성자만 삭제 가능
```

### 답변 신고
```
PATCH /questions/{questionId}/answers/{answerId}/report
Headers: Authorization: Bearer {token}
Response: 204 No Content
```

---

## 💭 댓글 (Comment)

### 댓글 생성
```
POST /answers/{answerId}/comments
Headers: Authorization: Bearer {token}
Body: {
  "content": string (required, not blank)
}
Response: 201 Created
{
  "id": number,
  "nickname": string,
  "content": string,
  "createdAt": string
}
```

### 댓글 목록 조회
```
GET /answers/{answerId}/comments
Response: 200 OK
[
  {
    "id": number,
    "nickname": string,
    "content": string,
    "createdAt": string
  }
]
→ 생성일시 오름차순
```

### 댓글 수정
```
PATCH /comments/{commentId}
Headers: Authorization: Bearer {token}
Body: {
  "content": string
}
Response: 200 OK (CommentResponseDTO)
```

### 댓글 삭제
```
DELETE /comments/{commentId}
Headers: Authorization: Bearer {token}
Response: 204 No Content
```

---

## 👍 추천/비추천 (Reaction)

### 추천/비추천 토글
```
POST /reaction/like
POST /reaction/dislike
Headers: Authorization: Bearer {token}
Body: {
  "targetType": "QUESTION" | "ANSWER" | "COMMENT",
  "targetId": number,
  "reactionType": "LIKE" | "DISLIKE"
}
Response: 204 No Content

동작 방식:
- 같은 타입이면 삭제 (토글)
- 다른 타입이면 변경
- 없으면 생성
```

### 추천/비추천 개수 조회
```
GET /reaction/count?targetType={type}&targetId={id}
Response: 200 OK
{
  "likeCount": number,
  "dislikeCount": number
}
```

### 내 반응 상태 조회
```
GET /reaction/me?targetType={type}&targetId={id}
Headers: Authorization: Bearer {token}
Response: 200 OK
{
  "hasReaction": boolean,
  "reactionType": "LIKE" | "DISLIKE" | "NONE"
}
```

### 반응 삭제
```
DELETE /reaction?targetType={type}&targetId={id}
Headers: Authorization: Bearer {token}
Response: 204 No Content
```

### 여러 대상의 반응 개수 조회 (배치)
```
GET /reaction/counts/likes?targetType={type}&targetIds=1,2,3
GET /reaction/counts/dislikes?targetType={type}&targetIds=1,2,3
Response: 200 OK
{
  "1": 5,
  "2": 3,
  "3": 0
}
```

### 여러 대상의 내 반응 상태 조회 (배치)
```
GET /reaction/me/map?targetType={type}&targetIds=1,2,3
Headers: Authorization: Bearer {token}
Response: 200 OK
{
  "1": "LIKE",
  "2": "DISLIKE",
  "3": "NONE" (없으면 키 자체가 없음)
}
```

---

## 🔔 알림 (Notification)

### SSE 구독 (실시간 알림)
```
GET /notification/subscribe
Headers: Authorization: Bearer {token}
Content-Type: text/event-stream

→ EventSource API 사용
→ 연결 유지 필요
→ 서버에서 알림 발생 시 자동으로 전송됨
```

### 알림 목록 조회
```
GET /notification
Headers: Authorization: Bearer {token}
Response: 200 OK
{
  "notifications": [
    {
      "id": number,
      "type": "NEW_ANSWER" | "REPORT_RECEIVED" | ...,
      "content": string,
      "senderId": number | null,
      "receiverId": number,
      "isRead": boolean,
      "createdAt": string
    }
  ],
  "totalCount": number
}
→ 최신순 정렬
```

### 알림 읽음 처리
```
PATCH /notification/{id}/read
Headers: Authorization: Bearer {token}
Response: 204 No Content
→ 수신자만 가능
```

### 알림 삭제
```
DELETE /notification/{id}
Headers: Authorization: Bearer {token}
Response: 204 No Content
→ 수신자만 가능
```

### SSE 연결 종료
```
DELETE /notification/subscribe
Headers: Authorization: Bearer {token}
Response: 204 No Content
```

---

## 🚨 신고 (Report)

### 신고 생성
```
POST /reports
Headers: Authorization: Bearer {token}
Body: {
  "reporterId": number,
  "reportedId": number,
  "entityType": "QUESTION" | "ANSWER" | "COMMENT" | "USER",
  "entityId": number,
  "category": "SPAM" | "ABUSE" | "INAPPROPRIATE" | ...,
  "reasonDetail": string (optional, 최대 500자)
}
Response: 201 Created (ReportResponseDTO)
```

### 대기 중인 신고 목록 조회 (관리자)
```
GET /reports?page=0&size=10
Headers: Authorization: Bearer {token} (ADMIN 권한 필요)
Response: 200 OK (Page<ReportResponseDTO>)
```

### 신고 승인 (관리자)
```
PATCH /reports/{reportId}/approve
Headers: Authorization: Bearer {token} (ADMIN 권한 필요)
Response: 204 No Content
```

### 신고 거부 (관리자)
```
PATCH /reports/{reportId}/reject
Headers: Authorization: Bearer {token} (ADMIN 권한 필요)
Response: 204 No Content
```

---

## 👨‍💼 관리자 (Admin)

### 사용자 목록 조회
```
GET /admin/users?page=0&size=20
Headers: Authorization: Bearer {token} (ADMIN 권한 필요)
Response: 200 OK (Page<ForAdminDTO>)
```

### 사용자 상세 조회
```
GET /admin/users/{id}
Headers: Authorization: Bearer {token} (ADMIN 권한 필요)
Response: 200 OK (UserMyPageResponseDTO)
```

### 사용자 삭제
```
DELETE /admin/users/{id}
Headers: Authorization: Bearer {token} (ADMIN 권한 필요)
Response: 204 No Content
```

---

## 📊 데이터 타입 및 Enum

### TargetType (Reaction 대상)
```typescript
type TargetType = "QUESTION" | "ANSWER" | "COMMENT"
```

### ReactionType (반응 타입)
```typescript
type ReactionType = "LIKE" | "DISLIKE"
```

### NotificationType (알림 타입)
```typescript
type NotificationType = 
  | "NEW_ANSWER"           // 새 답변
  | "REPORT_RECEIVED"      // 신고 접수
  // 기타 타입들...
```

### EntityType (신고 대상 엔티티)
```typescript
type EntityType = "QUESTION" | "ANSWER" | "COMMENT" | "USER"
```

### ReportCategory (신고 카테고리)
```typescript
type ReportCategory = 
  | "SPAM" 
  | "ABUSE" 
  | "INAPPROPRIATE"
  // 기타 카테고리들...
```

---

## ⚠️ 에러 처리

### 에러 응답 형식
```json
{
  "timestamp": "2024-01-01T00:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "code": "VALIDATION_ERROR",
  "message": "요청 값이 올바르지 않습니다.",
  "path": "/api/questions",
  "details": {
    "title": "제목은 필수입니다.",
    "content": "내용은 필수입니다."
  }
}
```

### 주요 에러 코드
- `VALIDATION_ERROR`: 요청 값 검증 실패
- `USER_NOT_FOUND`: 사용자를 찾을 수 없음
- `QUESTION_NOT_FOUND`: 질문을 찾을 수 없음
- `ANSWER_NOT_FOUND`: 답변을 찾을 수 없음
- `COMMENT_NOT_FOUND`: 댓글을 찾을 수 없음
- `QUESTION_OWNER_MISMATCH`: 질문 작성자가 아님
- `ANSWER_OWNER_MISMATCH`: 답변 작성자가 아님
- `USER_DUPLICATE`: 중복된 사용자 정보
- `REFRESH_TOKEN_NOT_FOUND`: Refresh Token을 찾을 수 없음
- `INVALID_TOKEN`: 유효하지 않은 토큰
- `EXPIRED_TOKEN`: 만료된 토큰

### HTTP 상태 코드
- `200 OK`: 성공
- `201 Created`: 생성 성공
- `204 No Content`: 성공 (응답 본문 없음)
- `400 Bad Request`: 잘못된 요청
- `401 Unauthorized`: 인증 실패
- `403 Forbidden`: 권한 없음
- `404 Not Found`: 리소스를 찾을 수 없음
- `500 Internal Server Error`: 서버 오류

---

## 🔄 특별한 동작 방식

### 1. OAuth2 로그인 플로우
```
1. 사용자가 "GitHub로 로그인" 버튼 클릭
2. window.location.href = "http://localhost:8080/oauth2/authorization/github"
3. GitHub 인증 완료 후 백엔드가 리다이렉트
   → http://localhost:3000?code={temporaryCode}
4. 프론트엔드에서 URL 파라미터에서 code 추출
5. POST /oauth/token?code={code} 호출
6. accessToken 받아서 저장
7. 메인 페이지로 이동
```

### 2. 토큰 자동 갱신
```
- Access Token 만료 시 자동으로 Refresh Token으로 재발급
- Axios Interceptor 사용 권장:
  1. 401 에러 발생 시
  2. POST /user/refreshToken 호출
  3. 새 accessToken으로 원래 요청 재시도
```

### 3. SSE 알림 연결
```javascript
// EventSource 사용 예시
const eventSource = new EventSource(
  'http://localhost:8080/notification/subscribe',
  {
    headers: {
      'Authorization': `Bearer ${accessToken}`
    }
  }
);

eventSource.onmessage = (event) => {
  const notification = JSON.parse(event.data);
  // 알림 처리
};

eventSource.onerror = (error) => {
  // 에러 처리 및 재연결 로직
};
```

### 4. 페이지네이션
```
- Spring Data의 Page 객체 사용
- 기본값: page=0, size=10 (질문), size=20 (사용자)
- 정렬: sort=createdAt,desc (최신순)
```

---

## 🎯 프론트엔드 개발 시 주의사항

### 1. 인증 토큰 관리
- Access Token: 메모리 또는 localStorage에 저장
- Refresh Token: 쿠키에 자동 저장 (HttpOnly, 프론트엔드에서 직접 접근 불가)
- 모든 API 요청에 `Authorization: Bearer {accessToken}` 헤더 포함

### 2. CORS 및 쿠키
- `credentials: 'include'` 설정 필요 (fetch API)
- Axios: `withCredentials: true` 설정

### 3. 에러 처리
- 401 에러: 토큰 재발급 시도 후 재요청
- 403 에러: 권한 없음 메시지 표시
- 400 에러: validation 에러 메시지 표시

### 4. 실시간 알림
- SSE 연결은 로그인 시 자동으로 시작
- 페이지 전환 시에도 연결 유지 고려
- 연결 끊김 시 자동 재연결 로직 구현

### 5. 권한 체크
- 작성자만 수정/삭제 버튼 표시
- 관리자만 관리자 페이지 접근
- 프론트엔드에서도 권한 체크 (UX 개선용, 실제 보안은 백엔드)

### 6. 폼 검증
- 회원가입: loginId (영문+숫자만), password (8-64자, 복잡도 요구사항)
- 질문/답변/댓글: content는 not blank
- 이메일: 이메일 형식 검증

### 7. 반응(Reaction) UI
- 같은 타입 클릭 시 토글 (추천 → 없음)
- 다른 타입 클릭 시 변경 (추천 → 비추천)
- 로딩 상태 관리 (중복 클릭 방지)

### 8. 답변 채택
- 질문 작성자만 채택 버튼 표시
- 자기 답변은 채택 불가
- 이미 채택된 답변이 있으면 다른 답변 채택 불가

---

## 📝 추가 참고사항

### 백엔드 버전
- Spring Boot 3.5.9
- Java 21
- JWT: jjwt 0.12.6

### 개발 환경
- 백엔드: http://localhost:8080
- 프론트엔드: http://localhost:3000 (예상)

### 테스트 계정
- 관리자 ID: 5 (하드코딩, 추후 변경 예정)

---

## 🚀 개발 시작 전 체크리스트

- [ ] Axios 또는 Fetch API 설정 (withCredentials 포함)
- [ ] Access Token 저장소 결정 (localStorage vs 메모리)
- [ ] 토큰 자동 갱신 Interceptor 구현
- [ ] 전역 에러 핸들러 구현
- [ ] SSE 연결 관리 로직 구현
- [ ] 라우팅 설정 (인증 필요/불필요 페이지 구분)
- [ ] 로딩 상태 관리
- [ ] 폼 검증 로직 구현

---

이 프롬프트를 기반으로 프론트엔드 개발을 진행하시면 됩니다. 추가 질문이나 수정 사항이 있으면 알려주세요!

