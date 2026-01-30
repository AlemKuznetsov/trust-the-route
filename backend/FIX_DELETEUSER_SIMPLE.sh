#!/bin/bash
# Простое исправление метода deleteUser через прямой SQL

cd ~/trust-the-route-backend/backend

echo "=== Исправление метода deleteUser ==="
echo ""

# Находим строки метода deleteUser
START_LINE=$(grep -n "fun deleteUser" src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt | cut -d: -f1)

echo "Метод deleteUser начинается на строке $START_LINE"
echo ""

# Исправляем метод напрямую через sed
sed -i "${START_LINE},$((START_LINE + 11))d" src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt

# Вставляем исправленный метод
cat > /tmp/deleteUser_insert.kt << 'EOF'
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

# Вставляем исправленный метод на место удаленного
sed -i "${START_LINE}r /tmp/deleteUser_insert.kt" src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt

echo "✅ Метод исправлен"
echo ""
echo "Проверка:"
sed -n "${START_LINE},$((START_LINE + 12))p" src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt
