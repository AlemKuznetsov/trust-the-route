#!/bin/bash
# Быстрый перезапуск приложения

cd ~/trust-the-route-backend/backend

echo "=== Быстрый перезапуск ==="
echo ""

# Проверяем, используется ли systemd
if systemctl is-active --quiet trust-the-route-backend 2>/dev/null; then
    echo "Используется systemd service"
    echo "Перезапуск через systemd..."
    sudo systemctl restart trust-the-route-backend
    sleep 3
    sudo systemctl status trust-the-route-backend --no-pager | head -10
else
    echo "Используется ручной запуск"
    
    # Останавливаем старое приложение
    pkill -f "gradlew run" 2>/dev/null
    sleep 2
    
    # Загружаем переменные окружения
    if [ -f .env ]; then
        source .env
    else
        echo "❌ Файл .env не найден!"
        exit 1
    fi
    
    # Запускаем в фоне
    echo "Запуск приложения..."
    nohup ./gradlew run --no-daemon > app.log 2>&1 &
    APP_PID=$!
    
    sleep 5
    
    if ps -p $APP_PID > /dev/null 2>&1; then
        echo "✅ Приложение запущено (PID: $APP_PID)"
        echo ""
        echo "Логи:"
        tail -10 app.log
    else
        echo "❌ Приложение не запустилось"
        echo "Проверьте логи: tail -50 app.log"
    fi
fi

echo ""
echo "Проверка API:"
sleep 2
curl -s http://localhost:8080/api/v1/auth/register -X POST \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"test123","name":"Test"}' 2>&1 | head -c 200
echo ""
