#!/bin/bash
# Проверка доступности endpoints backend

echo "=== Проверка endpoints backend ==="
echo ""

BASE_URL="http://158.160.217.181:8080/api/v1/auth"

echo "1. Проверка /register (должен вернуть 400 или 409):"
curl -X POST "$BASE_URL/register" \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"test","name":"Test"}' \
  -w "\nHTTP Code: %{http_code}\n" \
  -s | head -5

echo ""
echo "2. Проверка /login (должен вернуть 400 или 401):"
curl -X POST "$BASE_URL/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"test"}' \
  -w "\nHTTP Code: %{http_code}\n" \
  -s | head -5

echo ""
echo "3. Проверка /yandex (должен вернуть 400):"
curl -X POST "$BASE_URL/yandex" \
  -H "Content-Type: application/json" \
  -d '{"yandexToken":"test","email":"test@test.com","name":"Test"}' \
  -w "\nHTTP Code: %{http_code}\n" \
  -s | head -5

echo ""
echo "4. Проверка /profile (должен вернуть 401 без токена):"
curl -X PUT "$BASE_URL/profile" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test"}' \
  -w "\nHTTP Code: %{http_code}\n" \
  -s | head -5

echo ""
echo "5. Проверка /password (должен вернуть 401 без токена):"
curl -X PUT "$BASE_URL/password" \
  -H "Content-Type: application/json" \
  -d '{"oldPassword":"test","newPassword":"test123"}' \
  -w "\nHTTP Code: %{http_code}\n" \
  -s | head -5

echo ""
echo "6. Проверка /account (должен вернуть 401 без токена):"
curl -X DELETE "$BASE_URL/account?confirmation=УДАЛИТЬ" \
  -w "\nHTTP Code: %{http_code}\n" \
  -s | head -5

echo ""
echo "=== Проверка завершена ==="
echo ""
echo "Если все endpoints возвращают 404, значит backend не запущен или не перезапущен после изменений."
echo "Выполните: sudo systemctl restart trust-the-route-backend"
