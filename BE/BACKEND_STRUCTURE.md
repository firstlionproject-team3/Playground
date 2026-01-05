# Playground 백엔드 디렉토리 구조

## 📁 현재 프로젝트 구조

```
playground/
├── .gradle/                         # Gradle 빌드 캐시
├── .idea/                           # IntelliJ IDEA 설정
├── build/                           # 빌드 출력 디렉토리
├── gradle/                          # Gradle Wrapper
│   ├── wrapper/
│   │   ├── gradle-wrapper.jar
│   │   └── gradle-wrapper.properties
│   └── libs/
│
├── src/
│   ├── main/
│   │   ├── generated/               # QueryDSL 등 생성된 코드
│   │   │
│   │   ├── java/
│   │   │   └── org/example/playground/
│   │   │       │
│   │   │       ├── PlaygroundApplication.java  # 메인 애플리케이션 클래스
│   │   │       │
│   │   │       ├── domain/                      # 도메인별 패키지 (DDD 구조)
│   │   │       │   │
│   │   │       │   ├── user/                   # 사용자 도메인
│   │   │       │   │   ├── controller/
│   │   │       │   │   │   ├── UserController.java
│   │   │       │   │   │   └── AdminUserController.java
│   │   │       │   │   │
│   │   │       │   │   ├── service/
│   │   │       │   │   │   ├── UserService.java (인터페이스)
│   │   │       │   │   │   ├── UserServiceImpl.java
│   │   │       │   │   │   ├── AdminUserService.java
│   │   │       │   │   │   ├── AdminUserServiceImpl.java
│   │   │       │   │   │   ├── UserFactory.java
│   │   │       │   │   │   └── UserTxService.java
│   │   │       │   │   │
│   │   │       │   │   ├── repository/
│   │   │       │   │   │   ├── UserRepository.java
│   │   │       │   │   │   ├── RoleRepository.java
│   │   │       │   │   │   └── UserRoleRepository.java
│   │   │       │   │   │
│   │   │       │   │   ├── entity/
│   │   │       │   │   │   ├── User.java
│   │   │       │   │   │   ├── Role.java
│   │   │       │   │   │   ├── UserRole.java
│   │   │       │   │   │   └── UserStatus.java (Enum)
│   │   │       │   │   │
│   │   │       │   │   ├── dto/
│   │   │       │   │   │   ├── UserRegisterRequestDTO.java
│   │   │       │   │   │   ├── UserRegisterResponseDTO.java
│   │   │       │   │   │   ├── UserUpdateRequestDTO.java
│   │   │       │   │   │   ├── UserMyPageResponseDTO.java
│   │   │       │   │   │   ├── OAuth2UserInfo.java
│   │   │       │   │   │   ├── SecurityResponseForJWT.java
│   │   │       │   │   │   └── ForAdminDTO.java
│   │   │       │   │   │
│   │   │       │   │   ├── exception/
│   │   │       │   │   │   ├── UserErrorCode.java
│   │   │       │   │   │   ├── UserException.java
│   │   │       │   │   │   └── UserNotFoundException.java
│   │   │       │   │   │
│   │   │       │   │   └── readme.md
│   │   │       │   │
│   │   │       │   ├── question/                # 질문 도메인
│   │   │       │   │   ├── controller/
│   │   │       │   │   │   └── QuestionController.java
│   │   │       │   │   │
│   │   │       │   │   ├── service/
│   │   │       │   │   │   └── QuestionService.java
│   │   │       │   │   │
│   │   │       │   │   ├── repository/
│   │   │       │   │   │   └── QuestionRepository.java
│   │   │       │   │   │
│   │   │       │   │   ├── entity/
│   │   │       │   │   │   └── Question.java
│   │   │       │   │   │
│   │   │       │   │   ├── dto/
│   │   │       │   │   │   ├── request/
│   │   │       │   │   │   │   ├── QuestionCreateRequestDTO.java
│   │   │       │   │   │   │   └── QuestionUpdateRequestDTO.java
│   │   │       │   │   │   └── response/
│   │   │       │   │   │       ├── QuestionSummaryResponseDTO.java
│   │   │       │   │   │       ├── QuestionDetailResponseDTO.java
│   │   │       │   │   │       ├── QuestionResponseDTO.java
│   │   │       │   │   │       └── IdResponse.java
│   │   │       │   │   │
│   │   │       │   │   ├── exception/
│   │   │       │   │   │   └── QuestionErrorCode.java
│   │   │       │   │   │
│   │   │       │   │   └── readme.md
│   │   │       │   │
│   │   │       │   ├── answer/                 # 답변 도메인
│   │   │       │   │   ├── controller/
│   │   │       │   │   │   └── AnswerController.java
│   │   │       │   │   │
│   │   │       │   │   ├── service/
│   │   │       │   │   │   └── AnswerService.java
│   │   │       │   │   │
│   │   │       │   │   ├── repository/
│   │   │       │   │   │   └── AnswerRepository.java
│   │   │       │   │   │
│   │   │       │   │   ├── entity/
│   │   │       │   │   │   └── Answer.java
│   │   │       │   │   │
│   │   │       │   │   ├── dto/
│   │   │       │   │   │   ├── request/
│   │   │       │   │   │   │   ├── AnswerCreateRequestDTO.java
│   │   │       │   │   │   │   └── AnswerUpdateRequestDTO.java
│   │   │       │   │   │   └── response/
│   │   │       │   │   │       ├── AnswerSummaryResponseDTO.java
│   │   │       │   │   │       ├── AnswerDetailResponseDTO.java
│   │   │       │   │   │       └── AnswerResponseDTO.java
│   │   │       │   │   │
│   │   │       │   │   ├── exception/
│   │   │       │   │   │   └── AnswerErrorCode.java
│   │   │       │   │   │
│   │   │       │   │   └── readme.md
│   │   │       │   │
│   │   │       │   ├── comment/                # 댓글 도메인
│   │   │       │   │   ├── controller/
│   │   │       │   │   │   └── CommentController.java
│   │   │       │   │   │
│   │   │       │   │   ├── service/
│   │   │       │   │   │   └── CommentService.java
│   │   │       │   │   │
│   │   │       │   │   ├── repository/
│   │   │       │   │   │   └── CommentRepository.java
│   │   │       │   │   │
│   │   │       │   │   ├── entity/
│   │   │       │   │   │   └── Comment.java
│   │   │       │   │   │
│   │   │       │   │   ├── dto/
│   │   │       │   │   │   ├── request/
│   │   │       │   │   │   │   └── CommentRequestDTO.java
│   │   │       │   │   │   └── response/
│   │   │       │   │   │       └── CommentResponseDTO.java
│   │   │       │   │   │
│   │   │       │   │   ├── exception/
│   │   │       │   │   │   ├── CommentErrorCode.java
│   │   │       │   │   │   └── CommentException.java
│   │   │       │   │   │
│   │   │       │   │   └── readme.md
│   │   │       │   │
│   │   │       │   ├── reaction/               # 추천/비추천 도메인
│   │   │       │   │   ├── controller/
│   │   │       │   │   │   └── ReactionController.java
│   │   │       │   │   │
│   │   │       │   │   ├── service/
│   │   │       │   │   │   ├── ReactionService.java
│   │   │       │   │   │   └── ReactionTargetFinder.java
│   │   │       │   │   │
│   │   │       │   │   ├── repository/
│   │   │       │   │   │   └── ReactionRepository.java
│   │   │       │   │   │
│   │   │       │   │   ├── entity/
│   │   │       │   │   │   ├── Reaction.java
│   │   │       │   │   │   ├── ReactionType.java (Enum)
│   │   │       │   │   │   ├── TargetType.java (Enum)
│   │   │       │   │   │   └── ReactionCountable.java (인터페이스)
│   │   │       │   │   │
│   │   │       │   │   └── dto/
│   │   │       │   │       └── ReactionRequestDto.java
│   │   │       │   │
│   │   │       │   ├── notification/          # 알림 도메인
│   │   │       │   │   ├── controller/
│   │   │       │   │   │   └── NotificationController.java
│   │   │       │   │   │
│   │   │       │   │   ├── service/
│   │   │       │   │   │   ├── NotificationService.java
│   │   │       │   │   │   └── NotificationSseService.java
│   │   │       │   │   │
│   │   │       │   │   ├── repository/
│   │   │       │   │   │   └── NotificationRepository.java
│   │   │       │   │   │
│   │   │       │   │   ├── entity/
│   │   │       │   │   │   ├── Notification.java
│   │   │       │   │   │   └── NotificationType.java (Enum)
│   │   │       │   │   │
│   │   │       │   │   ├── dto/
│   │   │       │   │   │   ├── NotificationRequestDTO.java
│   │   │       │   │   │   ├── NotificationResponseDTO.java
│   │   │       │   │   │   └── NotificationListResponseDTO.java
│   │   │       │   │   │
│   │   │       │   │   ├── exception/
│   │   │       │   │   │   └── NotificationErrorCode.java
│   │   │       │   │   │
│   │   │       │   │   └── readme.md
│   │   │       │   │
│   │   │       │   ├── refreshtoken/          # Refresh Token 도메인
│   │   │       │   │   ├── controller/
│   │   │       │   │   │   └── RefreshTokenController.java
│   │   │       │   │   │
│   │   │       │   │   ├── service/
│   │   │       │   │   │   └── RefreshTokenService.java
│   │   │       │   │   │
│   │   │       │   │   ├── repository/
│   │   │       │   │   │   └── RefreshTokenRepository.java
│   │   │       │   │   │
│   │   │       │   │   ├── entity/
│   │   │       │   │   │   └── RefreshToken.java
│   │   │       │   │   │
│   │   │       │   │   ├── dto/
│   │   │       │   │   │   └── AccessAndRefreshTokenDTO.java
│   │   │       │   │   │
│   │   │       │   │   └── exception/
│   │   │       │   │       ├── RefreshTokenErrorCode.java
│   │   │       │   │       └── RefreshTokenException.java
│   │   │       │   │
│   │   │       │   ├── report/                # 신고 도메인
│   │   │       │   │   ├── controller/
│   │   │       │   │   │   ├── ReportController.java
│   │   │       │   │   │   └── AdminReportController.java
│   │   │       │   │   │
│   │   │       │   │   ├── service/
│   │   │       │   │   │   ├── ReportService.java (인터페이스)
│   │   │       │   │   │   └── ReportServiceImpl.java
│   │   │       │   │   │
│   │   │       │   │   ├── repository/
│   │   │       │   │   │   └── ReportRepository.java
│   │   │       │   │   │
│   │   │       │   │   ├── entity/
│   │   │       │   │   │   ├── Report.java
│   │   │       │   │   │   ├── ReportStatus.java (Enum)
│   │   │       │   │   │   ├── ReportCategory.java (Enum)
│   │   │       │   │   │   ├── ReportReason.java (Enum)
│   │   │       │   │   │   ├── EntityType.java (Enum)
│   │   │       │   │   │   └── ReportTarget.java
│   │   │       │   │   │
│   │   │       │   │   ├── dto/
│   │   │       │   │   │   ├── ReportCreateRequestDTO.java
│   │   │       │   │   │   └── ReportResponseDTO.java
│   │   │       │   │   │
│   │   │       │   │   └── exception/
│   │   │       │   │       ├── ReportErrorCode.java
│   │   │       │   │       └── ReportException.java
│   │   │       │   │
│   │   │       │   └── [각 도메인별 readme.md 파일]
│   │   │       │
│   │   │       └── global/                    # 전역 설정 및 공통 기능
│   │   │           │
│   │   │           ├── config/                # 설정 클래스
│   │   │           │   └── SecurityConfig.java
│   │   │           │
│   │   │           ├── exception/            # 전역 예외 처리
│   │   │           │   ├── ErrorCode.java (인터페이스)
│   │   │           │   ├── BusinessException.java
│   │   │           │   ├── ErrorResponse.java
│   │   │           │   └── GlobalExceptionHandler.java
│   │   │           │
│   │   │           ├── security/             # 보안 관련
│   │   │           │   ├── auth/
│   │   │           │   │   ├── controller/
│   │   │           │   │   │   └── AuthController.java
│   │   │           │   │   ├── service/
│   │   │           │   │   │   └── AuthService.java
│   │   │           │   │   └── exception/
│   │   │           │   │       └── LoginFailedException.java
│   │   │           │   │
│   │   │           │   ├── jwt/
│   │   │           │   │   ├── JwtTokenProvider.java
│   │   │           │   │   ├── JwtTokenFilter.java
│   │   │           │   │   ├── dto/
│   │   │           │   │   │   └── TokenDTO.java
│   │   │           │   │   └── exception/
│   │   │           │   │       ├── JwtExceptionCode.java
│   │   │           │   │       └── JwtAuthenticationEntryPoint.java
│   │   │           │   │
│   │   │           │   └── user/
│   │   │           │       ├── CustomUserDetails.java
│   │   │           │       └── dto/
│   │   │           │           └── LoginRequestDTO.java
│   │   │           │
│   │   │           ├── oauth2/               # OAuth2 소셜 로그인
│   │   │           │   ├── controller/
│   │   │           │   │   ├── OAuthController.java
│   │   │           │   │   └── OAuthTestController.java
│   │   │           │   │
│   │   │           │   ├── service/
│   │   │           │   │   └── CustomOAuth2UserService.java
│   │   │           │   │
│   │   │           │   ├── handler/
│   │   │           │   │   ├── OAuth2SuccessHandler.java
│   │   │           │   │   └── OAuth2FailureHandler.java
│   │   │           │   │
│   │   │           │   ├── entity/
│   │   │           │   │   └── CustomOAuth2User.java
│   │   │           │   │
│   │   │           │   ├── store/
│   │   │           │   │   ├── TempCodeStore.java
│   │   │           │   │   └── CodeInfo.java
│   │   │           │   │
│   │   │           │   └── exception/
│   │   │           │       ├── OAuthErrorCode.java
│   │   │           │       ├── OAuthUnsupportedProviderException.java
│   │   │           │       ├── OAuthProviderIdMappingException.java
│   │   │           │       ├── OAuthAttributesMappingException.java
│   │   │           │       └── TemporaryCodeGenerationException.java
│   │   │           │
│   │   │           └── util/                 # 유틸리티 클래스
│   │   │               └── CookieUtil.java
│   │   │
│   │   └── resources/                        # 리소스 파일
│   │       ├── application.properties        # 메인 설정 파일
│   │       ├── application-test.yml          # 테스트 환경 설정
│   │       ├── data.sql                      # 초기 데이터 (선택)
│   │       ├── static/                       # 정적 리소스
│   │       └── templates/                    # 템플릿 파일 (선택)
│   │
│   └── test/                                 # 테스트 코드
│       └── java/
│           └── org/example/playground/
│               ├── PlaygroundApplicationTests.java
│               └── global/
│                   ├── oauth2/
│                   │   ├── OAuth2SuccessHandlerTest.java
│                   │   └── OAuth2FailureHandlerTest.java
│                   └── security/
│                       └── jwt/
│                           └── [JWT 테스트 파일]
│
├── build.gradle                             # Gradle 빌드 설정
├── settings.gradle                          # Gradle 프로젝트 설정
├── gradlew                                  # Gradle Wrapper (Unix)
├── gradlew.bat                             # Gradle Wrapper (Windows)
├── .gitignore
├── README.md
└── HELP.md
```

