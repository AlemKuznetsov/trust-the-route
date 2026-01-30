# Быстрая загрузка файлов на сервер

## Вариант 1: Через PowerShell (SCP)

**Откройте PowerShell на вашем компьютере и выполните:**

```powershell
cd "C:\Trust The Route"

# Загрузите файлы на сервер
scp -i C:\Users\kuzne\.ssh\ssh-key-1769657037850 backend\src\main\kotlin\com\trusttheroute\backend\routes\auth\AuthRoutes.kt ubuntu@158.160.217.181:~/trust-the-route-backend/backend/src/main/kotlin/com/trusttheroute/backend/routes/auth/

scp -i C:\Users\kuzne\.ssh\ssh-key-1769657037850 backend\src\main\kotlin\com\trusttheroute\backend\models\User.kt ubuntu@158.160.217.181:~/trust-the-route-backend/backend/src/main/kotlin/com/trusttheroute/backend/models/

scp -i C:\Users\kuzne\.ssh\ssh-key-1769657037850 backend\src\main\kotlin\com\trusttheroute\backend\repositories\UserRepository.kt ubuntu@158.160.217.181:~/trust-the-route-backend/backend/src/main/kotlin/com/trusttheroute/backend/repositories/
```

**Или используйте готовый скрипт:**

```powershell
cd "C:\Trust The Route"
.\backend\UPLOAD_FILES_TO_SERVER.ps1
```

## Вариант 2: Через Cloud Shell (если SCP не работает)

1. **Откройте Cloud Shell в Yandex Cloud**
2. **Подключитесь к VM:**
   ```bash
   ssh ubuntu@158.160.217.181
   ```
3. **Создайте файлы вручную** (скопируйте содержимое из локальных файлов)

## После загрузки файлов

**Выполните на сервере:**

```bash
cd ~/trust-the-route-backend/backend

# Проверьте файлы
grep -q 'post("/yandex")' src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt && echo "✅ OK" || echo "❌ НЕ НАЙДЕН"

# Остановите service
sudo systemctl stop trust-the-route-backend

# Очистите процессы
sudo lsof -ti:8080 | xargs -r sudo kill -9 2>/dev/null
ps aux | grep -E "gradlew|java" | grep -v grep | awk '{print $2}' | xargs -r sudo kill -9 2>/dev/null

# Пересоберите
./gradlew clean build --no-daemon

# Запустите
sudo systemctl start trust-the-route-backend

# Проверьте
sleep 5
sudo systemctl status trust-the-route-backend
```
