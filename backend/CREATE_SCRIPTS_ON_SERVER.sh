#!/bin/bash
# Создание всех скриптов на сервере

# Этот файл нужно выполнить на сервере через SSH
# Или скопировать команды из него и выполнить вручную

cd ~/trust-the-route-backend/backend

# Создаем SETUP_ENV.sh
cat > SETUP_ENV.sh << 'EOF'
#!/bin/bash
# Установка переменных окружения для бэкенда

echo "=== Настройка переменных окружения ==="
echo ""

# Проверяем, существует ли файл .env
if [ -f ~/trust-the-route-backend/backend/.env ]; then
    echo "📄 Файл .env найден, загружаем переменные..."
    source ~/trust-the-route-backend/backend/.env
else
    echo "📝 Создаем файл .env..."
    
    # Запрашиваем пароль от PostgreSQL
    echo ""
    read -sp "Введите пароль от PostgreSQL (trust_user): " DB_PASSWORD
    echo ""
    
    # Генерируем JWT секрет, если его нет
    if [ -z "$JWT_SECRET" ]; then
        echo "Генерируем JWT секрет..."
        JWT_SECRET=$(openssl rand -hex 32)
        echo "✅ Сгенерирован JWT_SECRET: $JWT_SECRET"
    else
        read -p "Введите JWT_SECRET (или нажмите Enter для генерации нового): " USER_JWT_SECRET
        if [ -n "$USER_JWT_SECRET" ]; then
            JWT_SECRET=$USER_JWT_SECRET
        else
            JWT_SECRET=$(openssl rand -hex 32)
            echo "✅ Сгенерирован JWT_SECRET: $JWT_SECRET"
        fi
    fi
    
    # Сохраняем в .env файл
    cat > ~/trust-the-route-backend/backend/.env << ENVEOF
export DB_PASSWORD='$DB_PASSWORD'
export JWT_SECRET='$JWT_SECRET'
ENVEOF
    
    echo ""
    echo "✅ Файл .env создан в ~/trust-the-route-backend/backend/.env"
fi

# Загружаем переменные
source ~/trust-the-route-backend/backend/.env

echo ""
echo "=== Текущие переменные окружения ==="
echo "DB_PASSWORD: установлен (скрыт)"
echo "JWT_SECRET: ${JWT_SECRET:0:10}... (первые 10 символов)"
echo ""
echo "✅ Переменные окружения готовы!"
echo ""
echo "Для постоянной загрузки добавьте в ~/.bashrc:"
echo "  source ~/trust-the-route-backend/backend/.env"
EOF

# Создаем RUN_APPLICATION.sh
cat > RUN_APPLICATION.sh << 'EOF'
#!/bin/bash
# Запуск Ktor приложения с переменными окружения

cd ~/trust-the-route-backend/backend

# Загружаем переменные окружения
if [ -f .env ]; then
    source .env
else
    echo "❌ Файл .env не найден. Запустите сначала SETUP_ENV.sh"
    exit 1
fi

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
EOF

# Создаем START_BACKGROUND.sh
cat > START_BACKGROUND.sh << 'EOF'
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
EOF

# Делаем скрипты исполняемыми
chmod +x SETUP_ENV.sh RUN_APPLICATION.sh START_BACKGROUND.sh

echo "✅ Все скрипты созданы и сделаны исполняемыми!"
echo ""
echo "Теперь вы можете:"
echo "  1. Настроить переменные окружения: ./SETUP_ENV.sh"
echo "  2. Запустить приложение: ./RUN_APPLICATION.sh"
echo "  3. Или запустить в фоне: ./START_BACKGROUND.sh"