## 📦 패키지 구조 설명

### 1. Domain Layer (도메인 계층)

각 도메인은 **DDD(Domain-Driven Design)** 패턴을 따릅니다:

```
domain/
└── {domain-name}/
    ├── controller/      # REST API 엔드포인트
    ├── service/        # 비즈니스 로직
    ├── repository/     # 데이터 접근 계층
    ├── entity/        # JPA 엔티티
    ├── dto/           # 데이터 전송 객체
    │   ├── request/   # 요청 DTO
    │   └── response/  # 응답 DTO
    └── exception/     # 도메인별 예외
```

### 2. Global Layer (전역 계층)

프로젝트 전역에서 사용되는 공통 기능:

- **config/**: Spring 설정 클래스
- **exception/**: 전역 예외 처리
- **security/**: 인증/인가 관련
- **oauth2/**: OAuth2 소셜 로그인
- **util/**: 유틸리티 클래스

## 🔧 주요 파일 설명

### `build.gradle`

```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.5.9'
    id 'io.spring.dependency-management' version '1.1.7'
}

dependencies {
    // Spring Boot Starters
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-oauth2-client'
    implementation 'org.springframework.boot:spring-boot-starter-validation'

    // JWT
    implementation 'io.jsonwebtoken:jjwt-api:0.12.6'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.6'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.6'

    // Database
    runtimeOnly 'com.h2database:h2'
    runtimeOnly 'com.mysql:mysql-connector-j'

    // Lombok
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'

    // Test
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
}
```

### `application.properties`

```properties
# 애플리케이션 설정
spring.application.name=playground
spring.profiles.active=secret

# OAuth2 설정
spring.security.oauth2.client.registration.github.client-id=${github.client-id}
spring.security.oauth2.client.registration.github.client-secret=${github.client-secret}
spring.security.oauth2.client.registration.naver.client-id=${naver.client-id}
spring.security.oauth2.client.registration.naver.client-secret=${naver.client-secret}

# OAuth 콜백 URL
playground.oauth.callback-url=http://localhost:8080/test/oauth/verify-code
playground.oauth.fail.url=/test/oauth/fail
```

## 🎯 디렉토리 구조 원칙

### 1. **도메인 중심 구조 (DDD)**

- 각 도메인은 독립적으로 관리
- 도메인 간 의존성 최소화
- 도메인별 예외 처리

### 2. **레이어 분리**

- **Controller**: HTTP 요청/응답 처리
- **Service**: 비즈니스 로직
- **Repository**: 데이터 접근
- **Entity**: 도메인 모델

### 3. **DTO 패턴**

- Request/Response 분리
- 도메인 엔티티 노출 방지
- API 버전 관리 용이

### 4. **예외 처리 계층화**

- 도메인별 ErrorCode 정의
- GlobalExceptionHandler로 중앙 처리
- 일관된 에러 응답 형식

## 📝 개선 제안

### 1. 추가 고려 사항

```
src/main/java/org/example/playground/
├── global/
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── JpaConfig.java          # JPA 추가 설정 (필요 시)
│   │   ├── WebConfig.java          # WebMvc 설정 (필요 시)
│   │   └── CorsConfig.java         # CORS 설정 분리 (선택)
│   │
│   └── validation/                 # 커스텀 Validator (필요 시)
│       └── ...
```

### 2. 테스트 구조 개선

```
src/test/java/org/example/playground/
├── domain/
│   ├── user/
│   │   ├── UserServiceTest.java
│   │   └── UserControllerTest.java
│   └── question/
│       └── ...
└── global/
    └── ...
```

### 3. 설정 파일 분리

```
src/main/resources/
├── application.properties
├── application-dev.properties
├── application-prod.properties
└── application-test.yml
```

## 🔍 주요 패턴 및 관례

### 1. **Service 인터페이스 패턴**

- 복잡한 도메인은 인터페이스와 구현체 분리
- 예: `UserService` / `UserServiceImpl`
- 예: `ReportService` / `ReportServiceImpl`

### 2. **Factory 패턴**

- 복잡한 객체 생성은 Factory 클래스 사용
- 예: `UserFactory` (닉네임 자동 생성 등)

### 3. **Repository 커스텀 쿼리**

- JPA 메서드 네이밍
- `@Query` 어노테이션
- Fetch Join 최적화

### 4. **Entity 생명주기 콜백**

- `@PrePersist`: 저장 전 기본값 설정
- `@SQLDelete`: 소프트 삭제

## 📚 참고사항

### 현재 사용 기술 스택

- **Spring Boot**: 3.5.9
- **Java**: 21
- **JPA/Hibernate**: Spring Data JPA
- **Security**: Spring Security + JWT
- **OAuth2**: Spring Security OAuth2 Client
- **Database**: H2 (개발), MySQL (운영)
- **Build Tool**: Gradle

### 네이밍 컨벤션

- **Controller**: `{Domain}Controller`
- **Service**: `{Domain}Service` / `{Domain}ServiceImpl`
- **Repository**: `{Domain}Repository`
- **Entity**: 단수형 (User, Question, Answer)
- **DTO**: `{Domain}{Purpose}DTO` (Request/Response)
- **Exception**: `{Domain}Exception` / `{Domain}ErrorCode`

---

이 구조는 현재 프로젝트의 실제 구조를 기반으로 작성되었습니다. 프로젝트 성장에 따라 필요한 부분을 확장하시면 됩니다!
