#!/bin/bash
# Финальное исправление метода deleteUser с использованием прямого SQL

cd ~/trust-the-route-backend/backend

echo "=== Исправление метода deleteUser ==="
echo ""

# Создаем резервную копию
cp src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt \
   src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt.backup.$(date +%Y%m%d_%H%M%S)

# Находим строки метода deleteUser
START_LINE=$(grep -n "fun deleteUser" src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt | cut -d: -f1)
END_LINE=$((START_LINE + 11))

echo "Метод deleteUser начинается на строке $START_LINE"
echo ""

# Создаем исправленный метод с использованием прямого SQL
cat > /tmp/deleteUser_fixed.kt << 'EOF'
    fun deleteUser(userId: String): Boolean {
        return transaction {
            try {
                val uuid = UUID.fromString(userId)
                val query = "DELETE FROM ${Users.tableName} WHERE ${Users.id.name} = ?"
                val deletedRows = exec(query, listOf(uuid)) {
                    it.executeUpdate()
                }
                deletedRows > 0
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
EOF

# Создаем новый файл
head -n $((START_LINE - 1)) src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt > /tmp/UserRepository_new.kt
cat /tmp/deleteUser_fixed.kt >> /tmp/UserRepository_new.kt
echo "}" >> /tmp/UserRepository_new.kt

# Заменяем файл
mv /tmp/UserRepository_new.kt src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt

echo "✅ Метод исправлен"
echo ""
echo "Проверка исправленного метода:"
sed -n "${START_LINE},$((START_LINE + 12))p" src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt
echo ""

echo "=== Компиляция ==="
./gradlew compileKotlin --no-daemon 2>&1 | tee /tmp/compile.log

if grep -q "error:" /tmp/compile.log; then
    echo ""
    echo "❌ Ошибки компиляции:"
    grep "error:" /tmp/compile.log | head -5
    echo ""
    echo "Восстанавливаем резервную копию..."
    cp src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt.backup.* src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt 2>/dev/null
    exit 1
fi

echo ""
echo "✅ Компиляция успешна!"
