#!/bin/bash
# Проверка маршрутов и логов для диагностики 404 ошибок

cd ~/trust-the-route-backend/backend

echo "=========================================="
echo "Проверка маршрутов и логов"
echo "=========================================="
echo ""

echo "1. Проверка маршрутов в AuthRoutes.kt:"
echo "----------------------------------------"
grep -n "put\|delete\|post" src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt | grep -i "profile\|password\|account" | head -10
echo ""

echo "2. Проверка последних логов сервиса:"
echo "----------------------------------------"
sudo journalctl -u trust-the-route-backend -n 50 --no-pager | grep -E "404|error|exception|PUT|DELETE" | tail -20
echo ""

echo "3. Проверка, что сервис слушает порт 8080:"
echo "----------------------------------------"
sudo netstat -tlnp | grep 8080 || sudo ss -tlnp | grep 8080
echo ""

echo "4. Тест доступности API:"
echo "----------------------------------------"
curl -X GET http://localhost:8080/api/v1/auth/login -v 2>&1 | grep -E "HTTP|404|200|401" | head -5
echo ""

echo "5. Проверка структуры маршрутов:"
echo "----------------------------------------"
echo "PUT /api/v1/auth/profile:"
grep -A 5 'put("/profile")' src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt | head -3
echo ""
echo "PUT /api/v1/auth/password:"
grep -A 5 'put("/password")' src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt | head -3
echo ""
echo "DELETE /api/v1/auth/account:"
grep -A 5 'delete("/account")' src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt | head -3
echo ""

echo "=========================================="
echo "Проверка завершена"
echo "=========================================="
