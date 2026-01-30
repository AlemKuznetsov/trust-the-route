#!/bin/bash
# Экспорт пользователей в CSV для Excel

cd ~/trust-the-route-backend/backend

echo "=== Экспорт пользователей в CSV ==="

# Экспорт в CSV с заголовками
sudo -u postgres psql -d trust_the_route -c "\COPY (SELECT id, email, name, created_at, updated_at FROM users ORDER BY created_at DESC) TO '/tmp/users_export.csv' WITH CSV HEADER;"

# Проверяем файл
echo ""
echo "=== Содержимое файла ==="
cat /tmp/users_export.csv

echo ""
echo "=== Файл создан: /tmp/users_export.csv ==="
echo "Для скачивания используйте:"
echo "  scp ubuntu@158.160.217.181:/tmp/users_export.csv ."
echo ""
echo "Или скопируйте содержимое выше и вставьте в Excel"
