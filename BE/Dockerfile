FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

COPY . .

# gradlew 실행 권한 부여 (윈도우에서 올릴 때 깨지는 경우 방지)
RUN chmod +x ./gradlew

# 빌드 실행 (여기서 JAR 파일이 생성됨)
RUN ./gradlew bootJar

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]