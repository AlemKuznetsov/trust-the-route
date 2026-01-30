#!/bin/bash
# Простой скрипт для удаления дубликатов из User.kt

cd ~/trust-the-route-backend/backend

echo "=== Удаление дубликатов из User.kt ==="
echo ""

# Создаем правильную версию User.kt без ErrorResponse и MessageResponse
cat > src/main/kotlin/com/trusttheroute/backend/models/User.kt << 'USER_EOF'
package com.trusttheroute.backend.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import java.util.*
import kotlinx.serialization.Serializable

object Users : Table("users") {
    val id = uuid("id")
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val name = varchar("name", 255).nullable()
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    
    override val primaryKey = PrimaryKey(id, name = "users_pkey")
}

@Serializable
data class User(
    val id: String,
    val email: String,
    val name: String,
    val token: String? = null
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class ResetPasswordRequest(
    val email: String
)

@Serializable
data class UpdateProfileRequest(
    val name: String
)

@Serializable
data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)

@Serializable
data class DeleteAccountRequest(
    val confirmationText: String
)

@Serializable
data class YandexAuthRequest(
    val yandexToken: String,
    val email: String,
    val name: String
)

@Serializable
data class AuthResponse(
    val user: User,
    val token: String
)
USER_EOF

echo "✅ User.kt исправлен (дубликаты удалены)"
echo ""

# Проверяем, что ApiResponses.kt существует
if [ ! -f "src/main/kotlin/com/trusttheroute/backend/models/ApiResponses.kt" ]; then
    echo "❌ ApiResponses.kt не найден!"
    echo "Создаем ApiResponses.kt..."
    
    cat > src/main/kotlin/com/trusttheroute/backend/models/ApiResponses.kt << 'API_EOF'
package com.trusttheroute.backend.models

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val error: String
)

@Serializable
data class MessageResponse(
    val message: String
)
API_EOF
    
    echo "✅ ApiResponses.kt создан"
fi

echo ""
echo "=== Проверка ==="
echo "Определения ErrorResponse:"
grep -r "data class ErrorResponse" src/main/kotlin/com/trusttheroute/backend/models/ | wc -l
echo "Определения MessageResponse:"
grep -r "data class MessageResponse" src/main/kotlin/com/trusttheroute/backend/models/ | wc -l
echo ""
echo "✅ Готово! Теперь можно собрать проект:"
echo "   ./gradlew clean build"
