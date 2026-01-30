#!/bin/bash
# Быстрое исправление метода deleteUser - все в одной команде

cd ~/trust-the-route-backend/backend && \
echo "=== Остановка сервиса ===" && \
sudo systemctl stop trust-the-route-backend && \
sleep 2 && \
echo "=== Очистка процессов ===" && \
sudo lsof -ti:8080 | xargs -r sudo kill -9 2>/dev/null && \
ps aux | grep -E "gradlew|java.*backend" | grep -v grep | awk '{print $2}' | xargs -r sudo kill -9 2>/dev/null && \
sleep 2 && \
echo "=== Проверка метода deleteUser ===" && \
if grep -q "val deletedRows = Users.deleteWhere { Users.id eq uuid }" src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt; then
    echo "✅ Метод уже исправлен"
else
    echo "⚠️  Метод нужно исправить вручную"
    echo "Откройте файл: nano src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt"
    echo "И замените метод deleteUser на:"
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
    exit 1
fi && \
echo "=== Очистка проекта ===" && \
./gradlew clean --no-daemon && \
echo "=== Компиляция ===" && \
./gradlew compileKotlin --no-daemon 2>&1 | tee /tmp/compile.log && \
if grep -q "error:" /tmp/compile.log; then
    echo "❌ Ошибки компиляции:"
    grep "error:" /tmp/compile.log
    exit 1
fi && \
echo "=== Сборка ===" && \
./gradlew build --no-daemon 2>&1 | tail -10 && \
echo "=== Запуск сервиса ===" && \
sudo systemctl start trust-the-route-backend && \
sleep 3 && \
echo "=== Статус ===" && \
sudo systemctl status trust-the-route-backend --no-pager -l | head -15 && \
echo "" && \
echo "✅ ГОТОВО! Проверьте статус выше."
