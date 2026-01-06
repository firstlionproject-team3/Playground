```
com.team.project
├─ ProjectApplication.java

├─ global
│   ├─ config
│   │   ├─ JpaConfig.java
│   │   ├─ WebConfig.java
│   │   └─ SwaggerConfig.java
│   ├─ security
│   │   ├─ config
│   │   │   ├─ SecurityConfig.java
│   │   │   └─ CorsConfig.java
│   │   ├─ jwt
│   │   │   ├─ JwtTokenProvider.java
│   │   │   ├─ JwtAuthenticationFilter.java
│   │   │   └─ JwtExceptionHandlerFilter.java
│   │   ├─ oauth2
│   │   │   ├─ CustomOAuth2UserService.java
│   │   │   ├─ OAuth2SuccessHandler.java
│   │   │   └─ OAuth2FailureHandler.java
│   │   ├─ principal
│   │   │   ├─ UserPrincipal.java
│   │   │   └─ UserPrincipalService.java
│   │   └─ util
│   │       └─ SecurityUtils.java
│   ├─ exception
│   │   ├─ GlobalExceptionHandler.java
│   │   ├─ ErrorCode.java
│   │   ├─ BusinessException.java
│   │   └─ common
│   │       ├─ NotFoundException.java
│   │       ├─ UnauthorizedException.java
│   │       └─ ForbiddenException.java
│   ├─ response
│   │   ├─ ApiResponse.java
│   │   └─ ApiErrorResponse.java
│   └─ util
│       ├─ TimeUtils.java
│       └─ CursorUtils.java

└─ domain
├─ user
│   ├─ controller
│   │   └─ UserController.java
│   ├─ service
│   │   ├─ UserService.java
│   │   └─ AuthService.java
│   ├─ repository
│   │   └─ UserRepository.java
│   ├─ entity
│   │   ├─ User.java
│   │   ├─ Role.java
│   │   └─ SocialAccount.java
│   ├─ dto
│   │   ├─ request
│   │   │   ├─ SignUpRequest.java
│   │   │   └─ LoginRequest.java
│   │   └─ response
│   │       ├─ TokenResponse.java
│   │       └─ UserResponse.java
│   └─ exception
│       ├─ UserNotFoundException.java
│       └─ DuplicateEmailException.java
│
├─ question
│   ├─ controller
│   ├─ service
│   ├─ repository
│   ├─ entity
│   ├─ dto
│   │   ├─ request
│   │   └─ response
│   └─ exception
│
├─ answer
│   ├─ controller
│   ├─ service
│   ├─ repository
│   ├─ entity
│   ├─ dto
│   │   ├─ request
│   │   └─ response
│   └─ exception
│
└─ comment
├─ controller
├─ service
├─ repository
├─ entity
├─ dto
│   ├─ request
│   └─ response
└─ exception
```