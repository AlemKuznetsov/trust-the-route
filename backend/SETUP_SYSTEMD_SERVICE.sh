#!/bin/bash
# Настройка systemd service для автозапуска бэкенда

cd ~/trust-the-route-backend/backend

echo "=== Настройка systemd service ==="
echo ""

# Проверяем, существует ли .env файл
if [ ! -f .env ]; then
    echo "❌ Файл .env не найден!"
    echo "Создайте его командой: ./SETUP_ENV.sh"
    exit 1
fi

# Загружаем переменные окружения
source .env

# Получаем абсолютный путь к проекту
PROJECT_DIR=$(pwd)
USER_NAME=$(whoami)

echo "Проект: $PROJECT_DIR"
echo "Пользователь: $USER_NAME"
echo ""

# Создаем systemd service файл
sudo tee /etc/systemd/system/trust-the-route-backend.service > /dev/null << EOF
[Unit]
Description=Trust The Route Backend API
After=network.target postgresql.service

[Service]
Type=simple
User=$USER_NAME
WorkingDirectory=$PROJECT_DIR
EnvironmentFile=$PROJECT_DIR/.env
ExecStart=$PROJECT_DIR/gradlew run --no-daemon
Restart=always
RestartSec=10
StandardOutput=append:$PROJECT_DIR/app.log
StandardError=append:$PROJECT_DIR/app.log

[Install]
WantedBy=multi-user.target
EOF

echo "✅ Service файл создан"
echo ""

# Перезагружаем systemd
sudo systemctl daemon-reload

echo "✅ Systemd перезагружен"
echo ""

# Останавливаем старое приложение, если запущено
pkill -f "gradlew run" 2>/dev/null
sleep 2

# Запускаем через systemd
echo "Запуск service..."
sudo systemctl start trust-the-route-backend

sleep 3

# Проверяем статус
echo ""
echo "Статус service:"
sudo systemctl status trust-the-route-backend --no-pager | head -15

echo ""
echo "Включение автозапуска..."
sudo systemctl enable trust-the-route-backend

echo ""
echo "=== Готово! ==="
echo ""
echo "Полезные команды:"
echo "  - Статус: sudo systemctl status trust-the-route-backend"
echo "  - Остановить: sudo systemctl stop trust-the-route-backend"
echo "  - Запустить: sudo systemctl start trust-the-route-backend"
echo "  - Перезапустить: sudo systemctl restart trust-the-route-backend"
echo "  - Логи: journalctl -u trust-the-route-backend -f"
echo "  - Или: tail -f $PROJECT_DIR/app.log"
