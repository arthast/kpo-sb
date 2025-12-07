# Build stage
FROM gradle:8.11.1-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle :file-storing-service:build -x test --no-daemon

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/file-storing-service/build/libs/*.jar app.jar
RUN mkdir -p /app/uploads
EXPOSE 8081
CMD ["java", "-jar", "app.jar"]

