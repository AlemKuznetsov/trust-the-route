#!/bin/bash
# Удаление дублирующихся определений ErrorResponse и MessageResponse из User.kt

cd ~/trust-the-route-backend/backend

echo "=== Исправление дубликатов в User.kt ==="
echo ""

# Проверяем, есть ли дубликаты
if grep -q "data class ErrorResponse" src/main/kotlin/com/trusttheroute/backend/models/User.kt; then
    echo "⚠️  Найдены дубликаты в User.kt, удаляем..."
    
    # Создаем резервную копию
    cp src/main/kotlin/com/trusttheroute/backend/models/User.kt src/main/kotlin/com/trusttheroute/backend/models/User.kt.backup
    
    # Удаляем строки с ErrorResponse и MessageResponse из User.kt
    # Используем sed для удаления строк, начиная с "data class ErrorResponse" до конца файла
    # Но сначала найдем, где заканчивается AuthResponse
    awk '
    /^@Serializable$/ && found_auth == 0 {
        print
        getline
        if ($0 ~ /^data class AuthResponse/) {
            found_auth = 1
            print
            getline
            print
            getline
            print
            # После закрывающей скобки AuthResponse - это конец файла
            if ($0 ~ /^\)$/) {
                print
                exit
            }
        } else {
            print
        }
        next
    }
    found_auth == 1 && /^\)$/ {
        print
        exit
    }
    !/^data class (ErrorResponse|MessageResponse)/ && !/^@Serializable$/ || found_auth == 0 {
        print
    }
    ' src/main/kotlin/com/trusttheroute/backend/models/User.kt.backup > src/main/kotlin/com/trusttheroute/backend/models/User.kt
    
    echo "✅ Дубликаты удалены"
    echo ""
    echo "Проверяем файл User.kt:"
    tail -10 src/main/kotlin/com/trusttheroute/backend/models/User.kt
    echo ""
    
else
    echo "✅ Дубликатов не найдено в User.kt"
fi

# Проверяем, что ApiResponses.kt существует и содержит определения
if [ -f "src/main/kotlin/com/trusttheroute/backend/models/ApiResponses.kt" ]; then
    echo "✅ ApiResponses.kt найден"
    echo ""
    echo "Содержимое ApiResponses.kt:"
    cat src/main/kotlin/com/trusttheroute/backend/models/ApiResponses.kt
    echo ""
else
    echo "❌ ApiResponses.kt не найден! Нужно загрузить файл."
    exit 1
fi

echo "=== Проверка на дубликаты ==="
echo ""

# Проверяем, сколько раз определены классы
error_count=$(grep -r "data class ErrorResponse" src/main/kotlin/com/trusttheroute/backend/models/ | wc -l)
message_count=$(grep -r "data class MessageResponse" src/main/kotlin/com/trusttheroute/backend/models/ | wc -l)

if [ "$error_count" -eq 1 ] && [ "$message_count" -eq 1 ]; then
    echo "✅ ErrorResponse определен 1 раз"
    echo "✅ MessageResponse определен 1 раз"
    echo ""
    echo "=== Готово к сборке ==="
else
    echo "❌ Найдены дубликаты!"
    echo "ErrorResponse определен $error_count раз(а)"
    echo "MessageResponse определен $message_count раз(а)"
    echo ""
    echo "Файлы с определениями:"
    grep -r "data class ErrorResponse" src/main/kotlin/com/trusttheroute/backend/models/ || true
    grep -r "data class MessageResponse" src/main/kotlin/com/trusttheroute/backend/models/ || true
    exit 1
fi
