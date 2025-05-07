FROM openjdk:17-jdk-slim AS builder
WORKDIR /app
COPY settings.gradle build.gradle gradlew /app/
COPY gradle /app/gradle
COPY multi-tenant-core/build.gradle /app/multi-tenant-core/
COPY security-common/build.gradle /app/security-common/
COPY user-service/build.gradle /app/user-service/
COPY multi-tenant-core/src /app/multi-tenant-core/src
COPY security-common/src /app/security-common/src
COPY user-service/src /app/user-service/src
RUN ./gradlew :user-service:bootJar -x test

# Stage 2: Create the runtime image
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/user-service/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]