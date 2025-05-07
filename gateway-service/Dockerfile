FROM openjdk:17-jdk-slim AS builder
WORKDIR /app
COPY settings.gradle build.gradle gradlew /app/
COPY gradle /app/gradle
COPY security-common/build.gradle /app/security-common/
COPY gateway-service/build.gradle /app/gateway-service/
COPY security-common/src /app/security-common/src
COPY gateway-service/src /app/gateway-service/src
RUN ./gradlew :gateway-service:bootJar -x test

# Stage 2: Create the runtime image
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/gateway-service/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
