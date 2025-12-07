# Build stage
FROM gradle:8.11.1-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle :file-analysis-service:build -x test --no-daemon

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/file-analysis-service/build/libs/*.jar app.jar
EXPOSE 8082
CMD ["java", "-jar", "app.jar"]

