#!/bin/bash
# СРОЧНОЕ исправление переменных окружения в systemd service

cd ~/trust-the-route-backend/backend

echo "=== СРОЧНОЕ исправление systemd service ==="
echo ""

# Сначала исправляем .env файл БЕЗ export
echo "1. Исправление .env файла..."
cat > .env << 'EOF'
DB_PASSWORD=Bcnbyf293!
JWT_SECRET=8e9db1a9046ed6baa814234f01eb7c38f1c28199e9817b261507093278557cf9
EOF
echo "✅ .env файл исправлен (БЕЗ export)"
echo ""

# Останавливаем service
echo "2. Остановка service..."
sudo systemctl stop trust-the-route-backend
sleep 2

# Убиваем все процессы на порту 8080
echo "3. Очистка порта 8080..."
sudo lsof -ti:8080 | xargs -r sudo kill -9 2>/dev/null
sleep 1

# Убиваем все Java процессы
echo "4. Очистка Java процессов..."
ps aux | grep -E "gradlew|java.*backend" | grep -v grep | awk '{print $2}' | xargs -r sudo kill -9 2>/dev/null
sleep 1

# Создаем правильный systemd service файл БЕЗ export
echo "5. Создание правильного systemd service файла..."
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

echo "✅ Service файл создан (БЕЗ export в Environment)"
echo ""

# Перезагружаем systemd
echo "6. Перезагрузка systemd daemon..."
sudo systemctl daemon-reload
echo "✅ Systemd перезагружен"
echo ""

# Запускаем service
echo "7. Запуск service..."
sudo systemctl start trust-the-route-backend

sleep 5

echo ""
echo "=== Статус service ==="
sudo systemctl status trust-the-route-backend --no-pager | head -30

echo ""
echo "=== Проверка переменных окружения ==="
sudo systemctl show trust-the-route-backend | grep -E "Environment|DB_PASSWORD|JWT_SECRET" | head -5

echo ""
echo "=== Проверка порта 8080 ==="
sudo ss -tlnp | grep 8080 || echo "⚠️  Порт 8080 не слушается (подождите еще 10-20 секунд)"

echo ""
echo "=== Последние логи (без ошибок export) ==="
sudo journalctl -u trust-the-route-backend -n 20 --no-pager | grep -v "export" | tail -15

echo ""
echo "=== Готово! ==="
echo ""
echo "Если service работает, вы увидите 'active (running)' в статусе."
echo "Если все еще есть проблемы, проверьте логи:"
echo "  sudo journalctl -u trust-the-route-backend -f"
