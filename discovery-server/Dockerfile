FROM openjdk:17-jdk-slim AS builder
WORKDIR /app
COPY settings.gradle build.gradle gradlew /app/
COPY gradle /app/gradle
COPY discovery-server/build.gradle /app/discovery-server/
COPY discovery-server/src /app/discovery-server/src
RUN ./gradlew :discovery-server:bootJar -x test

# Stage 2: Create the runtime image
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/discovery-server/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]