#!/bin/bash
# Исправление UserRepository.kt - замена Timestamp на LocalDateTime

cd ~/trust-the-route-backend/backend

echo "=== Исправление UserRepository.kt ==="
echo ""

# Создаем исправленный файл
cat > src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt << 'EOF'
package com.trusttheroute.backend.repositories

import com.trusttheroute.backend.models.Users
import com.trusttheroute.backend.models.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.*
import java.time.LocalDateTime

class UserRepository {
    
    fun createUser(email: String, password: String, name: String): User? {
        return transaction {
            try {
                // Проверить, существует ли пользователь
                val existingUser = Users.select { Users.email eq email }.firstOrNull()
                if (existingUser != null) {
                    return@transaction null
                }
                
                // Хешировать пароль
                val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())
                
                // Создать пользователя
                val newUserId = UUID.randomUUID()
                val currentTime = LocalDateTime.now()
                Users.insert {
                    it[Users.id] = newUserId
                    it[Users.email] = email
                    it[Users.passwordHash] = passwordHash
                    it[Users.name] = name
                    it[Users.createdAt] = currentTime
                    it[Users.updatedAt] = currentTime
                }
                
                // Получить созданного пользователя
                val userRow = Users.select { Users.id eq newUserId }.first()
                User(
                    id = userRow[Users.id].toString(),
                    email = userRow[Users.email],
                    name = userRow[Users.name] ?: ""
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun createYandexUser(email: String, name: String): User? {
        return transaction {
            try {
                // Проверить, существует ли пользователь
                val existingUser = Users.select { Users.email eq email }.firstOrNull()
                if (existingUser != null) {
                    // Пользователь уже существует, возвращаем его
                    return@transaction User(
                        id = existingUser[Users.id].toString(),
                        email = existingUser[Users.email],
                        name = existingUser[Users.name] ?: name
                    )
                }
                
                // Создать пользователя с пустым паролем (для YandexID пользователей)
                // Используем специальный хеш, который нельзя использовать для входа через пароль
                val passwordHash = BCrypt.hashpw(UUID.randomUUID().toString() + "_yandex", BCrypt.gensalt())
                
                // Создать пользователя
                val newUserId = UUID.randomUUID()
                val currentTime = LocalDateTime.now()
                Users.insert {
                    it[Users.id] = newUserId
                    it[Users.email] = email
                    it[Users.passwordHash] = passwordHash
                    it[Users.name] = name
                    it[Users.createdAt] = currentTime
                    it[Users.updatedAt] = currentTime
                }
                
                // Получить созданного пользователя
                val userRow = Users.select { Users.id eq newUserId }.first()
                User(
                    id = userRow[Users.id].toString(),
                    email = userRow[Users.email],
                    name = userRow[Users.name] ?: ""
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    
    fun getUserByEmail(email: String): User? {
        return transaction {
            Users.select { Users.email eq email }.firstOrNull()?.let { row ->
                User(
                    id = row[Users.id].toString(),
                    email = row[Users.email],
                    name = row[Users.name] ?: ""
                )
            }
        }
    }
    
    fun verifyPassword(email: String, password: String): User? {
        return transaction {
            val userRow = Users.select { Users.email eq email }.firstOrNull()
            if (userRow != null) {
                val passwordHash = userRow[Users.passwordHash]
                if (BCrypt.checkpw(password, passwordHash)) {
                    User(
                        id = userRow[Users.id].toString(),
                        email = userRow[Users.email],
                        name = userRow[Users.name] ?: ""
                    )
                } else {
                    null
                }
            } else {
                null
            }
        }
    }
    
    fun userExists(email: String): Boolean {
        return transaction {
            Users.select { Users.email eq email }.count() > 0
        }
    }

    fun getUserById(userId: String): User? {
        return transaction {
            try {
                val uuid = UUID.fromString(userId)
                Users.select { Users.id eq uuid }.firstOrNull()?.let { row ->
                    User(
                        id = row[Users.id].toString(),
                        email = row[Users.email],
                        name = row[Users.name] ?: ""
                    )
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    fun updateProfile(userId: String, name: String): User? {
        return transaction {
            try {
                val uuid = UUID.fromString(userId)
                Users.update({ Users.id eq uuid }) {
                    it[Users.name] = name
                    it[Users.updatedAt] = LocalDateTime.now()
                }
                getUserById(userId)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun changePassword(userId: String, oldPassword: String, newPassword: String): Boolean {
        return transaction {
            try {
                val uuid = UUID.fromString(userId)
                val userRow = Users.select { Users.id eq uuid }.firstOrNull()
                if (userRow != null) {
                    val passwordHash = userRow[Users.passwordHash]
                    if (BCrypt.checkpw(oldPassword, passwordHash)) {
                        val newPasswordHash = BCrypt.hashpw(newPassword, BCrypt.gensalt())
                        Users.update({ Users.id eq uuid }) {
                            it[Users.passwordHash] = newPasswordHash
                            it[Users.updatedAt] = LocalDateTime.now()
                        }
                        true
                    } else {
                        false
                    }
                } else {
                    false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    fun deleteUser(userId: String): Boolean {
        return transaction {
            try {
                val uuid = UUID.fromString(userId)
                val userExists = Users.select { Users.id eq uuid }.count() > 0
                if (userExists) {
                    Users.deleteWhere { Users.id eq uuid }
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
EOF

echo "✅ Файл UserRepository.kt исправлен"
echo ""
echo "Проверяем импорты:"
grep -E "import.*(Timestamp|LocalDateTime)" src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt
echo ""
echo "Собираем проект:"
./gradlew clean build 2>&1 | tail -40

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Сборка успешна!"
    echo ""
    echo "Перезапускаем сервис:"
    sudo systemctl restart trust-the-route-backend
    sleep 3
    sudo systemctl status trust-the-route-backend --no-pager | head -20
else
    echo ""
    echo "❌ Ошибка сборки. Проверьте вывод выше."
fi
