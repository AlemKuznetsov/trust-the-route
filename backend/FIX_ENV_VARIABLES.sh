#!/bin/bash
# Исправление переменных окружения в systemd service

cd ~/trust-the-route-backend/backend

echo "=== Исправление systemd service файла ==="
echo ""

# Создаем правильный .env файл БЕЗ export
echo "Создание .env файла без export..."
cat > .env << 'EOF'
DB_PASSWORD=Bcnbyf293!
JWT_SECRET=8e9db1a9046ed6baa814234f01eb7c38f1c28199e9817b261507093278557cf9
EOF
echo "✅ .env файл обновлен"
echo ""

# Обновляем systemd service файл БЕЗ EnvironmentFile и БЕЗ export
echo "Обновление systemd service файла..."
sudo tee /etc/systemd/system/trust-the-route-backend.service > /dev/null << 'EOF'
[Unit]
Description=Trust The Route Backend API
After=network.target postgresql.service

[Service]
Type=simple
User=ubuntu
Group=ubuntu
WorkingDirectory=/home/ubuntu/trust-the-route-backend/backend
Environment="DB_PASSWORD=Bcnbyf293!"
Environment="JWT_SECRET=8e9db1a9046ed6baa814234f01eb7c38f1c28199e9817b261507093278557cf9"
Environment="JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64"
Environment="PATH=/usr/lib/jvm/java-17-openjdk-amd64/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
ExecStart=/bin/bash -c 'cd /home/ubuntu/trust-the-route-backend/backend && ./gradlew run --no-daemon'
Restart=always
RestartSec=10
StandardOutput=append:/home/ubuntu/trust-the-route-backend/backend/app.log
StandardError=append:/home/ubuntu/trust-the-route-backend/backend/app.log

[Install]
WantedBy=multi-user.target
EOF

echo "✅ Service файл обновлен"
echo ""

# Перезагружаем systemd
echo "Перезагрузка systemd daemon..."
sudo systemctl daemon-reload
echo "✅ Systemd перезагружен"
echo ""

# Останавливаем старый процесс
echo "Остановка service..."
sudo systemctl stop trust-the-route-backend
sleep 2

# Убиваем все процессы на порту 8080
echo "Очистка порта 8080..."
PID_8080=$(sudo lsof -ti:8080 2>/dev/null)
if [ ! -z "$PID_8080" ]; then
    echo "Найден процесс на порту 8080: PID=$PID_8080"
    sudo kill -9 $PID_8080
    sleep 2
fi

# Убиваем все Java процессы связанные с backend
echo "Очистка Java процессов..."
JAVA_PIDS=$(ps aux | grep -E "gradlew|java.*backend" | grep -v grep | awk '{print $2}')
if [ ! -z "$JAVA_PIDS" ]; then
    echo "Найдены Java процессы: $JAVA_PIDS"
    echo "$JAVA_PIDS" | xargs -r sudo kill -9 2>/dev/null
    sleep 2
fi

echo ""

# Запускаем service
echo "Запуск service..."
sudo systemctl start trust-the-route-backend

sleep 5

echo ""
echo "=== Статус service ==="
sudo systemctl status trust-the-route-backend --no-pager | head -25

echo ""
echo "=== Проверка порта 8080 ==="
sudo ss -tlnp | grep 8080 || echo "Порт 8080 не слушается (подождите еще немного)"

echo ""
echo "=== Последние логи (последние 30 строк) ==="
sudo journalctl -u trust-the-route-backend -n 30 --no-pager

echo ""
echo "=== Проверка переменных окружения ==="
sudo systemctl show trust-the-route-backend | grep -E "Environment|DB_PASSWORD|JWT_SECRET" | head -5

echo ""
echo "=== Готово! ==="
echo ""
echo "Если service все еще не работает, проверьте логи:"
echo "  sudo journalctl -u trust-the-route-backend -n 100"
echo "  tail -f ~/trust-the-route-backend/backend/app.log"
