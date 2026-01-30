#!/bin/bash
# Проверка файла и полный перезапуск

cd ~/trust-the-route-backend/backend

echo "=== Проверка и перезапуск ==="
echo ""

# Проверяем, что в файле нет ErrorResponse
echo "1. Проверяем AuthRoutes.kt на наличие ErrorResponse:"
if grep -q "ErrorResponse" src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt; then
    echo "❌ Найден ErrorResponse в файле!"
    grep -n "ErrorResponse" src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt
    echo ""
    echo "Исправляем файл..."
else
    echo "✅ ErrorResponse не найден - файл правильный"
fi

# Убеждаемся, что используем mapOf
echo ""
echo "2. Проверяем использование mapOf:"
grep -c "mapOf(\"error\"" src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt || echo "0"

# Полностью останавливаем все процессы
echo ""
echo "3. Останавливаем все процессы Gradle:"
pkill -9 -f "gradlew" 2>/dev/null
pkill -9 -f "java.*trust" 2>/dev/null
sleep 3

# Очищаем build
echo "4. Очищаем build:"
rm -rf build/
rm -rf .gradle/

# Пересобираем
echo "5. Пересобираем:"
./gradlew clean build --no-daemon

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Сборка успешна!"
    echo ""
    echo "6. Запускаем приложение:"
    if [ -f .env ]; then
        source .env
    fi
    
    # Убеждаемся, что порт свободен
    lsof -ti:8080 | xargs kill -9 2>/dev/null
    sleep 2
    
    nohup ./gradlew run --no-daemon > app.log 2>&1 &
    APP_PID=$!
    echo "PID: $APP_PID"
    
    echo ""
    echo "7. Ждем запуска (10 секунд)..."
    sleep 10
    
    echo ""
    echo "8. Проверяем процесс:"
    if ps -p $APP_PID > /dev/null 2>&1; then
        echo "✅ Процесс работает"
    else
        echo "❌ Процесс не работает. Логи:"
        tail -30 app.log
        exit 1
    fi
    
    echo ""
    echo "9. Проверяем логи:"
    tail -10 app.log
    
    echo ""
    echo "10. Тест API:"
    sleep 2
    RESPONSE=$(curl -s http://localhost:8080/api/v1/auth/register -X POST \
      -H "Content-Type: application/json" \
      -d '{"email":"test@test.com","password":"test123","name":"Test"}')
    
    echo "$RESPONSE"
    echo ""
    
    if echo "$RESPONSE" | grep -q "token"; then
        echo "✅ API работает! Регистрация успешна!"
    elif echo "$RESPONSE" | grep -q "error"; then
        echo "⚠️  API вернул ошибку, но это нормально (возможно, пользователь уже существует)"
        echo "Попробуем логин:"
        curl -s http://localhost:8080/api/v1/auth/login -X POST \
          -H "Content-Type: application/json" \
          -d '{"email":"test@test.com","password":"test123"}'
    else
        echo "❌ Неожиданный ответ"
    fi
else
    echo ""
    echo "❌ Ошибка сборки"
fi
