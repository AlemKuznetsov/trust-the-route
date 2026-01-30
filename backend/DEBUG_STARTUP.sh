#!/bin/bash
# Диагностика проблем с запуском приложения

cd ~/trust-the-route-backend/backend

echo "=== Диагностика проблем с запуском ==="
echo ""

# Проверяем логи
echo "1. Последние 50 строк лога:"
echo "---"
tail -50 app.log 2>/dev/null || echo "Файл app.log не найден или пуст"
echo "---"
echo ""

# Проверяем переменные окружения
echo "2. Проверка переменных окружения:"
if [ -f .env ]; then
    echo "✅ Файл .env существует"
    source .env
    if [ -z "$DB_PASSWORD" ]; then
        echo "❌ DB_PASSWORD не установлен"
    else
        echo "✅ DB_PASSWORD установлен"
    fi
    if [ -z "$JWT_SECRET" ]; then
        echo "❌ JWT_SECRET не установлен"
    else
        echo "✅ JWT_SECRET установлен (${JWT_SECRET:0:10}...)"
    fi
else
    echo "❌ Файл .env не найден"
fi
echo ""

# Проверяем подключение к базе данных
echo "3. Проверка подключения к PostgreSQL:"
if command -v psql &> /dev/null; then
    if PGPASSWORD="$DB_PASSWORD" psql -h localhost -U trust_user -d trust_the_route -c "SELECT 1;" > /dev/null 2>&1; then
        echo "✅ Подключение к базе данных работает"
    else
        echo "❌ Не удается подключиться к базе данных"
        echo "   Проверьте:"
        echo "   - Запущен ли PostgreSQL: sudo systemctl status postgresql"
        echo "   - Правильный ли пароль в DB_PASSWORD"
        echo "   - Существует ли пользователь trust_user и база trust_the_route"
    fi
else
    echo "⚠️  psql не найден, пропускаем проверку БД"
fi
echo ""

# Пробуем запустить вручную для просмотра ошибок
echo "4. Попытка запуска в обычном режиме (для просмотра ошибок):"
echo "   (Остановите через Ctrl+C после просмотра ошибок)"
echo ""
echo "Выполните вручную:"
echo "  source .env"
echo "  ./gradlew run"
echo ""

# Проверяем порт
echo "5. Проверка порта 8080:"
if lsof -i :8080 > /dev/null 2>&1; then
    echo "⚠️  Порт 8080 уже занят:"
    lsof -i :8080
else
    echo "✅ Порт 8080 свободен"
fi
