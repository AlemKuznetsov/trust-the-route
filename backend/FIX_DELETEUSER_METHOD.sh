#!/bin/bash
# Исправление метода deleteUser на сервере

cd ~/trust-the-route-backend/backend

echo "=== Исправление метода deleteUser ==="
echo ""

# Проверяем текущую строку 197
echo "Текущая строка 197:"
sed -n '197p' src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt

echo ""
echo "Текущий метод deleteUser (строки 193-204):"
sed -n '193,204p' src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt

echo ""
echo "=== Исправление ==="

# Создаем временный файл с исправленным методом
cat > /tmp/deleteUser_fixed.kt << 'EOF'
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

# Заменяем метод deleteUser в файле
# Находим начало метода (строка с "fun deleteUser")
START_LINE=$(grep -n "fun deleteUser" src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt | cut -d: -f1)
END_LINE=$((START_LINE + 11))  # Метод занимает примерно 12 строк

echo "Метод deleteUser начинается на строке $START_LINE"

# Создаем новый файл без старого метода deleteUser
head -n $((START_LINE - 1)) src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt > /tmp/UserRepository_new.kt
# Добавляем исправленный метод
cat /tmp/deleteUser_fixed.kt >> /tmp/UserRepository_new.kt
# Добавляем закрывающую скобку класса если нужно
echo "}" >> /tmp/UserRepository_new.kt

# Проверяем результат
echo ""
echo "Проверка исправленного метода:"
tail -15 /tmp/UserRepository_new.kt

echo ""
echo "=== Альтернативный способ: прямая замена через sed ==="

# Проще: заменим проблемную строку напрямую
# Проверяем, что там на строке 197
if grep -q "val deletedRows = Users.deleteWhere" src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt; then
    echo "Найдена строка с deleteWhere"
    # Проверяем следующую строку
    LINE_198=$(sed -n '198p' src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt)
    echo "Строка 198: $LINE_198"
    
    # Если строка 198 не содержит "deletedRows > 0", исправим
    if ! echo "$LINE_198" | grep -q "deletedRows > 0"; then
        echo "Исправление строки 198..."
        sed -i '198s/.*/                deletedRows > 0/' src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt
    fi
fi

echo ""
echo "=== Проверка после исправления ==="
sed -n '193,204p' src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt

echo ""
echo "=== Попытка компиляции ==="
./gradlew compileKotlin --no-daemon 2>&1 | grep -E "error:|BUILD" | head -10
