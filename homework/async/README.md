# Домашнее задание №4: Асинхронное межсервисное взаимодействие

## Описание проекта

Микросервисная система интернет-магазина **"Гоzон"** для обработки заказов и платежей с асинхронным взаимодействием через Apache Kafka.

## Архитектура

```
┌─────────────────┐
│   API Gateway   │ ← Единая точка входа (порт 8080)
│   (порт 8080)   │
└────────┬────────┘
         │
    ┌────┴────┐
    │         │
┌───▼───┐ ┌───▼───┐
│ Order │ │Payment│
│Service│ │Service│
│(8082) │ │(8081) │
└───┬───┘ └───┬───┘
    │         │
    │  Kafka  │
    └────┬────┘
         │
    ┌────▼────┐
    │PostgreSQL│
    │ (2 БД)  │
    └─────────┘
```

### Микросервисы

1. **API Gateway** (порт 8080)
   - Маршрутизация запросов к микросервисам
   - Единая точка входа для клиентов

2. **Order Service** (порт 8082)
   - Создание заказов
   - Просмотр списка заказов
   - Просмотр статуса заказа
   - Реализует **Transactional Outbox** паттерн

3. **Payment Service** (порт 8081)
   - Создание счетов
   - Пополнение счетов
   - Просмотр баланса
   - Реализует **Transactional Inbox + Outbox** паттерны
   - Обеспечивает **exactly-once** семантику при списании

## Технологии

- **Java 21** + **Spring Boot 3.4.2**
- **Apache Kafka** - брокер сообщений
- **PostgreSQL 15** - базы данных
- **Docker** + **Docker Compose** - контейнеризация
- **SpringDoc OpenAPI** - документация API

## Паттерны

### Transactional Outbox (Order Service)
1. При создании заказа в одной транзакции сохраняется заказ и событие в таблицу `outbox_events`
2. Scheduler периодически читает неотправленные события и публикует их в Kafka
3. После успешной отправки событие помечается как отправленное

### Transactional Inbox (Payment Service)
1. Consumer получает событие из Kafka и сохраняет в таблицу `inbox_events`
2. Scheduler обрабатывает необработанные события
3. Дедупликация по `event_id` предотвращает повторную обработку

### Exactly-Once семантика
1. Таблица `processed_payments` хранит все обработанные платежи
2. Проверка по `order_id` предотвращает повторное списание
3. Оптимистичная блокировка (`@Version`) для счетов предотвращает race conditions

## Запуск

### Предварительные требования
- Docker и Docker Compose
- 4 GB свободной RAM

### Запуск всей системы

```bash
cd homework/async
docker-compose up --build
```

### Проверка работоспособности

После запуска доступны:
- API Gateway Swagger UI: http://localhost:8080/swagger-ui.html
- Order Service Swagger UI: http://localhost:8082/swagger-ui.html
- Payment Service Swagger UI: http://localhost:8081/swagger-ui.html

## API Endpoints

### Payment Service API

| Метод | Endpoint | Описание |
|-------|----------|----------|
| POST | `/api/accounts` | Создать счет |
| POST | `/api/accounts/deposit` | Пополнить счет |
| GET | `/api/accounts/{userId}` | Получить баланс |

### Order Service API

| Метод | Endpoint | Описание |
|-------|----------|----------|
| POST | `/api/orders` | Создать заказ |
| GET | `/api/orders/user/{userId}` | Список заказов пользователя |
| GET | `/api/orders/{orderId}` | Статус заказа |

## Тестирование

### Сценарий тестирования

1. **Создать счет** для пользователя:
```bash
curl -X POST http://localhost:8080/api/accounts \
  -H "Content-Type: application/json" \
  -d '{"userId": 1}'
```

2. **Пополнить счет**:
```bash
curl -X POST http://localhost:8080/api/accounts/deposit \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "amount": 1000}'
```

3. **Создать заказ** (асинхронно запустит оплату):
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "amount": 150, "description": "Свитер с оленями"}'
```

4. **Проверить статус заказа** (через ~10 секунд статус изменится):
```bash
curl http://localhost:8080/api/orders/1
```

5. **Проверить баланс** (должно быть 850):
```bash
curl http://localhost:8080/api/accounts/1
```

## Статусы заказов

- `NEW` - заказ создан, ожидает обработки платежа
- `FINISHED` - оплата успешно прошла
- `CANCELLED` - оплата не удалась (недостаточно средств или нет счета)

## Структура проекта

```
homework/async/
├── api-gateway/           # API Gateway сервис
│   ├── src/
│   ├── build.gradle.kts
│   └── Dockerfile
├── order-service/         # Order Service
│   ├── src/
│   │   └── main/java/hse/kpo/
│   │       ├── controllers/
│   │       ├── services/
│   │       ├── repositories/
│   │       ├── domains/
│   │       ├── outbox/
│   │       └── kafka/
│   ├── build.gradle.kts
│   └── Dockerfile
├── payment-service/       # Payment Service
│   ├── src/
│   │   └── main/java/hse/kpo/
│   │       ├── controllers/
│   │       ├── services/
│   │       ├── repositories/
│   │       ├── domains/
│   │       ├── inbox/
│   │       ├── outbox/
│   │       └── kafka/
│   ├── build.gradle.kts
│   └── Dockerfile
├── docker-compose.yml
├── postman_collection.json
└── README.md
```

## Kafka Topics

- `payment-requests` - запросы на оплату (Order → Payment)
- `payment-results` - результаты оплаты (Payment → Order)

## Остановка

```bash
docker-compose down
```

Для удаления данных:
```bash
docker-compose down -v
```