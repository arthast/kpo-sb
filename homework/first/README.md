# Московский зоопарк — консольное приложение

Цель: продемонстрировать доменную модель с принципами SOLID и DI-контейнером, позволяющую:
- принимать животных и вещи на баланс;
- проверять здоровье животного в ветклинике перед приёмкой;
- считать суммарное потребление еды всеми животными за день;
- формировать список животных, доступных для контактного зоопарка (травоядные с добротой > 5);
- печатать инвентаризационный отчёт (наименование + номер) по всем инвентаризируемым сущностям.

## Архитектура и доменная модель

Пакеты:
- `org.example.domain` — общие интерфейсы `IAlive`, `IInventory`, `INamed`;
- `org.example.domain.animal` — иерархия животных: `Animal` (база), `Herbo` (травоядные, с полем доброты),
  `Predator` (хищники), конкретные: `Monkey`, `Rabbit`, `Tiger`, `Wolf`;
- `org.example.domain.thing` — вещи: `Thing` (база), `Table`, `Computer`;
- `org.example.service` — интерфейсы сервисов: `IVetClinic`, `IZooService`;
- `org.example.service.impl` — реализации: `VetClinicImpl`, `ZooServiceImpl`;
- `org.example.di` — DI-модуль `AppModule` (Guice);
- `org.example.Main` — простое CLI-меню.

Интерфейсы:
- `IAlive` — «живые» сущности: `getFoodKgPerDay()`.
- `IInventory` — инвентаризируемые сущности: `getNumber()`.
- `INamed` — имя для печати в отчётах: `getName()`.

Сервисы:
- `IVetClinic.isHealthy(Animal)` — проверка здоровья.
- `IZooService` — приёмка животных (через ветклинику), учёт вещей, отчёты и выборка «контактных» животных.

## Применение SOLID
- S (Single Responsibility): каждая сущность отвечает за свою зону (модель, сервис, DI, CLI).
- O (Open/Closed): легко добавить новое животное/вещь, не меняя существующую логику сервиса.
- L (Liskov): `Herbo` и `Predator` корректно подставимы вместо `Animal`.
- I (Interface Segregation): мелкие интерфейсы `IAlive`, `IInventory`, `INamed` без избыточных обязанностей.
- D (Dependency Inversion): сервисы программируют через интерфейсы; конкретики внедряются DI-контейнером (Guice).

## DI-контейнер (Guice)
Конфигурация в `org.example.di.AppModule`:
```java
bind(IVetClinic.class).to(VetClinicImpl.class);
bind(IZooService.class).to(ZooServiceImpl.class);
```
В `Main` создаётся инжектор и извлекается `IZooService`.

## Требования и запуск
- Требуется установленный JDK 21 (Gradle toolchain настроен на Java 21).
- Сборка управляется Gradle Wrapper, ничего дополнительно ставить не нужно.

Команды (Windows cmd.exe):
```bat
cd D:\JavaProjects\kpo-sb\homework\first
gradlew.bat clean test jacocoTestReport
gradlew.bat run
```

После запуска появится меню:
1) Добавить животное
2) Добавить вещь
3) Отчет: количество животных и суммарная еда/день
4) Список животных для контактного зоопарка
5) Отчет по инвентарю (имя + номер)
0) Выход

Подсказки:
- Доброта для травоядных: 0..10. Для попадания в контактный зоопарк — строго > 5.
- Животное принимается на баланс только если ветклиника вернула «здоров».

## Тесты и покрытие
Запуск тестов и отчётов:
```bat
gradlew.bat test jacocoTestReport
```
Отчёты:
- Юнит-тесты (HTML): `build/reports/tests/test/index.html`
- Покрытие Jacoco (HTML): `build/reports/jacoco/test/html/index.html`

В проекте есть тесты для основных сценариев (`ZooServiceImplTest`, `VetClinicImplTest`, `HerboTest`).

## Замечания по расширяемости
- Новые виды животных/вещей — добавляются как новые классы, сервисы менять не нужно.
- Правила приёмки (ветклиника) можно варьировать, внедряя другую реализацию `IVetClinic` через DI.
- В дальнейшем можно вынести хранение в репозитории/БД, оставив интерфейсы в `service`.
