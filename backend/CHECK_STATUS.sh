#!/bin/bash
# Проверка статуса приложения и тестирование API

cd ~/trust-the-route-backend/backend

echo "=== Проверка статуса приложения ==="
echo ""

# Проверяем, запущен ли процесс
if pgrep -f "gradlew run" > /dev/null; then
    PID=$(pgrep -f "gradlew run")
    echo "✅ Приложение запущено (PID: $PID)"
else
    echo "❌ Приложение не запущено"
    echo "Проверьте логи: tail -50 app.log"
    exit 1
fi

echo ""
echo "=== Последние строки лога ==="
tail -20 app.log

echo ""
echo "=== Проверка доступности API ==="
sleep 2

# Проверяем, отвечает ли сервер
if curl -s http://localhost:8080 > /dev/null 2>&1; then
    echo "✅ Сервер отвечает на порту 8080"
else
    echo "⚠️  Сервер не отвечает. Проверьте логи: tail -f app.log"
    exit 1
fi

echo ""
echo "=== Тестирование API endpoints ==="
echo ""

# Тест регистрации
echo "1. Тест регистрации пользователя:"
REGISTER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123456","name":"Test User"}')

if echo "$REGISTER_RESPONSE" | grep -q "token"; then
    echo "   ✅ Регистрация работает"
    echo "   Ответ: $REGISTER_RESPONSE" | head -c 200
    echo ""
else
    echo "   ⚠️  Регистрация вернула: $REGISTER_RESPONSE"
fi

echo ""
echo "2. Тест входа (если пользователь уже существует):"
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123456"}')

if echo "$LOGIN_RESPONSE" | grep -q "token"; then
    echo "   ✅ Вход работает"
    echo "   Ответ: $LOGIN_RESPONSE" | head -c 200
    echo ""
else
    echo "   ⚠️  Вход вернул: $LOGIN_RESPONSE"
fi

echo ""
echo "=== Информация ==="
echo "Приложение доступно на:"
echo "  - Локально: http://localhost:8080"
echo "  - Извне: http://$(curl -s ifconfig.me):8080"
echo ""
echo "Полезные команды:"
echo "  - Просмотр логов: tail -f app.log"
echo "  - Остановка: pkill -f 'gradlew run'"
echo "  - Перезапуск: ./START_BACKGROUND.sh"
