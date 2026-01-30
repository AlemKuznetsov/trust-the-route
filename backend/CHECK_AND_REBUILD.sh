#!/bin/bash
# Проверка и пересборка backend

cd ~/trust-the-route-backend/backend

echo "=== Проверка backend ==="
echo ""

# 1. Проверяем, что файлы обновлены
echo "1. Проверка наличия endpoint /yandex в AuthRoutes.kt..."
if grep -q "post(\"/yandex\")" src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt; then
    echo "✅ Endpoint /yandex найден в коде"
else
    echo "❌ Endpoint /yandex НЕ найден в коде!"
    echo "Нужно обновить файлы на сервере"
    exit 1
fi

echo ""
echo "2. Проверка наличия метода createYandexUser в UserRepository.kt..."
if grep -q "fun createYandexUser" src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt; then
    echo "✅ Метод createYandexUser найден"
else
    echo "❌ Метод createYandexUser НЕ найден!"
    echo "Нужно обновить файлы на сервере"
    exit 1
fi

echo ""
echo "3. Проверка модели YandexAuthRequest..."
if grep -q "data class YandexAuthRequest" src/main/kotlin/com/trusttheroute/backend/models/User.kt; then
    echo "✅ Модель YandexAuthRequest найдена"
else
    echo "❌ Модель YandexAuthRequest НЕ найдена!"
    echo "Нужно обновить файлы на сервере"
    exit 1
fi

echo ""
echo "4. Остановка service..."
sudo systemctl stop trust-the-route-backend
sleep 2

echo ""
echo "5. Очистка старых процессов..."
sudo lsof -ti:8080 | xargs -r sudo kill -9 2>/dev/null
ps aux | grep -E "gradlew|java.*backend" | grep -v grep | awk '{print $2}' | xargs -r sudo kill -9 2>/dev/null
sleep 2

echo ""
echo "6. Очистка предыдущей сборки..."
./gradlew clean

echo ""
echo "7. Пересборка проекта..."
./gradlew build --no-daemon

if [ $? -ne 0 ]; then
    echo "❌ Ошибка сборки! Проверьте логи выше"
    exit 1
fi

echo ""
echo "✅ Сборка успешна"

echo ""
echo "8. Запуск service..."
sudo systemctl start trust-the-route-backend

sleep 5

echo ""
echo "=== Статус service ==="
sudo systemctl status trust-the-route-backend --no-pager | head -25

echo ""
echo "=== Проверка порта 8080 ==="
sudo ss -tlnp | grep 8080 || echo "⚠️  Порт 8080 не слушается"

echo ""
echo "=== Последние логи (проверка на ошибки) ==="
sudo journalctl -u trust-the-route-backend -n 50 --no-pager | tail -20

echo ""
echo "=== Тестирование endpoints ==="
echo ""
echo "Тест /yandex endpoint:"
curl -X POST http://localhost:8080/api/v1/auth/yandex \
  -H "Content-Type: application/json" \
  -d '{"yandexToken":"test","email":"test@test.com","name":"Test"}' \
  -w "\nHTTP Code: %{http_code}\n" \
  -s | head -3

echo ""
echo ""
echo "Тест /profile endpoint:"
curl -X PUT http://localhost:8080/api/v1/auth/profile \
  -H "Content-Type: application/json" \
  -d '{"name":"Test"}' \
  -w "\nHTTP Code: %{http_code}\n" \
  -s | head -3

echo ""
echo ""
echo "=== Готово! ==="
echo ""
echo "Если endpoints возвращают 400 или 401 (не 404), значит они работают!"
echo "404 означает, что endpoint не найден - проверьте логи выше на ошибки компиляции."
