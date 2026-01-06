# Playground 프로젝트 디렉토리 구조

## 전체 프로젝트 구조

```
Playground/
├── BE/                          # 백엔드 (Spring Boot)
│   ├── build.gradle             # Gradle 빌드 설정
│   ├── settings.gradle          # Gradle 프로젝트 설정
│   ├── gradlew                  # Gradle Wrapper (Unix)
│   ├── gradlew.bat             # Gradle Wrapper (Windows)
│   ├── ReadMe.md               # 백엔드 README
│   │
│   ├── gradle/                  # Gradle Wrapper 파일
│   │   └── wrapper/
│   │       ├── gradle-wrapper.jar
│   │       └── gradle-wrapper.properties
│   │
│   ├── src/                     # 소스 코드
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── org/example/playground/
│   │   │   │       ├── domain/          # 도메인 로직 (제외)
│   │   │   │       ├── global/          # 전역 설정 (제외)
│   │   │   │       └── PlaygroundApplication.java
│   │   │   │
│   │   │   └── resources/               # 리소스 파일
│   │   │       ├── application.properties
│   │   │       ├── application-test.yml
│   │   │       └── data.sql
│   │   │
│   │   └── test/                       # 테스트 코드
│   │       └── java/
│   │           └── org/example/playground/
│   │               ├── global/          # 테스트 (제외)
│   │               └── PlaygroundApplicationTests.java
│   │
│   ├── build/                   # 빌드 산출물 (자동 생성)
│   ├── bin/                     # 컴파일된 클래스 (자동 생성)
│   └── out/                     # IntelliJ 빌드 산출물 (자동 생성)
│
├── FE/                          # 프론트엔드 (React + TypeScript) (제외)
│
└── readMe.md                    # 프로젝트 루트 README
```

## BE 디렉토리 상세 구조 (domain, global 제외)

### 루트 레벨 파일

```
BE/
├── build.gradle                 # Gradle 빌드 설정 파일
├── settings.gradle              # Gradle 프로젝트 설정
├── gradlew                     # Gradle Wrapper 실행 스크립트 (Unix)
├── gradlew.bat                 # Gradle Wrapper 실행 스크립트 (Windows)
└── ReadMe.md                   # 백엔드 README 문서
```

### Gradle 설정

```
BE/gradle/
└── wrapper/
    ├── gradle-wrapper.jar      # Gradle Wrapper JAR
    └── gradle-wrapper.properties # Gradle 버전 설정
```

### 소스 코드 구조

```
BE/src/
├── main/
│   ├── java/
│   │   └── org/example/playground/
│   │       ├── domain/          # 도메인 로직 (제외)
│   │       ├── global/          # 전역 설정 (제외)
│   │       └── PlaygroundApplication.java  # 메인 애플리케이션 클래스
│   │
│   └── resources/               # 리소스 파일
│       ├── application.properties          # 기본 설정 파일
│       ├── application-test.yml            # 테스트 프로파일 설정
│       └── data.sql                        # H2 초기 데이터
│
└── test/
    └── java/
        └── org/example/playground/
            ├── global/          # 테스트 코드 (제외)
            └── PlaygroundApplicationTests.java  # 애플리케이션 테스트
```

### 빌드 산출물 (자동 생성, .gitignore에 포함)

```
BE/
├── build/                       # Gradle 빌드 산출물
│   ├── classes/                 # 컴파일된 클래스 파일
│   ├── generated/               # 생성된 소스 파일
│   ├── reports/                 # 빌드 리포트
│   ├── resources/               # 복사된 리소스 파일
│   └── tmp/                     # 임시 파일
│
├── bin/                         # IntelliJ 컴파일 산출물
│   ├── default/
│   ├── generated-sources/
│   ├── generated-test-sources/
│   └── main/
│
└── out/                         # IntelliJ 빌드 산출물
    └── production/
        ├── classes/
        └── resources/
```

## 주요 파일 설명

### 빌드 관련

- **build.gradle**: 프로젝트 의존성, 플러그인, 빌드 설정
- **settings.gradle**: Gradle 프로젝트 이름 및 설정
- **gradlew / gradlew.bat**: Gradle Wrapper 실행 스크립트

### 설정 파일

- **application.properties**: Spring Boot 기본 설정
- **application-test.yml**: 테스트 환경 설정 (H2, JWT 등)
- **data.sql**: H2 데이터베이스 초기 데이터

### 소스 코드

- **PlaygroundApplication.java**: Spring Boot 메인 애플리케이션 클래스
- **PlaygroundApplicationTests.java**: 기본 애플리케이션 테스트

## 디렉토리 제외 사항

- **FE/**: 프론트엔드 디렉토리 (제외)
- **domain/**: 도메인 로직 디렉토리 (제외)
- **global/**: 전역 설정 디렉토리 (제외)
- **build/**, **bin/**, **out/**: 빌드 산출물 (자동 생성, 제외)

## 참고사항

- 빌드 산출물(`build/`, `bin/`, `out/`)은 `.gitignore`에 포함되어 Git에 커밋되지 않습니다.
- `src/main/resources/`의 설정 파일들은 프로파일에 따라 다른 파일을 사용할 수 있습니다.
- `data.sql`은 H2 데이터베이스 초기화 시 자동으로 실행됩니다.

