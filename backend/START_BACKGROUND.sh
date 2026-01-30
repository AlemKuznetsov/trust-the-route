#!/bin/bash
# Запуск приложения в фоновом режиме

cd ~/trust-the-route-backend/backend

# Загружаем переменные окружения
if [ -f .env ]; then
    source .env
else
    echo "❌ Файл .env не найден. Запустите сначала SETUP_ENV.sh"
    exit 1
fi

# Проверяем переменные
if [ -z "$DB_PASSWORD" ] || [ -z "$JWT_SECRET" ]; then
    echo "❌ Переменные окружения не установлены. Запустите SETUP_ENV.sh"
    exit 1
fi

echo "=== Запуск приложения в фоновом режиме ==="
echo ""

# Останавливаем предыдущий процесс, если он запущен
if pgrep -f "gradlew run" > /dev/null; then
    echo "⚠️  Найден запущенный процесс, останавливаем..."
    pkill -f "gradlew run"
    sleep 2
fi

# Запускаем в фоне
nohup ./gradlew run > app.log 2>&1 &
APP_PID=$!

echo "✅ Приложение запущено в фоновом режиме"
echo "   PID: $APP_PID"
echo "   Логи: ~/trust-the-route-backend/backend/app.log"
echo ""
echo "Проверка статуса:"
sleep 3
if ps -p $APP_PID > /dev/null; then
    echo "✅ Процесс работает"
    echo ""
    echo "Последние строки лога:"
    tail -10 app.log
    echo ""
    echo "Для просмотра логов в реальном времени:"
    echo "  tail -f app.log"
    echo ""
    echo "Для остановки приложения:"
    echo "  pkill -f 'gradlew run'"
else
    echo "❌ Процесс не запустился. Проверьте логи:"
    echo "  tail -50 app.log"
fi
