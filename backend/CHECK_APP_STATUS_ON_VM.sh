#!/bin/bash
# Проверка статуса приложения на VM

cd ~/trust-the-route-backend/backend

echo "=== 1. Проверка процессов Java ==="
ps aux | grep java | grep -v grep

echo ""
echo "=== 2. Проверка порта 8080 ==="
sudo ss -tlnp | grep 8080 || echo "Порт 8080 не слушается"

echo ""
echo "=== 3. Статус systemd service ==="
sudo systemctl status trust-the-route-backend --no-pager | head -20

echo ""
echo "=== 4. Последние логи приложения (последние 50 строк) ==="
tail -50 app.log 2>/dev/null || echo "app.log пуст или не найден"

echo ""
echo "=== 5. Поиск сообщений о запуске Ktor сервера ==="
if [ -f app.log ]; then
    echo "Ищем сообщения о запуске:"
    grep -i "started\|ktor\|server\|port\|listening\|bind" app.log | tail -10 || echo "Не найдено сообщений о запуске сервера"
else
    echo "Файл app.log не найден"
fi

echo ""
echo "=== 6. Проверка переменных окружения в service ==="
sudo systemctl show trust-the-route-backend | grep -E "Environment|DB_PASSWORD|JWT_SECRET"
