# Техническая документация

```text
Документация по используемым технологиям, примененных при разработке социального проекта для некоммерческой организации Добрый Шкаф
```

Монорепозиторий для проекта DobriyShkafApp: настольный клиент на Kotlin и backend на Java/Spring живут в отдельных Gradle-модулях.

## Модули

- `frontend` - настольное приложение на Kotlin с MVI-архитектурой.
- `backend` - backend на Java/Spring с REST API для работы с семьями.

## Требования

- JDK 17.
- Gradle wrapper из репозитория: `gradlew` или `gradlew.bat`.

## Команды

Посмотреть все доступные Gradle-задачи:

```bash
./gradlew tasks --all
```

Запуск frontend:

```bash
./gradlew :frontend:run
```

Сборка frontend:

```bash
./gradlew :frontend:build
```

Сборка backend:

```bash
./gradlew :backend:build
```

Сборка всего проекта:

```bash
./gradlew build
```

## Работа с ветками

Основная интеграционная ветка: `main`.

После завершения задачи в `frontend/` или `backend/` изменения объединяются в `main` через PR или merge.

## Настройка адреса backend

Фронтенд по умолчанию обращается к:

```text
http://185.204.0.88:8082/api/families-admin
```

Адрес можно переопределить системным свойством:

```bash
./gradlew :frontend:run -Ddatabase.api.base=http://host:port/api/families-admin
```

Или переменной окружения:

```bash
DATABASE_API_BASE=http://host:port/api/families-admin ./gradlew :frontend:run
```