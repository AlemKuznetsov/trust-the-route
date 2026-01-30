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
    cat > ~/trust-the-route-backend/backend/.env << EOF
export DB_PASSWORD='$DB_PASSWORD'
export JWT_SECRET='$JWT_SECRET'
EOF
    
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
echo ""
echo "Или запустите приложение:"
echo "  cd ~/trust-the-route-backend/backend"
echo "  source .env"
echo "  ./RUN_APPLICATION.sh"
