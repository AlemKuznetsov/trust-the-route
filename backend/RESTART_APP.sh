#!/bin/bash
# Перезапуск приложения

cd ~/trust-the-route-backend/backend

echo "=== Перезапуск приложения ==="
echo ""

# Останавливаем предыдущий процесс, если запущен
if pgrep -f "gradlew run" > /dev/null; then
    echo "Останавливаем предыдущий процесс..."
    pkill -f "gradlew run"
    sleep 2
fi

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

echo "✅ Переменные окружения загружены"
echo ""

# Запускаем в фоне
echo "Запуск приложения в фоновом режиме..."
nohup ./gradlew run > app.log 2>&1 &
APP_PID=$!

echo "Ожидание запуска (5 секунд)..."
sleep 5

# Проверяем статус
if ps -p $APP_PID > /dev/null; then
    echo "✅ Приложение запущено успешно!"
    echo "   PID: $APP_PID"
    echo "   URL: http://0.0.0.0:8080"
    echo ""
    echo "Последние строки лога:"
    tail -10 app.log
    echo ""
    echo "Полезные команды:"
    echo "  - Просмотр логов: tail -f app.log"
    echo "  - Остановка: pkill -f 'gradlew run'"
    echo "  - Проверка статуса: pgrep -f 'gradlew run'"
else
    echo "❌ Приложение не запустилось. Проверьте логи:"
    echo "   tail -50 app.log"
fi
