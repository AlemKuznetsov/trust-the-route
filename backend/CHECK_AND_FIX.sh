#!/bin/bash
# Проверка и исправление файлов на сервере

cd ~/trust-the-route-backend/backend

echo "=== Проверка Application.kt ==="
head -30 src/main/kotlin/com/trusttheroute/backend/Application.kt | grep -E "(import|json)"

echo -e "\n=== Проверка зависимостей ==="
./gradlew dependencies --configuration compileClasspath | grep -E "(ktor|serialization)" | head -10

echo -e "\n=== Проверка User.kt ==="
head -15 src/main/kotlin/com/trusttheroute/backend/models/User.kt

echo -e "\n=== Попытка сборки с подробным выводом ==="
./gradlew clean build --stacktrace 2>&1 | head -100
