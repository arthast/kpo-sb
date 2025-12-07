# Система Антиплагиат

Это микросервисная система, которая:

- принимает файлы работ от студентов;
- сохраняет их и информацию о сдаче;
- автоматически проверяет работы на заимствования (плагиат);
- выдаёт отчёты и дополнительную аналитику (облако слов).

---

## 1. Что запущено и на каких портах

После команды:

```bash
cd homework/antiplagiat
docker compose up --build
```

- API Gateway — `http://localhost:8080`
- File Storing Service — `http://localhost:8081`
- File Analysis Service — `http://localhost:8082`

Swagger UI:

- Gateway: `http://localhost:8080/swagger-ui.html`
- Storing: `http://localhost:8081/swagger-ui.html`
- Analysis: `http://localhost:8082/swagger-ui.html`

---

### Основные запросы (в общем виде)

Все запросы отправляются на API Gateway (`http://localhost:8080`).

- Отправить работу на проверку:

  ```bash
  POST http://localhost:8080/api/works/submit
  # тело: multipart/form-data
  #   file=@<путь_к_файлу>
  #   studentName=<имя студента>
  #   assignmentId=<id задания>
  ```

- Получить все сдачи:

  ```bash
  GET http://localhost:8080/api/submissions
  ```

- Получить отчёт по конкретной сдаче:

  ```bash
  GET http://localhost:8080/api/submissions/{submissionId}/report
  ```

- Получить облако слов по конкретной сдаче:

  ```bash
  GET http://localhost:8080/api/submissions/{submissionId}/wordcloud
  ```

- Получить все отчёты:

  ```bash
  GET http://localhost:8080/api/reports
  ```

- Получить отчёты по конкретному заданию:

  ```bash
  GET http://localhost:8080/api/works/{assignmentId}/reports
  ```

---

## 2. Примеры работы: создаём несколько файлов и проверяем плагиат

Сделаем отдельную папку `files` и создадим там несколько тестовых работ.

```bash
mkdir -p files

# Работа 1 — оригинальный текст
cat > files/work1.txt << 'EOF'
Программирование это интересно и увлекательно. 
В этой работе я самостоятельно решаю задачи и пишу код.
EOF

# Работа 2 — почти такой же текст
cat > files/work2.txt << 'EOF'
Программирование это очень интересно и увлекательно. 
В этой работе я просто самостоятельно решаю задачи и пишу код.
EOF

# Работа 3 — другой текст
cat > files/work3.txt << 'EOF'
Математический анализ и линейная алгебра являются фундаментальными 
дисциплинами для программистов. В этой работе рассматриваются основы.
EOF
```

### 2.1. Отправляем работу 1 на проверку

```bash
curl -X POST "http://localhost:8080/api/works/submit" \
  -F "file=@files/work1.txt" \
  -F "studentName=Иванов Иван" \
  -F "assignmentId=1"
```

В ответ придёт JSON с информацией о сдаче. Примерно так:

```json
{
  "submissionId": 1,
  "studentName": "Иванов Иван",
  "assignmentId": 1,
  "fileName": "work1.txt",
  "submittedAt": "2025-12-07T10:59:25.973677587",
  "report": {
    "id": 1,
    "submissionId": 1,
    "assignmentId": 1,
    "studentName": "Иванов Иван",
    "status": "COMPLETED",
    "isPlagiarism": false,
    "similarityPercentage": 0.0,
    "similarToSubmissionId": null,
    "similarToStudentName": null,
    "details": "Плагиат не обнаружен. Порог схожести: 50.0%",
    "analyzedAt": "2025-12-07T10:59:26.869897625"
  }
}
```

### 2.2. Отправляем работу 2 (почти копия) — ожидаем плагиат

```bash
curl -X POST "http://localhost:8080/api/works/submit" \
  -F "file=@files/work2.txt" \
  -F "studentName=Петров Пётр" \
  -F "assignmentId=1"
```

Теперь в ответе в отчёте должен появиться флаг плагиата и высокая схожесть, потому что текст очень похож на `work1.txt`:

```json
{
  "submissionId": 2,
  "studentName": "Петров Пётр",
  "assignmentId": 1,
  "fileName": "work2.txt",
  "submittedAt": "2025-12-07T11:01:50.017079871",
  "report": {
    "id": 2,
    "submissionId": 2,
    "assignmentId": 1,
    "studentName": "Петров Пётр",
    "status": "COMPLETED",
    "isPlagiarism": true,
    "similarityPercentage": 84.61538461538461,
    "similarToSubmissionId": 1,
    "similarToStudentName": "Иванов Иван",
    "details": "Уникальных слов в работе 1: 13, в работе 2: 11. Совпадающих слов: 11. Схожесть: 84.62%",
    "analyzedAt": "2025-12-07T11:01:50.065380076"
  }
}
```

