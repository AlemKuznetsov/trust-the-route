# Backend API работает! ✅

## Ошибка "User with this email already exists" - это нормально!

Это означает, что:
- ✅ Backend доступен извне
- ✅ API отвечает корректно
- ✅ База данных работает
- ✅ Пользователь уже существует (создан ранее)

## Тестирование:

### 1. Вход с существующим пользователем:

```powershell
$body = @{
    email = "test@example.com"
    password = "test123"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://158.160.217.181:8080/api/v1/auth/login" `
    -Method Post `
    -ContentType "application/json" `
    -Body $body
```

### 2. Регистрация нового пользователя (с другим email):

```powershell
$body = @{
    email = "newuser@example.com"
    password = "test123"
    name = "New User"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://158.160.217.181:8080/api/v1/auth/register" `
    -Method Post `
    -ContentType "application/json" `
    -Body $body
```

### 3. Тест сброса пароля:

```powershell
$body = @{
    email = "test@example.com"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://158.160.217.181:8080/api/v1/auth/reset-password" `
    -Method Post `
    -ContentType "application/json" `
    -Body $body
```

## Ожидаемый результат при входе:

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

## Следующий шаг:

Android приложение уже настроено с правильным IP адресом (`158.160.217.181:8080`), поэтому оно должно работать с backend!
