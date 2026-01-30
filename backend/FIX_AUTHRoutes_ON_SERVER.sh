#!/bin/bash
# Восстановление файла AuthRoutes.kt на сервере

cd ~/trust-the-route-backend/backend

echo "=========================================="
echo "Восстановление AuthRoutes.kt"
echo "=========================================="
echo ""

# Создаем резервную копию
cp src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt \
   src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt.backup.$(date +%Y%m%d_%H%M%S)

echo "1. Проверка текущих импортов:"
echo "----------------------------------------"
head -15 src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt
echo ""

echo "2. Проверка наличия проблемных строк:"
echo "----------------------------------------"
if grep -q "import io.ktor.application" src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt; then
    echo "✅ Импорт application найден"
else
    echo "❌ Импорт application НЕ найден - нужно восстановить"
fi

if grep -q "import io.ktor.request" src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt; then
    echo "✅ Импорт request найден"
else
    echo "❌ Импорт request НЕ найден - нужно восстановить"
fi

if grep -q "import io.ktor.response" src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt; then
    echo "✅ Импорт response найден"
else
    echo "❌ Импорт response НЕ найден - нужно восстановить"
fi

if grep -q "import io.ktor.routing" src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt; then
    echo "✅ Импорт routing найден"
else
    echo "❌ Импорт routing НЕ найден - нужно восстановить"
fi

echo ""
echo "3. Попытка компиляции:"
echo "----------------------------------------"
./gradlew compileKotlin --no-daemon 2>&1 | grep -E "error:|BUILD" | head -10

echo ""
echo "=========================================="
echo "Если есть ошибки, файл нужно восстановить"
echo "=========================================="
