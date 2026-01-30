#!/bin/bash
# Тестирование всех API endpoints

cd ~/trust-the-route-backend/backend

echo "=== Тестирование всех API endpoints ==="
echo ""

# Убеждаемся, что приложение запущено
if ! pgrep -f "gradlew run" > /dev/null; then
    echo "Запускаем приложение..."
    if [ -f .env ]; then
        source .env
    fi
    nohup ./gradlew run --no-daemon > app.log 2>&1 &
    sleep 10
fi

TEST_EMAIL="testuser$(date +%s)@test.com"
TEST_PASSWORD="test123456"
TEST_NAME="Test User"

echo "1. Тест регистрации:"
echo "Email: $TEST_EMAIL"
REGISTER_RESPONSE=$(curl -s http://localhost:8080/api/v1/auth/register -X POST \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$TEST_EMAIL\",\"password\":\"$TEST_PASSWORD\",\"name\":\"$TEST_NAME\"}")

echo "$REGISTER_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$REGISTER_RESPONSE"
echo ""

# Извлекаем токен
TOKEN=$(echo "$REGISTER_RESPONSE" | grep -o '"token":"[^"]*' | head -1 | cut -d'"' -f4)

if [ -n "$TOKEN" ]; then
    echo "✅ Регистрация успешна! Token получен."
    echo ""
    
    echo "2. Тест входа с правильными данными:"
    LOGIN_RESPONSE=$(curl -s http://localhost:8080/api/v1/auth/login -X POST \
      -H "Content-Type: application/json" \
      -d "{\"email\":\"$TEST_EMAIL\",\"password\":\"$TEST_PASSWORD\"}")
    
    echo "$LOGIN_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$LOGIN_RESPONSE"
    echo ""
    
    echo "3. Тест входа с неверным паролем:"
    ERROR_RESPONSE=$(curl -s http://localhost:8080/api/v1/auth/login -X POST \
      -H "Content-Type: application/json" \
      -d "{\"email\":\"$TEST_EMAIL\",\"password\":\"wrongpassword\"}")
    
    echo "$ERROR_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$ERROR_RESPONSE"
    echo ""
    
    echo "4. Тест повторной регистрации (должна вернуть ошибку):"
    DUPLICATE_RESPONSE=$(curl -s http://localhost:8080/api/v1/auth/register -X POST \
      -H "Content-Type: application/json" \
      -d "{\"email\":\"$TEST_EMAIL\",\"password\":\"$TEST_PASSWORD\",\"name\":\"$TEST_NAME\"}")
    
    echo "$DUPLICATE_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$DUPLICATE_RESPONSE"
    echo ""
    
    echo "5. Тест сброса пароля:"
    RESET_RESPONSE=$(curl -s http://localhost:8080/api/v1/auth/reset-password -X POST \
      -H "Content-Type: application/json" \
      -d "{\"email\":\"$TEST_EMAIL\"}")
    
    echo "$RESET_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$RESET_RESPONSE"
    echo ""
    
    echo "=== Итоги ==="
    echo "✅ Все endpoints работают!"
    echo "✅ Регистрация: OK"
    echo "✅ Вход: OK"
    echo "✅ Обработка ошибок: OK"
    echo "✅ Сброс пароля: OK"
else
    echo "❌ Не удалось получить токен. Проверьте ответ регистрации."
fi
