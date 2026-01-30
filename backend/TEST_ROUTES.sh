#!/bin/bash
# Тестирование маршрутов API

cd ~/trust-the-route-backend/backend

echo "=========================================="
echo "Тестирование маршрутов API"
echo "=========================================="
echo ""

echo "1. Проверка POST /api/v1/auth/login (должен работать):"
echo "----------------------------------------"
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"test"}' \
  -w "\nHTTP Status: %{http_code}\n" \
  2>&1 | tail -5
echo ""

echo "2. Проверка PUT /api/v1/auth/profile (должен вернуть 401 без токена):"
echo "----------------------------------------"
curl -X PUT http://localhost:8080/api/v1/auth/profile \
  -H "Content-Type: application/json" \
  -d '{"name":"Test"}' \
  -w "\nHTTP Status: %{http_code}\n" \
  2>&1 | tail -5
echo ""

echo "3. Проверка PUT /api/v1/auth/password (должен вернуть 401 без токена):"
echo "----------------------------------------"
curl -X PUT http://localhost:8080/api/v1/auth/password \
  -H "Content-Type: application/json" \
  -d '{"oldPassword":"test","newPassword":"test123"}' \
  -w "\nHTTP Status: %{http_code}\n" \
  2>&1 | tail -5
echo ""

echo "4. Проверка DELETE /api/v1/auth/account (должен вернуть 401 без токена):"
echo "----------------------------------------"
curl -X DELETE "http://localhost:8080/api/v1/auth/account?confirmation=УДАЛИТЬ" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n" \
  2>&1 | tail -5
echo ""

echo "=========================================="
echo "Если все запросы возвращают 404, значит маршруты не зарегистрированы"
echo "Если возвращают 401, значит маршруты работают, но нужна аутентификация"
echo "=========================================="
