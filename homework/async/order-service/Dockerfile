# Build stage
FROM gradle:8.11.1-jdk21 AS build

WORKDIR /app

# Копируем файлы сборки
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src

# Собираем проект
RUN gradle build -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

# Копируем собранный jar
COPY --from=build /app/build/libs/*.jar app.jar

# Открываем порт
EXPOSE 8082

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
