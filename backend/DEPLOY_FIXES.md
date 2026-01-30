# Инструкция по загрузке исправлений на сервер

## Что было исправлено:

1. ✅ Создан файл `ApiResponses.kt` с определениями `ErrorResponse` и `MessageResponse`
2. ✅ Добавлены импорты в `Application.kt` и `AuthRoutes.kt`
3. ✅ Заменен оставшийся `mapOf()` на `MessageResponse` в `AuthRoutes.kt`

## Шаг 1: Загрузить файлы на сервер

### Вариант A: Использовать готовый скрипт (рекомендуется)

**Откройте PowerShell и выполните:**

```powershell
cd "C:\Trust The Route"
.\backend\UPLOAD_FIXED_FILES.ps1
```

### Вариант B: Загрузить файлы вручную

**В PowerShell выполните команды:**

```powershell
cd "C:\Trust The Route"

$SSH_KEY = "C:\Users\kuzne\.ssh\ssh-key-1769657037850"
$SERVER = "ubuntu@158.160.217.181"

# 1. Загрузить новый файл ApiResponses.kt
scp -i $SSH_KEY backend\src\main\kotlin\com\trusttheroute\backend\models\ApiResponses.kt ${SERVER}:~/trust-the-route-backend/backend/src/main/kotlin/com/trusttheroute/backend/models/

# 2. Загрузить обновленный Application.kt
scp -i $SSH_KEY backend\src\main\kotlin\com\trusttheroute\backend\Application.kt ${SERVER}:~/trust-the-route-backend/backend/src/main/kotlin/com/trusttheroute/backend/

# 3. Загрузить обновленный AuthRoutes.kt
scp -i $SSH_KEY backend\src\main\kotlin\com\trusttheroute\backend\routes\auth\AuthRoutes.kt ${SERVER}:~/trust-the-route-backend/backend/src/main/kotlin/com/trusttheroute/backend/routes/auth/
```

## Шаг 2: Подключиться к серверу

```powershell
ssh -i C:\Users\kuzne\.ssh\ssh-key-1769657037850 ubuntu@158.160.217.181
```

## Шаг 2.5: Удалить дубликаты из User.kt (ВАЖНО!)

**На сервере выполните:**

```bash
cd ~/trust-the-route-backend/backend
chmod +x FIX_DUPLICATES_SIMPLE.sh
./FIX_DUPLICATES_SIMPLE.sh
```

Этот скрипт удалит дублирующиеся определения `ErrorResponse` и `MessageResponse` из `User.kt`, оставив их только в `ApiResponses.kt`.

## Шаг 3: Пересобрать проект на сервере

### Вариант A: Использовать готовый скрипт

```bash
cd ~/trust-the-route-backend/backend
chmod +x REBUILD_AND_RESTART.sh
./REBUILD_AND_RESTART.sh
```

### Вариант B: Выполнить команды вручную

```bash
cd ~/trust-the-route-backend/backend

# Остановить сервис
sudo systemctl stop trust-the-route-backend

# Очистить старые сборки
./gradlew clean

# Собрать проект
./gradlew build --no-daemon

# Если сборка успешна, перезапустить сервис
sudo systemctl start trust-the-route-backend

# Проверить статус
sudo systemctl status trust-the-route-backend

# Посмотреть логи (если есть ошибки)
sudo journalctl -u trust-the-route-backend -n 50 --no-pager
```

## Проверка успешности исправления

После пересборки проверьте:

1. **Статус сервиса:**
   ```bash
   sudo systemctl status trust-the-route-backend
   ```
   Должен быть `active (running)`

2. **Логи без ошибок компиляции:**
   ```bash
   sudo journalctl -u trust-the-route-backend -n 50 --no-pager
   ```
   Не должно быть ошибок типа "Unresolved reference" или "Redeclaration"

3. **Проверка работы API:**
   ```bash
   curl http://localhost:8080/api/v1/auth/register -X POST -H "Content-Type: application/json" -d '{"email":"test@test.com","password":"test123","name":"Test"}'
   ```

## Если возникли проблемы

1. **Ошибка "Unresolved reference":**
   - Проверьте, что файл `ApiResponses.kt` загружен
   - Проверьте импорты в `Application.kt` и `AuthRoutes.kt`

2. **Ошибка "Redeclaration":**
   - Убедитесь, что `ErrorResponse` и `MessageResponse` определены только в `ApiResponses.kt`
   - Проверьте, нет ли дубликатов в других файлах

3. **Ошибка сборки:**
   - Посмотрите полный вывод: `./gradlew build --stacktrace`
   - Проверьте версию Kotlin и Ktor в `build.gradle.kts`
