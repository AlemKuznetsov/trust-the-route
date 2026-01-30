#!/bin/bash
# Финальное исправление с добавлением зависимости

cd ~/trust-the-route-backend/backend

echo "=== Финальное исправление ==="
echo ""

# Проверяем, существует ли файл ApiResponses.kt
if [ ! -f src/main/kotlin/com/trusttheroute/backend/models/ApiResponses.kt ]; then
    echo "Создаем ApiResponses.kt..."
    mkdir -p src/main/kotlin/com/trusttheroute/backend/models
    cat > src/main/kotlin/com/trusttheroute/backend/models/ApiResponses.kt << 'RESPONSESEOF'
package com.trusttheroute.backend.models

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val error: String)

@Serializable
data class MessageResponse(val message: String)
RESPONSESEOF
    echo "✅ Файл создан"
else
    echo "✅ Файл ApiResponses.kt уже существует"
fi

# Проверяем содержимое файла
echo ""
echo "Проверяем содержимое ApiResponses.kt:"
cat src/main/kotlin/com/trusttheroute/backend/models/ApiResponses.kt

# Добавляем зависимость kotlinx-serialization-json если её нет
if ! grep -q "kotlinx-serialization-json" build.gradle.kts; then
    echo ""
    echo "Добавляем зависимость kotlinx-serialization-json..."
    sed -i '/ktor-serialization-kotlinx-json/a\    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")' build.gradle.kts
    echo "✅ Зависимость добавлена"
fi

echo ""
echo "Пересобираем проект..."
./gradlew clean build

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Сборка успешна!"
    echo ""
    echo "Останавливаем старое приложение..."
    pkill -f 'gradlew run' 2>/dev/null
    sleep 3
    
    echo "Запускаем новое приложение..."
    if [ -f .env ]; then
        source .env
    fi
    nohup ./gradlew run > app.log 2>&1 &
    sleep 6
    
    echo ""
    echo "Проверяем, что приложение запущено:"
    if pgrep -f "gradlew run" > /dev/null; then
        echo "✅ Приложение запущено"
        echo ""
        echo "Последние строки лога:"
        tail -10 app.log
        echo ""
        echo "Тест API:"
        curl -s http://localhost:8080/api/v1/auth/register -X POST \
          -H "Content-Type: application/json" \
          -d '{"email":"test@test.com","password":"test123","name":"Test"}'
        echo ""
    else
        echo "❌ Приложение не запустилось. Логи:"
        tail -30 app.log
    fi
else
    echo ""
    echo "❌ Ошибка сборки. Проверьте вывод выше."
fi
