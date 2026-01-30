#!/bin/bash
# Управление приложением

cd ~/trust-the-route-backend/backend

case "$1" in
    start)
        if pgrep -f "gradlew run" > /dev/null; then
            echo "⚠️  Приложение уже запущено (PID: $(pgrep -f 'gradlew run'))"
        else
            echo "Запуск приложения..."
            if [ -f .env ]; then
                source .env
            fi
            nohup ./gradlew run --no-daemon > app.log 2>&1 &
            APP_PID=$!
            echo "Приложение запускается (PID: $APP_PID)"
            echo "Подождите 10 секунд для полного запуска..."
            sleep 10
            if ps -p $APP_PID > /dev/null 2>&1; then
                echo "✅ Приложение запущено успешно!"
                echo "Логи: tail -f app.log"
            else
                echo "❌ Приложение не запустилось. Проверьте логи: tail -50 app.log"
            fi
        fi
        ;;
    stop)
        if pgrep -f "gradlew run" > /dev/null; then
            PID=$(pgrep -f "gradlew run")
            echo "Остановка приложения (PID: $PID)..."
            pkill -f "gradlew run"
            sleep 2
            echo "✅ Приложение остановлено"
        else
            echo "⚠️  Приложение не запущено"
        fi
        ;;
    restart)
        echo "Перезапуск приложения..."
        pkill -f "gradlew run" 2>/dev/null
        sleep 2
        if [ -f .env ]; then
            source .env
        fi
        nohup ./gradlew run --no-daemon > app.log 2>&1 &
        APP_PID=$!
        echo "Приложение перезапускается (PID: $APP_PID)"
        sleep 10
        if ps -p $APP_PID > /dev/null 2>&1; then
            echo "✅ Приложение перезапущено успешно!"
        else
            echo "❌ Ошибка перезапуска. Проверьте логи: tail -50 app.log"
        fi
        ;;
    status)
        if pgrep -f "gradlew run" > /dev/null; then
            PID=$(pgrep -f "gradlew run")
            echo "✅ Приложение запущено (PID: $PID)"
            echo ""
            echo "Последние строки лога:"
            tail -5 app.log
            echo ""
            echo "Проверка API:"
            if curl -s http://localhost:8080 > /dev/null 2>&1; then
                echo "✅ API отвечает на порту 8080"
            else
                echo "⚠️  API не отвечает"
            fi
        else
            echo "❌ Приложение не запущено"
        fi
        ;;
    logs)
        if [ -f app.log ]; then
            tail -${2:-50} app.log
        else
            echo "Файл логов не найден"
        fi
        ;;
    *)
        echo "Использование: $0 {start|stop|restart|status|logs [количество_строк]}"
        echo ""
        echo "Команды:"
        echo "  start   - Запустить приложение"
        echo "  stop    - Остановить приложение"
        echo "  restart - Перезапустить приложение"
        echo "  status  - Показать статус приложения"
        echo "  logs    - Показать логи (по умолчанию 50 строк)"
        exit 1
        ;;
esac
