# Быстрое исправление дубликатов на сервере

## Проблема
Ошибка компиляции: `Redeclaration: ErrorResponse` и `Redeclaration: MessageResponse`
- Определены в `ApiResponses.kt` (правильно)
- Определены в `User.kt` (дубликат - нужно удалить)

## Решение (1 команда на сервере)

**Подключитесь к серверу и выполните:**

```bash
cd ~/trust-the-route-backend/backend && chmod +x FIX_DUPLICATES_SIMPLE.sh && ./FIX_DUPLICATES_SIMPLE.sh && ./gradlew clean build --no-daemon && sudo systemctl restart trust-the-route-backend
```

Или по шагам:

```bash
# 1. Перейти в директорию проекта
cd ~/trust-the-route-backend/backend

# 2. Загрузить скрипт исправления (если его нет)
# (скопируйте содержимое FIX_DUPLICATES_SIMPLE.sh на сервер)

# 3. Выполнить исправление
chmod +x FIX_DUPLICATES_SIMPLE.sh
./FIX_DUPLICATES_SIMPLE.sh

# 4. Пересобрать проект
./gradlew clean build --no-daemon

# 5. Перезапустить сервис
sudo systemctl restart trust-the-route-backend

# 6. Проверить статус
sudo systemctl status trust-the-route-backend
```

## Что делает скрипт FIX_DUPLICATES_SIMPLE.sh

1. Перезаписывает `User.kt` правильной версией (без `ErrorResponse` и `MessageResponse`)
2. Проверяет наличие `ApiResponses.kt` (создает, если отсутствует)
3. Проверяет, что классы определены только один раз

## Альтернатива: Исправить вручную

Если скрипт не работает, отредактируйте `User.kt` на сервере:

```bash
nano ~/trust-the-route-backend/backend/src/main/kotlin/com/trusttheroute/backend/models/User.kt
```

Удалите строки после `AuthResponse` (обычно строки 75-80), оставив файл заканчиваться на:

```kotlin
@Serializable
data class AuthResponse(
    val user: User,
    val token: String
)
```

Сохраните файл (Ctrl+O, Enter, Ctrl+X) и пересоберите проект.
