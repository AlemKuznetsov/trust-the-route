#!/bin/bash
# Создание исправленного UserRepository.kt на сервере

cd ~/trust-the-route-backend/backend

echo "=== Создание исправленного UserRepository.kt ==="
echo ""

# Создаем резервную копию
cp src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt.backup.$(date +%Y%m%d_%H%M%S)

# Проверяем версию Exposed
echo "Проверка версии Exposed в build.gradle.kts..."
grep -i "exposed" build.gradle.kts | head -5

echo ""
echo "Попытка компиляции с подробным выводом ошибок..."
./gradlew compileKotlin --no-daemon --stacktrace 2>&1 | grep -A 10 "error:" | head -30

echo ""
echo "Если ошибки остались, проверьте:"
echo "1. Версию Exposed в build.gradle.kts"
echo "2. Что импорт now добавлен правильно"
echo "3. Что синтаксис deleteWhere правильный для вашей версии Exposed"
