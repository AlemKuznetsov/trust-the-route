#!/bin/bash
# Исправление конфликта портов и окончательная настройка service

cd ~/trust-the-route-backend/backend

echo "=== 1. Останавливаем service ==="
sudo systemctl stop trust-the-route-backend
sleep 2

echo "=== 2. Проверяем процессы на порту 8080 ==="
PID_8080=$(sudo lsof -ti:8080)
if [ ! -z "$PID_8080" ]; then
    echo "⚠️  Найден процесс на порту 8080: PID=$PID_8080"
    echo "Останавливаем процесс..."
    sudo kill -9 $PID_8080
    sleep 2
else
    echo "✅ Порт 8080 свободен"
fi

echo ""
echo "=== 3. Проверяем все Java процессы ==="
JAVA_PIDS=$(ps aux | grep java | grep -v grep | awk '{print $2}')
if [ ! -z "$JAVA_PIDS" ]; then
    echo "⚠️  Найдены Java процессы:"
    ps aux | grep java | grep -v grep
    echo "Останавливаем все Java процессы связанные с backend..."
    echo "$JAVA_PIDS" | xargs -r sudo kill -9
    sleep 2
else
    echo "✅ Нет запущенных Java процессов"
fi

echo ""
echo "=== 4. Исправляем .env файл (убираем export) ==="
cat > .env << 'EOF'
DB_PASSWORD=Bcnbyf293!
JWT_SECRET=8e9db1a9046ed6baa814234f01eb7c38f1c28199e9817b261507093278557cf9
EOF
echo "✅ .env обновлен"

echo ""
echo "=== 5. Обновляем systemd service БЕЗ EnvironmentFile ==="
sudo tee /etc/systemd/system/trust-the-route-backend.service > /dev/null << 'EOF'
[Unit]
Description=Trust The Route Backend API
After=network.target postgresql.service

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/home/ubuntu/trust-the-route-backend/backend
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

echo "✅ Service файл обновлен (БЕЗ EnvironmentFile)"

echo ""
echo "=== 6. Перезагружаем systemd ==="
sudo systemctl daemon-reload

echo ""
echo "=== 7. Проверяем, что порт свободен ==="
if sudo lsof -ti:8080 > /dev/null 2>&1; then
    echo "⚠️  Порт 8080 все еще занят!"
    sudo lsof -i:8080
else
    echo "✅ Порт 8080 свободен"
fi

echo ""
echo "=== 8. Запускаем service ==="
sudo systemctl start trust-the-route-backend

sleep 5

echo ""
echo "=== 9. Проверяем статус ==="
sudo systemctl status trust-the-route-backend --no-pager | head -25

echo ""
echo "=== 10. Проверяем порт 8080 ==="
if sudo netstat -tlnp | grep 8080; then
    echo "✅ Порт 8080 слушается!"
else
    echo "⚠️  Порт 8080 не слушается"
fi

echo ""
echo "=== 11. Последние логи ==="
tail -30 app.log 2>/dev/null || echo "app.log пуст"
