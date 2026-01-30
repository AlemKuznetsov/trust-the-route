#!/bin/bash
# Альтернативное исправление UserRepository.kt - использование java.time вместо now()

cd ~/trust-the-route-backend/backend

echo "=== Альтернативное исправление UserRepository.kt ==="
echo ""

# Создаем резервную копию
cp src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt.backup

# Заменяем импорт now на java.time
echo "Замена импорта now на java.time.Instant..."
sed -i 's/import org.jetbrains.exposed.sql.kotlin.datetime.now/import java.time.Instant\nimport java.sql.Timestamp/' src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt

# Заменяем все использования now() на Timestamp.from(Instant.now())
echo "Замена всех now() на Timestamp.from(Instant.now())..."
sed -i 's/now()/Timestamp.from(Instant.now())/g' src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt

# Проверяем результат
echo ""
echo "Проверка импортов:"
head -10 src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt | grep -E "import|package"

echo ""
echo "Проверка использования now:"
grep -n "now()" src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt || echo "✅ Все now() заменены"

echo ""
echo "=== Попытка компиляции ==="
./gradlew compileKotlin --no-daemon 2>&1 | grep -E "error:|BUILD|SUCCESS" | head -20
