# Развертывание обновлений на сервер

## Проблема
Backend возвращает 404 для новых endpoints, потому что файлы на сервере не обновлены.

## Решение: Загрузить обновленные файлы на сервер

### Вариант 1: Через SCP (если у вас есть SSH доступ)

**На вашем компьютере (PowerShell):**

```powershell
# 1. Перейдите в папку проекта
cd "C:\Trust The Route"

# 2. Загрузите обновленные файлы на сервер
scp -i C:\Users\kuzne\.ssh\ssh-key-1769657037850 backend/src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt ubuntu@158.160.217.181:~/trust-the-route-backend/backend/src/main/kotlin/com/trusttheroute/backend/routes/auth/

scp -i C:\Users\kuzne\.ssh\ssh-key-1769657037850 backend/src/main/kotlin/com/trusttheroute/backend/models/User.kt ubuntu@158.160.217.181:~/trust-the-route-backend/backend/src/main/kotlin/com/trusttheroute/backend/models/

scp -i C:\Users\kuzne\.ssh\ssh-key-1769657037850 backend/src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt ubuntu@158.160.217.181:~/trust-the-route-backend/backend/src/main/kotlin/com/trusttheroute/backend/repositories/
```

### Вариант 2: Через Cloud Shell (если SCP не работает)

**В Cloud Shell:**

```bash
# 1. Подключитесь к VM
ssh ubuntu@158.160.217.181

# 2. Перейдите в папку проекта
cd ~/trust-the-route-backend/backend

# 3. Создайте файлы вручную или скопируйте содержимое
# (используйте nano или vi для редактирования)
```

### Вариант 3: Создать файлы напрямую на сервере

**Подключитесь к VM через SSH или Cloud Shell и выполните:**

```bash
cd ~/trust-the-route-backend/backend

# Создайте скрипт для обновления файлов
cat > UPDATE_FILES.sh << 'SCRIPT_EOF'
#!/bin/bash
# Этот скрипт нужно будет выполнить после загрузки файлов
cd ~/trust-the-route-backend/backend

echo "Остановка service..."
sudo systemctl stop trust-the-route-backend

echo "Очистка..."
./gradlew clean

echo "Пересборка..."
./gradlew build --no-daemon

echo "Запуск service..."
sudo systemctl start trust-the-route-backend

sleep 5

echo "Статус:"
sudo systemctl status trust-the-route-backend --no-pager | head -20
SCRIPT_EOF

chmod +x UPDATE_FILES.sh
```

## После загрузки файлов на сервер

**Выполните на сервере:**

```bash
cd ~/trust-the-route-backend/backend

# 1. Проверьте, что файлы обновлены
grep -q "post(\"/yandex\")" src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt && echo "✅ Endpoint /yandex найден" || echo "❌ Endpoint /yandex НЕ найден"

grep -q "fun createYandexUser" src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt && echo "✅ Метод createYandexUser найден" || echo "❌ Метод createYandexUser НЕ найден"

# 2. Остановите service
sudo systemctl stop trust-the-route-backend

# 3. Очистите старые процессы
sudo lsof -ti:8080 | xargs -r sudo kill -9 2>/dev/null
ps aux | grep -E "gradlew|java.*backend" | grep -v grep | awk '{print $2}' | xargs -r sudo kill -9 2>/dev/null

# 4. Очистите и пересоберите проект
./gradlew clean build --no-daemon

# 5. Запустите service
sudo systemctl start trust-the-route-backend

# 6. Проверьте статус
sleep 5
sudo systemctl status trust-the-route-backend

# 7. Проверьте endpoints
curl -X POST http://localhost:8080/api/v1/auth/yandex \
  -H "Content-Type: application/json" \
  -d '{"yandexToken":"test","email":"test@test.com","name":"Test"}' \
  -w "\nHTTP Code: %{http_code}\n"
```

## Быстрый способ: Использовать готовый скрипт

Загрузите файл `backend/CHECK_AND_REBUILD.sh` на сервер и выполните:

```bash
chmod +x CHECK_AND_REBUILD.sh
./CHECK_AND_REBUILD.sh
```
