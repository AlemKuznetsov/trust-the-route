#!/bin/bash
# Исправление systemd service

cd ~/trust-the-route-backend/backend

echo "=== Обновление systemd service ==="

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
echo "=== Перезагрузка systemd ==="
sudo systemctl daemon-reload

echo ""
echo "=== Запуск service ==="
sudo systemctl start trust-the-route-backend

sleep 5

echo ""
echo "=== Статус service ==="
sudo systemctl status trust-the-route-backend --no-pager | head -25

echo ""
echo "=== Проверка порта 8080 ==="
sudo ss -tlnp | grep 8080 || echo "Порт 8080 не слушается (подождите еще немного)"

echo ""
echo "=== Последние логи ==="
tail -30 app.log
