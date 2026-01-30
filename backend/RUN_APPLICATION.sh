#!/bin/bash
# Запуск Ktor приложения с переменными окружения

cd ~/trust-the-route-backend/backend

echo "=== Запуск Trust The Route Backend ==="
echo ""

# Проверяем переменные окружения
if [ -z "$DB_PASSWORD" ]; then
    echo "⚠️  DB_PASSWORD не установлен"
    echo "Установите его командой:"
    echo "  export DB_PASSWORD='ваш_пароль_от_postgres'"
    echo ""
fi

if [ -z "$JWT_SECRET" ]; then
    echo "⚠️  JWT_SECRET не установлен"
    echo "Установите его командой:"
    echo "  export JWT_SECRET='ваш_секретный_ключ_минимум_32_символа'"
    echo ""
    echo "Или сгенерируйте новый:"
    echo "  openssl rand -hex 32"
    echo ""
fi

if [ -z "$DB_PASSWORD" ] || [ -z "$JWT_SECRET" ]; then
    echo "❌ Необходимо установить переменные окружения перед запуском!"
    exit 1
fi

echo "✅ Переменные окружения установлены"
echo "   DB_PASSWORD: установлен"
echo "   JWT_SECRET: установлен"
echo ""
echo "Запуск приложения..."
echo "Приложение будет доступно на: http://0.0.0.0:8080"
echo ""
echo "Для остановки нажмите Ctrl+C"
echo ""

# Запускаем приложение
./gradlew run
