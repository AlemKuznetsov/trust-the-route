#!/bin/bash
# Скрипт для создания всех файлов проекта на сервере

# Создать структуру директорий
mkdir -p ~/trust-the-route-backend/backend/src/main/kotlin/com/trusttheroute/backend/{config,routes/auth,models,repositories,utils}
mkdir -p ~/trust-the-route-backend/backend/src/main/resources
mkdir -p ~/trust-the-route-backend/backend/gradle/wrapper

cd ~/trust-the-route-backend/backend

echo "Структура директорий создана!"
