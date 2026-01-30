#!/bin/bash
# Проверка доступности сервера извне

cd ~/trust-the-route-backend/backend

echo "=== Проверка доступности сервера ==="
echo ""

# 1. Проверка, запущено ли приложение
echo "1. Проверка процесса:"
if pgrep -f "gradlew run" > /dev/null; then
    PID=$(pgrep -f "gradlew run")
    echo "   ✅ Приложение запущено (PID: $PID)"
else
    echo "   ❌ Приложение НЕ запущено!"
    echo "   Запустите: source .env && nohup ./gradlew run --no-daemon > app.log 2>&1 &"
    exit 1
fi

# 2. Проверка, слушает ли порт 8080
echo ""
echo "2. Проверка порта 8080:"
if sudo netstat -tlnp 2>/dev/null | grep -q ":8080" || sudo ss -tlnp 2>/dev/null | grep -q ":8080"; then
    echo "   ✅ Порт 8080 слушается"
    echo "   Детали:"
    sudo netstat -tlnp 2>/dev/null | grep ":8080" || sudo ss -tlnp 2>/dev/null | grep ":8080"
else
    echo "   ❌ Порт 8080 НЕ слушается!"
    echo "   Проверьте логи: tail -50 app.log"
fi

# 3. Проверка firewall
echo ""
echo "3. Проверка firewall (ufw):"
if command -v ufw &> /dev/null; then
    UFW_STATUS=$(sudo ufw status | head -1)
    echo "   Статус: $UFW_STATUS"
    if sudo ufw status | grep -q "8080"; then
        echo "   ✅ Порт 8080 открыт в firewall"
        sudo ufw status | grep "8080"
    else
        echo "   ⚠️  Порт 8080 не найден в правилах firewall"
        echo "   Откройте порт: sudo ufw allow 8080/tcp"
    fi
else
    echo "   ⚠️  ufw не установлен"
fi

# 4. Проверка локального подключения
echo ""
echo "4. Проверка локального подключения:"
if curl -s http://localhost:8080 > /dev/null 2>&1; then
    echo "   ✅ Локальное подключение работает"
else
    echo "   ❌ Локальное подключение НЕ работает"
    echo "   Проверьте логи: tail -50 app.log"
fi

# 5. Получение публичного IP
echo ""
echo "5. Публичный IP сервера:"
PUBLIC_IP=$(curl -s ifconfig.me 2>/dev/null || curl -s icanhazip.com 2>/dev/null || echo "не определен")
echo "   IP: $PUBLIC_IP"

# 6. Проверка извне (если возможно)
echo ""
echo "6. Рекомендации:"
echo "   - Убедитесь, что в Yandex Cloud Security Groups открыт порт 8080"
echo "   - Проверьте, что приложение слушает на 0.0.0.0:8080, а не только localhost"
echo "   - Для эмулятора Android используйте реальное устройство или настройте VPN"
echo ""
echo "7. Тест API локально:"
curl -s http://localhost:8080/api/v1/auth/register -X POST \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"test123","name":"Test"}' 2>&1 | head -c 200
echo ""
