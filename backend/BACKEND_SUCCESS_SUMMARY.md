# Backend успешно запущен! ✅

## Статус:
- ✅ Service запущен и работает (`active (running)`)
- ✅ Порт 8080 слушается
- ✅ API отвечает на запросы
- ✅ Регистрация и авторизация работают
- ✅ JWT токены генерируются

## Что дальше:

### 1. Откройте порт 8080 для внешнего доступа

В консоли Yandex Cloud:
1. Перейдите в **VPC** → **Security Groups**
2. Найдите Security Group вашей VM
3. Добавьте правило:
   - **Тип**: Custom TCP
   - **Порт**: 8080
   - **Протокол**: TCP
   - **Источник**: 0.0.0.0/0 (или ваш IP для безопасности)
   - **Описание**: Backend API

### 2. Обновите Android приложение

В файле `app/src/main/java/com/trusttheroute/app/di/NetworkModule.kt`:
```kotlin
private const val BASE_URL = "http://158.160.180.232:8080/api/v1/"
```

### 3. Полезные команды для управления backend:

```bash
# Статус service
sudo systemctl status trust-the-route-backend

# Остановить
sudo systemctl stop trust-the-route-backend

# Запустить
sudo systemctl start trust-the-route-backend

# Перезапустить
sudo systemctl restart trust-the-route-backend

# Логи в реальном времени
sudo journalctl -u trust-the-route-backend -f

# Или логи из файла
tail -f ~/trust-the-route-backend/backend/app.log
```

### 4. Тестирование API извне (после открытия порта):

```bash
# Регистрация
curl -X POST http://158.160.180.232:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123","name":"Test User"}'

# Вход
curl -X POST http://158.160.180.232:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123"}'
```

## Текущие endpoints:

- `POST /api/v1/auth/register` - Регистрация
- `POST /api/v1/auth/login` - Вход
- `POST /api/v1/auth/reset-password` - Сброс пароля

## Переменные окружения:

- `DB_PASSWORD` - Пароль PostgreSQL
- `JWT_SECRET` - Секретный ключ для JWT

## База данных:

- **База**: `trust_the_route`
- **Пользователь**: `trust_user`
- **Хост**: `localhost:5432`
