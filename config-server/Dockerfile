FROM openjdk:17-jdk-slim AS builder
WORKDIR /app
COPY settings.gradle build.gradle gradlew /app/
COPY gradle /app/gradle
COPY config-server/build.gradle /app/config-server/
COPY config-server/src /app/config-server/src
RUN ./gradlew :config-server:bootJar -x test

# Stage 2: Create the runtime image
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/config-server/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]