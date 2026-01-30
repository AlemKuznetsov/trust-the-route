# Trust The Route Backend API

Backend API для мобильного приложения Trust The Route, построенный на Ktor.

## Технологии

- Kotlin
- Ktor 2.3.6
- PostgreSQL
- Exposed (ORM)
- JWT аутентификация
- BCrypt для хеширования паролей

## Требования

- Java JDK 17 или выше
- PostgreSQL 12 или выше
- Gradle 7.6 или выше

## Установка

1. Клонировать репозиторий или создать проект
2. Настроить базу данных (см. `POSTGRESQL_INSTALLATION_FIX.md`)
3. Настроить переменные окружения:
   ```bash
   export DB_PASSWORD="ваш_пароль_от_базы_данных"
   export JWT_SECRET="ваш_секретный_ключ_минимум_32_символа"
   ```
4. Запустить приложение:
   ```bash
   ./gradlew run
   ```

## API Endpoints

### Регистрация
```
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "name": "User Name"
}
```

### Вход
```
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

### Сброс пароля
```
POST /api/v1/auth/reset-password
Content-Type: application/json

{
  "email": "user@example.com"
}
```

## Развертывание

1. Собрать JAR файл:
   ```bash
   ./gradlew build
   ```

2. Запустить на сервере:
   ```bash
   java -jar build/libs/trust-the-route-backend-1.0.0.jar
   ```

3. Настроить nginx для проксирования (опционально)

4. Настроить SSL сертификат (Let's Encrypt)

## Переменные окружения

- `DB_PASSWORD` - пароль от базы данных PostgreSQL
- `JWT_SECRET` - секретный ключ для JWT токенов (минимум 32 символа)
