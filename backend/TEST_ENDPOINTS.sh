#!/bin/bash
# Тестирование endpoints backend

echo "=== Тестирование endpoints backend ==="
echo ""

BASE_URL="http://158.160.217.181:8080/api/v1/auth"

echo "1. Тест /yandex endpoint (должен вернуть 400 - валидация):"
curl -X POST "$BASE_URL/yandex" \
  -H "Content-Type: application/json" \
  -d '{"yandexToken":"test","email":"test@test.com","name":"Test"}' \
  -w "\nHTTP Code: %{http_code}\n" \
  -s

echo ""
echo ""
echo "2. Тест /profile endpoint (должен вернуть 401 - нет токена):"
curl -X PUT "$BASE_URL/profile" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test"}' \
  -w "\nHTTP Code: %{http_code}\n" \
  -s

echo ""
echo ""
echo "3. Тест /password endpoint (должен вернуть 401 - нет токена):"
curl -X PUT "$BASE_URL/password" \
  -H "Content-Type: application/json" \
  -d '{"oldPassword":"test","newPassword":"test123"}' \
  -w "\nHTTP Code: %{http_code}\n" \
  -s

echo ""
echo ""
echo "4. Тест /account endpoint (должен вернуть 401 - нет токена):"
curl -X DELETE "$BASE_URL/account?confirmation=УДАЛИТЬ" \
  -w "\nHTTP Code: %{http_code}\n" \
  -s

echo ""
echo ""
echo "=== Результаты ==="
echo "Если все endpoints возвращают 400 или 401 (не 404), значит они доступны!"
echo "404 означает, что endpoint не найден."
echo "401 означает, что endpoint найден, но требуется авторизация (это правильно)."
