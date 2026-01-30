#!/bin/bash
# Проверка и исправление файлов на сервере

cd ~/trust-the-route-backend/backend

echo "=== Проверка файлов на сервере ==="
echo ""

# Проверка 1: Endpoint /yandex
echo "1. Проверка endpoint /yandex в AuthRoutes.kt..."
if grep -q 'post("/yandex")' src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt 2>/dev/null; then
    echo "   ✅ Endpoint /yandex найден"
else
    echo "   ❌ Endpoint /yandex НЕ найден!"
    echo "   ⚠️  Нужно обновить файл AuthRoutes.kt на сервере"
    MISSING_FILES=1
fi

# Проверка 2: Метод createYandexUser
echo ""
echo "2. Проверка метода createYandexUser в UserRepository.kt..."
if grep -q "fun createYandexUser" src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt 2>/dev/null; then
    echo "   ✅ Метод createYandexUser найден"
else
    echo "   ❌ Метод createYandexUser НЕ найден!"
    echo "   ⚠️  Нужно обновить файл UserRepository.kt на сервере"
    MISSING_FILES=1
fi

# Проверка 3: Модель YandexAuthRequest
echo ""
echo "3. Проверка модели YandexAuthRequest в User.kt..."
if grep -q "data class YandexAuthRequest" src/main/kotlin/com/trusttheroute/backend/models/User.kt 2>/dev/null; then
    echo "   ✅ Модель YandexAuthRequest найдена"
else
    echo "   ❌ Модель YandexAuthRequest НЕ найдена!"
    echo "   ⚠️  Нужно обновить файл User.kt на сервере"
    MISSING_FILES=1
fi

echo ""
if [ "$MISSING_FILES" = "1" ]; then
    echo "=== ❌ ОБНАРУЖЕНЫ ОТСУТСТВУЮЩИЕ ФАЙЛЫ ==="
    echo ""
    echo "Нужно загрузить обновленные файлы на сервер."
    echo ""
    echo "Варианты:"
    echo "1. Используйте SCP для загрузки файлов с вашего компьютера"
    echo "2. Или создайте файлы вручную на сервере"
    echo ""
    echo "Файлы для обновления:"
    echo "  - backend/src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt"
    echo "  - backend/src/main/kotlin/com/trusttheroute/backend/models/User.kt"
    echo "  - backend/src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt"
    echo ""
    exit 1
else
    echo "=== ✅ Все файлы на месте ==="
    echo ""
    echo "Пересборка проекта..."
    
    # Остановка service
    echo ""
    echo "Остановка service..."
    sudo systemctl stop trust-the-route-backend
    sleep 2
    
    # Очистка процессов
    echo "Очистка процессов..."
    sudo lsof -ti:8080 | xargs -r sudo kill -9 2>/dev/null
    ps aux | grep -E "gradlew|java.*backend" | grep -v grep | awk '{print $2}' | xargs -r sudo kill -9 2>/dev/null
    sleep 2
    
    # Пересборка
    echo "Очистка и пересборка..."
    ./gradlew clean build --no-daemon
    
    if [ $? -ne 0 ]; then
        echo "❌ Ошибка сборки!"
        exit 1
    fi
    
    echo ""
    echo "✅ Сборка успешна"
    
    # Запуск service
    echo ""
    echo "Запуск service..."
    sudo systemctl start trust-the-route-backend
    
    sleep 5
    
    echo ""
    echo "=== Статус service ==="
    sudo systemctl status trust-the-route-backend --no-pager | head -20
    
    echo ""
    echo "=== Тест endpoints ==="
    echo ""
    echo "Тест /yandex:"
    curl -X POST http://localhost:8080/api/v1/auth/yandex \
      -H "Content-Type: application/json" \
      -d '{"yandexToken":"test","email":"test@test.com","name":"Test"}' \
      -w "\nHTTP Code: %{http_code}\n" \
      -s | head -3
    
    echo ""
    echo ""
    echo "Тест /profile:"
    curl -X PUT http://localhost:8080/api/v1/auth/profile \
      -H "Content-Type: application/json" \
      -d '{"name":"Test"}' \
      -w "\nHTTP Code: %{http_code}\n" \
      -s | head -3
    
    echo ""
    echo ""
    echo "=== Готово! ==="
    echo "Если endpoints возвращают 400 или 401 (не 404), значит они работают!"
fi
