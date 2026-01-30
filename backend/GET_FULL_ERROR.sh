#!/bin/bash
# Получение полной ошибки приложения

cd ~/trust-the-route-backend/backend

echo "=== 1. Останавливаем service ==="
sudo systemctl stop trust-the-route-backend
sleep 2

echo ""
echo "=== 2. Проверяем порт 8080 (используем ss вместо netstat) ==="
sudo ss -tlnp | grep 8080 || echo "Порт 8080 свободен"

echo ""
echo "=== 3. Очищаем старые логи ==="
> app.log

echo ""
echo "=== 4. Запускаем вручную с полным стектрейсом ==="
source .env 2>/dev/null
export DB_PASSWORD="Bcnbyf293!"
export JWT_SECRET="8e9db1a9046ed6baa814234f01eb7c38f1c28199e9817b261507093278557cf9"

echo "Переменные окружения:"
echo "DB_PASSWORD: ${DB_PASSWORD:+установлен}"
echo "JWT_SECRET: ${JWT_SECRET:+установлен}"
echo ""

./gradlew run --no-daemon --stacktrace 2>&1 | tee /tmp/gradle_run.log

echo ""
echo "=== 5. Последние строки лога ==="
tail -50 app.log 2>/dev/null || echo "app.log пуст"
