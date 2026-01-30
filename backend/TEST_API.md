# Тестирование API после успешной сборки

## ✅ Результат проверки регистрации

Ответ `{"error":"User with this email already exists"}` означает, что:
- ✅ API работает корректно
- ✅ ErrorResponse сериализуется правильно
- ✅ Валидация работает
- ✅ База данных подключена

## Дополнительные тесты

### 1. Регистрация нового пользователя (с другим email)

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"newuser@test.com","password":"test123456","name":"New User"}'
```

Ожидаемый ответ: `{"user": {...}, "token": "..."}` со статусом 201

### 2. Тест логина

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"test123"}'
```

Ожидаемый ответ: `{"user": {...}, "token": "..."}` со статусом 200

### 3. Тест валидации (пустые поля)

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"","password":"","name":""}'
```

Ожидаемый ответ: `{"error":"Email, password, and name are required"}` со статусом 400

### 4. Тест с неверным паролем

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"wrongpassword"}'
```

Ожидаемый ответ: `{"error":"Invalid email or password"}` со статусом 401

### 5. Тест с полным выводом HTTP статуса

```bash
curl -v -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"another@test.com","password":"test123456","name":"Another User"}'
```

Флаг `-v` покажет HTTP статус код в заголовках ответа.

### 6. Тест MessageResponse (сброс пароля)

```bash
curl -X POST http://localhost:8080/api/v1/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com"}'
```

Ожидаемый ответ: `{"message":"If the email exists, a password reset link has been sent"}` со статусом 200

## Проверка всех endpoints

### Регистрация
```bash
POST /api/v1/auth/register
```

### Логин
```bash
POST /api/v1/auth/login
```

### Сброс пароля
```bash
POST /api/v1/auth/reset-password
```

### Yandex авторизация
```bash
POST /api/v1/auth/yandex
```

### Обновление профиля (требует токен)
```bash
PUT /api/v1/auth/profile
Authorization: Bearer <token>
```

### Смена пароля (требует токен)
```bash
PUT /api/v1/auth/password
Authorization: Bearer <token>
```

### Удаление аккаунта (требует токен)
```bash
DELETE /api/v1/auth/account?confirmation=УДАЛИТЬ
Authorization: Bearer <token>
```

## Успешные признаки работы API

✅ Все запросы возвращают JSON  
✅ ErrorResponse возвращает `{"error": "..."}`  
✅ MessageResponse возвращает `{"message": "..."}`  
✅ Правильные HTTP статус коды (200, 201, 400, 401, 409, 500)  
✅ Нет ошибок компиляции в логах  
✅ Нет "Unresolved reference" ошибок  

## Если нужно проверить логи

```bash
# Последние 50 строк
sudo journalctl -u trust-the-route-backend -n 50 --no-pager

# Только ошибки
sudo journalctl -u trust-the-route-backend -n 100 --no-pager | grep -i error

# Логи в реальном времени
sudo journalctl -u trust-the-route-backend -f
```
