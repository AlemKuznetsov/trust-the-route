#!/bin/bash
# Исправление сериализации во всех файлах

cd ~/trust-the-route-backend/backend

echo "=== Исправление сериализации ==="
echo ""

# Исправляем User.kt
echo "1. Обновляем User.kt..."
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

# Исправляем AuthRoutes.kt
echo "2. Обновляем AuthRoutes.kt..."
cat > src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt << 'ROUTESEOF'
package com.trusttheroute.backend.routes.auth

import com.trusttheroute.backend.models.*
import com.trusttheroute.backend.repositories.UserRepository
import com.trusttheroute.backend.utils.JwtUtils
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Application.configureAuthRoutes() {
    val userRepository = UserRepository()
    
    routing {
        route("/api/v1/auth") {
            post("/register") {
                try {
                    val request = call.receive<RegisterRequest>()
                    
                    if (request.email.isBlank() || request.password.isBlank() || request.name.isBlank()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("Email, password, and name are required")
                        )
                        return@post
                    }
                    
                    if (userRepository.userExists(request.email)) {
                        call.respond(
                            HttpStatusCode.Conflict,
                            ErrorResponse("User with this email already exists")
                        )
                        return@post
                    }
                    
                    val user = userRepository.createUser(
                        email = request.email,
                        password = request.password,
                        name = request.name
                    )
                    
                    if (user != null) {
                        val token = JwtUtils.generateToken(user.id, user.email)
                        call.respond(
                            HttpStatusCode.Created,
                            AuthResponse(
                                user = user.copy(token = token),
                                token = token
                            )
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            ErrorResponse("Failed to create user")
                        )
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(e.message ?: "Unknown error")
                    )
                }
            }
            
            post("/login") {
                try {
                    val request = call.receive<LoginRequest>()
                    
                    if (request.email.isBlank() || request.password.isBlank()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("Email and password are required")
                        )
                        return@post
                    }
                    
                    val user = userRepository.verifyPassword(request.email, request.password)
                    
                    if (user != null) {
                        val token = JwtUtils.generateToken(user.id, user.email)
                        call.respond(
                            HttpStatusCode.OK,
                            AuthResponse(
                                user = user.copy(token = token),
                                token = token
                            )
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            ErrorResponse("Invalid email or password")
                        )
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(e.message ?: "Unknown error")
                    )
                }
            }
            
            post("/reset-password") {
                try {
                    val request = call.receive<ResetPasswordRequest>()
                    
                    if (request.email.isBlank()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("Email is required")
                        )
                        return@post
                    }
                    
                    if (!userRepository.userExists(request.email)) {
                        call.respond(
                            HttpStatusCode.OK,
                            MessageResponse("If the email exists, a password reset link has been sent")
                        )
                        return@post
                    }
                    
                    call.respond(
                        HttpStatusCode.OK,
                        MessageResponse("If the email exists, a password reset link has been sent")
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(e.message ?: "Unknown error")
                    )
                }
            }
        }
    }
}

@Serializable
data class ErrorResponse(val error: String)

@Serializable
data class MessageResponse(val message: String)
ROUTESEOF

echo "✅ Файлы обновлены"
echo ""
echo "Пересобираем проект..."
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
    echo "  sleep 3"
    echo "  tail -20 app.log"
else
    echo ""
    echo "❌ Ошибка сборки. Проверьте вывод выше."
fi
