#!/bin/bash
# Пошаговое исправление метода deleteUser на сервере

cd ~/trust-the-route-backend/backend

echo "=========================================="
echo "ШАГ 1: Проверка текущего состояния"
echo "=========================================="
echo ""
echo "Текущий метод deleteUser:"
sed -n '194,205p' src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt
echo ""

echo "=========================================="
echo "ШАГ 2: Создание резервной копии"
echo "=========================================="
cp src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt \
   src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt.backup.$(date +%Y%m%d_%H%M%S)
echo "✅ Резервная копия создана"
echo ""

echo "=========================================="
echo "ШАГ 3: Остановка сервиса"
echo "=========================================="
sudo systemctl stop trust-the-route-backend
sleep 2
echo "✅ Сервис остановлен"
echo ""

echo "=========================================="
echo "ШАГ 4: Очистка процессов"
echo "=========================================="
sudo lsof -ti:8080 | xargs -r sudo kill -9 2>/dev/null
ps aux | grep -E "gradlew|java.*backend" | grep -v grep | awk '{print $2}' | xargs -r sudo kill -9 2>/dev/null
sleep 2
echo "✅ Процессы очищены"
echo ""

echo "=========================================="
echo "ШАГ 5: Проверка исправленного метода"
echo "=========================================="
echo "Убедитесь, что метод deleteUser выглядит так:"
echo ""
cat << 'EOF'
    fun deleteUser(userId: String): Boolean {
        return transaction {
            try {
                val uuid = UUID.fromString(userId)
                val deletedRows = Users.deleteWhere { Users.id eq uuid }
                deletedRows > 0
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
EOF
echo ""
read -p "Нажмите Enter для продолжения..."
echo ""

echo "=========================================="
echo "ШАГ 6: Очистка проекта"
echo "=========================================="
./gradlew clean --no-daemon
echo ""

echo "=========================================="
echo "ШАГ 7: Компиляция проекта"
echo "=========================================="
./gradlew compileKotlin --no-daemon 2>&1 | tee /tmp/compile_output.log

if grep -q "error:" /tmp/compile_output.log; then
    echo ""
    echo "❌ ОШИБКИ КОМПИЛЯЦИИ:"
    grep "error:" /tmp/compile_output.log | head -10
    echo ""
    echo "Проверьте файл: /tmp/compile_output.log"
    exit 1
fi

echo ""
echo "✅ Компиляция успешна!"
echo ""

echo "=========================================="
echo "ШАГ 8: Полная сборка проекта"
echo "=========================================="
./gradlew build --no-daemon 2>&1 | tail -20

if [ $? -ne 0 ]; then
    echo ""
    echo "❌ Ошибка сборки!"
    exit 1
fi

echo ""
echo "✅ Сборка успешна!"
echo ""

echo "=========================================="
echo "ШАГ 9: Запуск сервиса"
echo "=========================================="
sudo systemctl start trust-the-route-backend
sleep 3

echo ""
echo "=========================================="
echo "ШАГ 10: Проверка статуса сервиса"
echo "=========================================="
sudo systemctl status trust-the-route-backend --no-pager -l | head -20

echo ""
echo "=========================================="
echo "✅ ГОТОВО!"
echo "=========================================="
echo ""
echo "Если сервис не запустился, проверьте логи:"
echo "  sudo journalctl -u trust-the-route-backend -n 50 --no-pager"
echo ""
