# Тестирование доступа к Backend API

## Способ 1: С вашего компьютера (PowerShell)

### Проверка доступности порта:

```powershell
# Проверка порта
Test-NetConnection -ComputerName 158.160.217.181 -Port 8080
```

Если порт открыт, вы увидите:
- `TcpTestSucceeded : True`

### Тест API - Регистрация:

```powershell
# Регистрация нового пользователя
curl.exe -X POST http://158.160.217.181:8080/api/v1/auth/register `
  -H "Content-Type: application/json" `
  -d '{\"email\":\"test@example.com\",\"password\":\"test123\",\"name\":\"Test User\"}'
```

### Тест API - Вход:

```powershell
# Вход
curl.exe -X POST http://158.160.217.181:8080/api/v1/auth/login `
  -H "Content-Type: application/json" `
  -d '{\"email\":\"test@example.com\",\"password\":\"test123\"}'
```

## Способ 2: Через Cloud Shell (если нужно подключиться обратно)

1. **Откройте Cloud Shell** в консоли Yandex Cloud (кнопка в правом верхнем углу)

2. **Подключитесь к VM:**
   ```bash
   ssh ubuntu@158.160.217.181
   ```

3. **Проверьте статус backend:**
   ```bash
   sudo systemctl status trust-the-route-backend --no-pager | head -15
   ```

4. **Проверьте порт:**
   ```bash
   sudo ss -tlnp | grep 8080
   ```

5. **Тест API:**
   ```bash
   curl -X POST http://localhost:8080/api/v1/auth/register \
     -H "Content-Type: application/json" \
     -d '{"email":"test2@example.com","password":"test123","name":"Test User 2"}'
   ```

## Ожидаемый результат:

При успешной регистрации вы должны получить JSON ответ:
```json
{
  "user": {
    "id": "...",
    "email": "test@example.com",
    "name": "Test User"
  },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

## Если порт не доступен:

1. Проверьте, что правило в Security Group сохранено правильно
2. Убедитесь, что backend запущен: `sudo systemctl status trust-the-route-backend`
3. Проверьте логи: `tail -50 ~/trust-the-route-backend/backend/app.log`
