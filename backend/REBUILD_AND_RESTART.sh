#!/bin/bash
# Скрипт для пересборки проекта и перезапуска сервиса

set -e

cd ~/trust-the-route-backend/backend

echo "=== Проверка файлов ==="
echo ""

# Удаляем дубликаты из User.kt, если они есть
if grep -q "data class ErrorResponse" src/main/kotlin/com/trusttheroute/backend/models/User.kt 2>/dev/null; then
    echo "⚠️  Найдены дубликаты в User.kt, удаляем..."
    chmod +x FIX_DUPLICATES_SIMPLE.sh 2>/dev/null || true
    ./FIX_DUPLICATES_SIMPLE.sh
    echo ""
fi

# Проверяем наличие исправленных файлов
if [ ! -f "src/main/kotlin/com/trusttheroute/backend/models/ApiResponses.kt" ]; then
    echo "❌ Файл ApiResponses.kt не найден!"
    exit 1
fi

if ! grep -q "import com.trusttheroute.backend.models.ErrorResponse" src/main/kotlin/com/trusttheroute/backend/Application.kt; then
    echo "❌ В Application.kt отсутствует импорт ErrorResponse!"
    exit 1
fi

if ! grep -q "MessageResponse" src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt; then
    echo "❌ В AuthRoutes.kt отсутствует MessageResponse!"
    exit 1
fi

echo "✅ Все файлы на месте"
echo ""

echo "=== Остановка сервиса ==="
sudo systemctl stop trust-the-route-backend || true
echo ""

echo "=== Очистка старых сборок ==="
./gradlew clean
echo ""

echo "=== Сборка проекта ==="
./gradlew build --no-daemon

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Сборка успешна!"
    echo ""
    
    echo "=== Перезапуск сервиса ==="
    sudo systemctl start trust-the-route-backend
    sleep 2
    
    echo ""
    echo "=== Проверка статуса сервиса ==="
    sudo systemctl status trust-the-route-backend --no-pager -l
    
    echo ""
    echo "=== Проверка логов ==="
    echo "Последние 20 строк логов:"
    sudo journalctl -u trust-the-route-backend -n 20 --no-pager
    
else
    echo ""
    echo "❌ Ошибка сборки!"
    echo "Проверьте вывод выше для деталей"
    exit 1
fi
