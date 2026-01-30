#!/bin/bash
# Финальная проверка работоспособности

cd ~/trust-the-route-backend/backend

echo "=== 1. Статус service ==="
sudo systemctl status trust-the-route-backend --no-pager | head -10

echo ""
echo "=== 2. Проверка порта 8080 ==="
sudo ss -tlnp | grep 8080

echo ""
echo "=== 3. Последние логи приложения (ищем сообщение о запуске сервера) ==="
tail -50 app.log | grep -E "(Responding|started|ERROR|Exception)" || tail -30 app.log

echo ""
echo "=== 4. Тест API - регистрация ==="
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "test123",
    "name": "Test User"
  }' 2>&1

echo ""
echo ""
echo "=== 5. Тест API - повторная регистрация (должна вернуть ошибку) ==="
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "test123",
    "name": "Test User"
  }' 2>&1

echo ""
echo ""
echo "=== 6. Тест API - вход ==="
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "test123"
  }' 2>&1

echo ""
echo ""
echo "=== 7. Проверка процессов Java ==="
ps aux | grep java | grep -v grep | wc -l
echo "Java процессов запущено"
