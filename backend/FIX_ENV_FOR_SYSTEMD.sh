#!/bin/bash
# Исправление .env файла для systemd

cd ~/trust-the-route-backend/backend

echo "=== Исправление .env для systemd ==="
echo ""

# Проверяем текущий .env
echo "Текущий .env:"
cat .env
echo ""

# Создаем новый .env без export для systemd
echo "Создаем .env без export..."
cat > .env << 'ENVEOF'
DB_PASSWORD=Bcnbyf293!
JWT_SECRET=8e9db1a9046ed6baa814234f01eb7c38f1c28199e9817b261507093278557cf9
ENVEOF

echo "✅ .env обновлен (без export)"
echo ""

# Также создаем .env.systemd для systemd (без export)
echo "Создаем .env.systemd для systemd..."
cat > .env.systemd << 'ENVEOF'
DB_PASSWORD=Bcnbyf293!
JWT_SECRET=8e9db1a9046ed6baa814234f01eb7c38f1c28199e9817b261507093278557cf9
ENVEOF

echo "✅ .env.systemd создан"
echo ""

# Обновляем systemd service файл
echo "Обновляем systemd service..."
sudo tee /etc/systemd/system/trust-the-route-backend.service > /dev/null << 'EOF'
[Unit]
Description=Trust The Route Backend API
After=network.target postgresql.service

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/home/ubuntu/trust-the-route-backend/backend
EnvironmentFile=/home/ubuntu/trust-the-route-backend/backend/.env
Environment="DB_PASSWORD=Bcnbyf293!"
Environment="JWT_SECRET=8e9db1a9046ed6baa814234f01eb7c38f1c28199e9817b261507093278557cf9"
ExecStart=/home/ubuntu/trust-the-route-backend/backend/gradlew run --no-daemon
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
sudo systemctl daemon-reload

echo "✅ Systemd перезагружен"
echo ""

# Перезапускаем service
echo "Перезапуск service..."
sudo systemctl restart trust-the-route-backend

sleep 5

# Проверяем статус
echo ""
echo "Статус service:"
sudo systemctl status trust-the-route-backend --no-pager | head -20

echo ""
echo "Логи:"
tail -20 app.log
