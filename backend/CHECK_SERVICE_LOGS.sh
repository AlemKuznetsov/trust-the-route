#!/bin/bash
# Проверка логов systemd service

echo "=== Проверка логов service ==="
echo ""

# Проверяем логи через journalctl
echo "1. Последние логи через journalctl:"
sudo journalctl -u trust-the-route-backend -n 50 --no-pager

echo ""
echo "2. Логи из app.log:"
tail -50 ~/trust-the-route-backend/backend/app.log 2>/dev/null || echo "Файл app.log не найден"

echo ""
echo "3. Проверка переменных окружения:"
if [ -f ~/trust-the-route-backend/backend/.env ]; then
    echo "✅ Файл .env существует"
    source ~/trust-the-route-backend/backend/.env
    echo "DB_PASSWORD: ${DB_PASSWORD:+установлен}"
    echo "JWT_SECRET: ${JWT_SECRET:+установлен}"
else
    echo "❌ Файл .env не найден!"
fi

echo ""
echo "4. Проверка прав на gradlew:"
ls -la ~/trust-the-route-backend/backend/gradlew

echo ""
echo "5. Попытка запуска вручную для проверки ошибок:"
cd ~/trust-the-route-backend/backend
source .env 2>/dev/null
./gradlew run --no-daemon 2>&1 | head -30
