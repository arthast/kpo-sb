# Краткая инструкция по фронтенду

## Проблема: Network Error

### Причина
API_BASE_URL указывал на `http://localhost:8080`, что не работает внутри Docker контейнера.

### Решение
В `frontend/src/App.js` изменено:
```javascript
// Было:
const API_BASE_URL = 'http://localhost:8080';

// Стало:
const API_BASE_URL = '';
```

Теперь все запросы идут относительно текущего домена и проксируются через nginx.

## Архитектура запросов

```
Браузер (http://localhost:3000/api/accounts/1)
    ↓
Frontend Nginx (http://api-gateway:8080/api/accounts/1)
    ↓
API Gateway (http://payment-service:8081/api/accounts/1)
    ↓
Payment Service
    ↓
PostgreSQL
```

## Конфигурация nginx

Файл `frontend/nginx.conf`:
- Проксирует `/api/*` запросы к `api-gateway:8080`
- Добавляет правильные заголовки
- Устанавливает таймауты 60 секунд
- Включает CORS для совместимости

## Запуск

```bash
cd homework/async
docker-compose up -d
```

Откройте http://localhost:3000 в браузере.

## Перезапуск фронтенда после изменений

```bash
docker-compose stop frontend
docker-compose rm -f frontend
docker-compose build frontend
docker-compose up -d frontend
```

Или полный перезапуск:
```bash
docker-compose down
docker-compose up -d
```

