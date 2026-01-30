# Проверка работы сервиса после успешной сборки

## 1. Проверка статуса сервиса

```bash
sudo systemctl status trust-the-route-backend
```

Должен быть статус: `active (running)`

## 2. Проверка логов

```bash
# Последние 50 строк логов
sudo journalctl -u trust-the-route-backend -n 50 --no-pager

# Логи в реальном времени
sudo journalctl -u trust-the-route-backend -f
```

Не должно быть ошибок компиляции или "Unresolved reference".

## 3. Проверка работы API

### Тест регистрации пользователя:
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "test123456",
    "name": "Test User"
  }'
```

Ожидаемый ответ: `{"user": {...}, "token": "..."}` или `{"error": "..."}`

### Тест логина:
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "test123456"
  }'
```

### Проверка обработки ошибок:
```bash
# Должен вернуть ErrorResponse
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "",
    "password": "",
    "name": ""
  }'
```

Ожидаемый ответ: `{"error": "Email, password, and name are required"}`

## 4. Проверка порта

```bash
sudo ss -tlnp | grep 8080
```

Должен показать, что порт 8080 прослушивается Java процессом.

## 5. Если что-то не работает

### Перезапуск сервиса:
```bash
sudo systemctl restart trust-the-route-backend
sudo systemctl status trust-the-route-backend
```

### Просмотр подробных логов:
```bash
sudo journalctl -u trust-the-route-backend -n 100 --no-pager | grep -i error
```

### Проверка конфигурации:
```bash
cat /etc/systemd/system/trust-the-route-backend.service
```
