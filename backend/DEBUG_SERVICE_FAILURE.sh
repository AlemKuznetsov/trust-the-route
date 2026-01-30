#!/bin/bash
# Диагностика падения service

cd ~/trust-the-route-backend/backend

echo "=== 1. Полные логи service (последние 100 строк) ==="
sudo journalctl -u trust-the-route-backend -n 100 --no-pager

echo ""
echo "=== 2. Логи приложения (app.log) ==="
tail -100 app.log 2>/dev/null || echo "app.log пуст"

echo ""
echo "=== 3. Проверка порта 8080 ==="
sudo ss -tlnp | grep 8080 || echo "Порт 8080 не слушается"

echo ""
echo "=== 4. Попытка запуска вручную для просмотра ошибок ==="
echo "Останавливаем service..."
sudo systemctl stop trust-the-route-backend 2>/dev/null

sleep 2

echo "Запускаем вручную с переменными окружения..."
export DB_PASSWORD="Bcnbyf293!"
export JWT_SECRET="8e9db1a9046ed6baa814234f01eb7c38f1c28199e9817b261507093278557cf9"

echo "Переменные установлены:"
echo "DB_PASSWORD: ${DB_PASSWORD:+установлен}"
echo "JWT_SECRET: ${JWT_SECRET:+установлен}"
echo ""

echo "Запускаем приложение..."
timeout 30 ./gradlew run --no-daemon 2>&1 | head -150 || echo "Процесс завершился или превысил таймаут"
