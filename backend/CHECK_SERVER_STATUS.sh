#!/bin/bash
# Проверка статуса сервера

cd ~/trust-the-route-backend/backend

echo "=== 1. Проверка процессов Java ==="
ps aux | grep java | grep -v grep

echo ""
echo "=== 2. Проверка порта 8080 ==="
sudo ss -tlnp | grep 8080 || echo "Порт 8080 не слушается"

echo ""
echo "=== 3. Последние логи приложения (последние 100 строк) ==="
tail -100 app.log 2>/dev/null | grep -E "(Started|ERROR|Exception|BindException|Ktor|server|port|8080)" || tail -50 app.log

echo ""
echo "=== 4. Проверка, запущен ли Ktor сервер ==="
# Ищем в логах сообщения о запуске Ktor
if grep -q "Application started" app.log 2>/dev/null || grep -q "ktor" app.log 2>/dev/null; then
    echo "✅ Найдены упоминания Ktor в логах"
    grep -i "ktor\|started\|server\|port" app.log | tail -10
else
    echo "⚠️  Не найдено сообщений о запуске Ktor сервера"
    echo "Проверяем последние строки лога:"
    tail -20 app.log
fi

echo ""
echo "=== 5. Если приложение запущено вручную, проверьте логи в том терминале ==="
echo "В терминале где запущен 'gradlew run' должны быть сообщения типа:"
echo "  - 'Application started'"
echo "  - 'Responding at http://0.0.0.0:8080'"
echo "  - Или ошибки с BindException"
