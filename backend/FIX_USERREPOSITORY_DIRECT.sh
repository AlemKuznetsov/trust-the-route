#!/bin/bash
# Прямое исправление UserRepository.kt на сервере

cd ~/trust-the-route-backend/backend

echo "=== Прямое исправление UserRepository.kt ==="
echo ""

# Создаем резервную копию
cp src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt.backup

# Проверяем импорт now
if ! grep -q "import org.jetbrains.exposed.sql.kotlin.datetime.now" src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt; then
    echo "Добавление импорта now..."
    # Находим строку с импортом transaction и добавляем после нее
    sed -i '/import org.jetbrains.exposed.sql.transactions.transaction/a import org.jetbrains.exposed.sql.kotlin.datetime.now' src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt
fi

# Проверяем, что импорт добавлен
echo ""
echo "Проверка импортов:"
grep "import.*now" src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt

# Проверяем использование now() в файле
echo ""
echo "Проверка использования now():"
grep -n "now()" src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt

# Проверяем метод deleteUser
echo ""
echo "Проверка метода deleteUser (строка 197):"
sed -n '193,204p' src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt

echo ""
echo "=== Попытка компиляции ==="
./gradlew compileKotlin --no-daemon 2>&1 | grep -A 5 "error:" | head -20

echo ""
echo "Если ошибки остались, проверьте файл вручную:"
echo "  nano src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt"
