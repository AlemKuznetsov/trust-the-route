#!/bin/bash
# Исправление UserRepository.kt на сервере

cd ~/trust-the-route-backend/backend

echo "=== Исправление UserRepository.kt ==="
echo ""

# Проверяем импорты
echo "Проверка импортов..."
if ! grep -q "import org.jetbrains.exposed.sql.kotlin.datetime.now" src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt; then
    echo "Добавление импорта now..."
    # Добавляем импорт после строки с другими импортами
    sed -i '/import org.jetbrains.exposed.sql.transactions.transaction/a import org.jetbrains.exposed.sql.kotlin.datetime.now' src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt
fi

# Проверяем метод deleteUser
echo "Проверка метода deleteUser..."
if grep -q "Users.deleteWhere" src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt; then
    echo "Метод deleteUser найден"
    # Проверяем правильность синтаксиса
    if grep -q "val deletedRows = Users.deleteWhere" src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt; then
        echo "Синтаксис правильный"
    else
        echo "⚠️  Возможна проблема с синтаксисом"
    fi
fi

echo ""
echo "=== Пересборка ==="
sudo systemctl stop trust-the-route-backend
sleep 2

sudo lsof -ti:8080 | xargs -r sudo kill -9 2>/dev/null
ps aux | grep -E "gradlew|java.*backend" | grep -v grep | awk '{print $2}' | xargs -r sudo kill -9 2>/dev/null
sleep 2

echo "Очистка и пересборка..."
./gradlew clean build --no-daemon 2>&1 | tail -30

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Сборка успешна!"
    echo ""
    echo "Запуск service..."
    sudo systemctl start trust-the-route-backend
    sleep 5
    sudo systemctl status trust-the-route-backend --no-pager | head -20
else
    echo ""
    echo "❌ Ошибка сборки!"
    echo ""
    echo "Проверьте ошибки выше"
fi
