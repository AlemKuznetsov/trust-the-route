#!/bin/bash
# Тестирование API

echo "=== Проверка порта 8080 ==="
sudo ss -tlnp | grep 8080

echo ""
echo "=== Тест API endpoint ==="
echo "Проверяем доступность сервера..."

# Проверяем базовый endpoint (если есть)
curl -v http://localhost:8080/ 2>&1 | head -20

echo ""
echo "=== Тест регистрации ==="
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "test123",
    "name": "Test User"
  }' 2>&1 | head -30

echo ""
echo ""
echo "=== Статус приложения ==="
echo "Если вы видите ответы выше, значит API работает!"
echo ""
echo "Для остановки приложения нажмите Ctrl+C"
echo "Для запуска через systemd используйте:"
echo "  sudo systemctl start trust-the-route-backend"
