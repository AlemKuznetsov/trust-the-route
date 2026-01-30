#!/bin/bash
# Полное исправление User.kt

cd ~/trust-the-route-backend/backend

echo "=== Исправление User.kt ==="
echo ""

# Пересоздаем файл полностью
cat > src/main/kotlin/com/trusttheroute/backend/models/User.kt << 'USEREOF'
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
data class AuthResponse(
    val user: User,
    val token: String
)
USEREOF

echo "✅ Файл User.kt пересоздан"
echo ""
echo "Проверяем файл:"
head -10 src/main/kotlin/com/trusttheroute/backend/models/User.kt
echo "..."
tail -5 src/main/kotlin/com/trusttheroute/backend/models/User.kt
echo ""
echo "Пересобираем..."
./gradlew clean build

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Сборка успешна!"
    echo ""
    echo "Перезапустите приложение:"
    echo "  pkill -f 'gradlew run'"
    echo "  sleep 2"
    echo "  source .env"
    echo "  nohup ./gradlew run > app.log 2>&1 &"
    echo "  sleep 5"
    echo "  curl http://localhost:8080/api/v1/auth/register -X POST -H 'Content-Type: application/json' -d '{\"email\":\"test@test.com\",\"password\":\"test123\",\"name\":\"Test\"}'"
else
    echo ""
    echo "❌ Ошибка сборки. Проверьте вывод выше."
fi
