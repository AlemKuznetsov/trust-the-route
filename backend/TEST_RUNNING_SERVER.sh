#!/bin/bash
# Тестирование работающего сервера

cd ~/trust-the-route-backend/backend

echo "=== 1. Проверка порта 8080 ==="
sudo ss -tlnp | grep 8080

echo ""
echo "=== 2. Тест API - регистрация ==="
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "test123",
    "name": "Test User"
  }' 2>&1

echo ""
echo ""
echo "=== 3. Тест API - вход ==="
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "test123"
  }' 2>&1

echo ""
echo ""
echo "=== 4. Проверка процессов Java ==="
ps aux | grep java | grep -v grep
