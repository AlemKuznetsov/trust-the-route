#!/bin/bash
# Детальная проверка логов

cd ~/trust-the-route-backend/backend

echo "=== 1. Статус service ==="
sudo systemctl status trust-the-route-backend --no-pager | head -15

echo ""
echo "=== 2. Последние логи через journalctl (50 строк) ==="
sudo journalctl -u trust-the-route-backend -n 50 --no-pager

echo ""
echo "=== 3. Логи из app.log (последние 50 строк) ==="
tail -50 app.log 2>/dev/null || echo "Файл app.log не найден или пуст"

echo ""
echo "=== 4. Проверка процесса Java ==="
ps aux | grep java | grep -v grep

echo ""
echo "=== 5. Проверка порта 8080 ==="
sudo netstat -tlnp | grep 8080 || echo "Порт 8080 не слушается"

echo ""
echo "=== 6. Попытка запуска с детальными логами ==="
echo "Останавливаем service для теста..."
sudo systemctl stop trust-the-route-backend

sleep 2

echo "Запускаем вручную с полными логами..."
source .env 2>/dev/null
./gradlew run --no-daemon --stacktrace 2>&1 | head -100
