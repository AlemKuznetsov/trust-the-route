#!/bin/bash
# Проверка успешной сборки проекта

cd ~/trust-the-route-backend/backend

echo "=== Проверка сборки ==="
./gradlew clean build

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Сборка успешна!"
    echo ""
    echo "=== Следующие шаги ==="
    echo "1. Установите переменные окружения:"
    echo "   export DB_PASSWORD='ваш_пароль_от_postgres'"
    echo "   export JWT_SECRET='ваш_секретный_ключ_минимум_32_символа'"
    echo ""
    echo "2. Запустите приложение:"
    echo "   ./gradlew run"
    echo ""
    echo "   Или в фоновом режиме:"
    echo "   nohup ./gradlew run > app.log 2>&1 &"
    echo ""
    echo "3. Проверьте логи:"
    echo "   tail -f app.log"
else
    echo ""
    echo "❌ Сборка не удалась. Проверьте ошибки выше."
fi