Здесь видно, что:

- эта работа сочтена плагиатом (`"isPlagiarism": true`),
- она похожа на работу с `submissionId = 1`.

### 2.3. Отправляем работу 3 — другой текст

```bash
curl -X POST "http://localhost:8080/api/works/submit" \
  -F "file=@files/work3.txt" \
  -F "studentName=Сидоров Сидор" \
  -F "assignmentId=1"
```

```json
{
  "submissionId": 2,
  "studentName": "Петров Пётр",
  "assignmentId": 1,
  "fileName": "work2.txt",
  "submittedAt": "2025-12-07T11:01:50.017079871",
  "report": {
    "id": 2,
    "submissionId": 2,
    "assignmentId": 1,
    "studentName": "Петров Пётр",
    "status": "COMPLETED",
    "isPlagiarism": true,
    "similarityPercentage": 84.61538461538461,
    "similarToSubmissionId": 1,
    "similarToStudentName": "Иванов Иван",
    "details": "Уникальных слов в работе 1: 13, в работе 2: 11. Совпадающих слов: 11. Схожесть: 84.62%",
    "analyzedAt": "2025-12-07T11:01:50.065380076"
  }
}
```

---

## 3. Просмотр сдач и отчётов

### 3.1. Посмотреть все сдачи

```bash
curl "http://localhost:8080/api/submissions"
```

Пример ответа:

```json
[
  {
    "id": 1,
    "studentName": "Иванов Иван",
    "assignmentId": 1,
    "fileName": "work1.txt",
    "submittedAt": "2025-12-07T10:59:25.973678"
  },
  {
    "id": 2,
    "studentName": "Петров Пётр",
    "assignmentId": 1,
    "fileName": "work2.txt",
    "submittedAt": "2025-12-07T11:01:50.01708"
  }
]
```

### 3.2. Посмотреть все отчёты

```bash
curl "http://localhost:8080/api/reports"
```

Пример ответа (сильно сокращён):

```json
[
  {
    "id": 1,
    "submissionId": 1,
    "studentName": "Иванов Иван",
    "assignmentId": 1,
    "isPlagiarism": false,
    "similarityPercentage": 0.0
  },
  {
    "id": 2,
    "submissionId": 2,
    "studentName": "Петров Пётр",
    "assignmentId": 1,
    "isPlagiarism": true,
    "similarToSubmissionId": 1,
    "similarToStudentName": "Иванов Иван",
    "similarityPercentage": 70.5
  },
  {
    "id": 3,
    "submissionId": 3,
    "studentName": "Сидоров Сидор",
    "assignmentId": 1,
    "isPlagiarism": false,
    "similarityPercentage": 10.2
  }
]
```

### 3.3. Отчёты по конкретному заданию

Например, все отчёты по заданию `assignmentId = 1`:

```bash
curl "http://localhost:8080/api/works/1/reports"
```

### 3.4. Отчёт по конкретной сдаче

Допустим, у тебя есть сдача с `id = 2` (работа Петрова):

```bash
curl "http://localhost:8080/api/submissions/2/report"
```

---

## 4. Облако слов для работы

Для любой сдачи можно получить облако слов по её тексту.

Например, для сдачи с `id = 1`:

```bash
curl "http://localhost:8080/api/submissions/1/wordcloud"
```

---

## 5. Алгоритм проверки на плагиат (кратко)

Под капотом File Analysis Service использует коэффициент Жаккара:

1. Приводит текст к нижнему регистру.
2. Удаляет пунктуацию.
3. Делит текст на слова.
4. Выкидывает слишком короткие слова (например, короче 3 символов).
5. Представляет каждую работу как множество слов.
6. Для новой работы считает схожесть с каждой уже сохранённой работой:
    - `similarity = |A ∩ B| / |A ∪ B| * 100%`.
7. Если схожесть с любой ранее сданной работой ≥ 50 %, работа помечается как плагиат.

В отчёте сохраняются:

- флаг `isPlagiarism`;
- процент схожести;
- с какой работой схожесть максимальна (id и имя студента).

---

## 6. Swagger


- `http://localhost:8080/swagger-ui.html` — все внешние endpoint’ы (через Gateway).

Там можно отправлять работы, просматривать сдачи, отчёты и облако слов через веб-интерфейс.

---

## 7. Технологии (для отчёта)

- Java 21
- Spring Boot 3.4.2
- Spring Data JPA
- PostgreSQL 15 (две БД: для файлов и для отчётов)
- Docker и Docker Compose
- Swagger / OpenAPI 3
- Lombok
- Gradle 8.11
