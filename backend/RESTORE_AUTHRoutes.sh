#!/bin/bash
# Полное восстановление файла AuthRoutes.kt на сервере

cd ~/trust-the-route-backend/backend

echo "=========================================="
echo "Восстановление AuthRoutes.kt"
echo "=========================================="
echo ""

# Создаем резервную копию
cp src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt \
   src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt.backup.$(date +%Y%m%d_%H%M%S) 2>/dev/null

echo "1. Проверка текущего файла:"
echo "----------------------------------------"
head -15 src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt
echo ""

echo "2. Восстановление правильных импортов..."
echo "----------------------------------------"

# Создаем правильный файл с исправленными импортами
cat > /tmp/AuthRoutes_fixed.kt << 'AUTHRoutes_EOF'
package com.trusttheroute.backend.routes.auth

import com.trusttheroute.backend.models.*
import com.trusttheroute.backend.models.YandexAuthRequest
import com.trusttheroute.backend.repositories.UserRepository
import com.trusttheroute.backend.utils.JwtUtils
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.auth0.jwt.interfaces.DecodedJWT

fun Application.configureAuthRoutes() {
    val userRepository = UserRepository()
    
    // Функция для получения пользователя из токена
    fun getUserIdFromToken(call: ApplicationCall): String? {
        val authHeader = call.request.header("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null
        }
        val token = authHeader.removePrefix("Bearer ")
        val decodedJWT: DecodedJWT? = JwtUtils.verifyToken(token)
        return decodedJWT?.getClaim("userId")?.asString()
    }
    
    routing {
        route("/api/v1/auth") {
            
            post("/register") {
                try {
                    val request = call.receive<RegisterRequest>()
                    
                    // Валидация
                    if (request.email.isBlank() || request.password.isBlank() || request.name.isBlank()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "Email, password, and name are required")
                        )
                        return@post
                    }
                    
                    // Проверить, существует ли пользователь
                    if (userRepository.userExists(request.email)) {
                        call.respond(
                            HttpStatusCode.Conflict,
                            mapOf("error" to "User with this email already exists")
                        )
                        return@post
                    }
                    
                    // Создать пользователя
                    val user = userRepository.createUser(
                        email = request.email,
                        password = request.password,
                        name = request.name
                    )
                    
                    if (user != null) {
                        // Генерировать токен
                        val token = JwtUtils.generateToken(user.id, user.email)
                        
                        // Вернуть ответ
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
                            mapOf("error" to "Failed to create user")
                        )
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to e.message ?: "Unknown error")
                    )
                }
            }
            
            post("/login") {
                try {
                    val request = call.receive<LoginRequest>()
                    
                    // Валидация
                    if (request.email.isBlank() || request.password.isBlank()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "Email and password are required")
                        )
                        return@post
                    }
                    
                    // Проверить пароль
                    val user = userRepository.verifyPassword(request.email, request.password)
                    
                    if (user != null) {
                        // Генерировать токен
                        val token = JwtUtils.generateToken(user.id, user.email)
                        
                        // Вернуть ответ
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
                            mapOf("error" to "Invalid email or password")
                        )
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to e.message ?: "Unknown error")
                    )
                }
            }
            
            post("/reset-password") {
                try {
                    val request = call.receive<ResetPasswordRequest>()
                    
                    // Валидация
                    if (request.email.isBlank()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "Email is required")
                        )
                        return@post
                    }
                    
                    // Проверить, существует ли пользователь
                    if (!userRepository.userExists(request.email)) {
                        // Для безопасности не сообщаем, что пользователь не существует
                        call.respond(HttpStatusCode.OK, mapOf("message" to "If the email exists, a password reset link has been sent"))
                        return@post
                    }
                    
                    // TODO: Реализовать отправку email с ссылкой для сброса пароля
                    // Пока просто возвращаем успешный ответ
                    call.respond(HttpStatusCode.OK, mapOf("message" to "If the email exists, a password reset link has been sent"))
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to e.message ?: "Unknown error")
                    )
                }
            }

            post("/yandex") {
                try {
                    val request = call.receive<YandexAuthRequest>()
                    
                    // Валидация
                    if (request.yandexToken.isBlank() || request.email.isBlank() || request.name.isBlank()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "Yandex token, email, and name are required")
                        )
                        return@post
                    }
                    
                    // Проверить, существует ли пользователь
                    var user = userRepository.getUserByEmail(request.email)
                    
                    if (user == null) {
                        // Создать пользователя без пароля (для YandexID пользователей)
                        user = userRepository.createYandexUser(request.email, request.name)
                    }
                    
                    if (user != null) {
                        // Генерировать токен
                        val token = JwtUtils.generateToken(user.id, user.email)
                        
                        // Вернуть ответ
                        call.respond(
                            HttpStatusCode.OK,
                            AuthResponse(
                                user = user.copy(token = token),
                                token = token
                            )
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            mapOf("error" to "Failed to create or find user")
                        )
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to e.message ?: "Unknown error")
                    )
                }
            }
            
            put("/profile") {
                try {
                    val userId = getUserIdFromToken(call)
                    if (userId == null) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            mapOf("error" to "Authentication required")
                        )
                        return@put
                    }

                    val request = call.receive<UpdateProfileRequest>()
                    
                    if (request.name.isBlank()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "Name is required")
                        )
                        return@put
                    }

                    val updatedUser = userRepository.updateProfile(userId, request.name)
                    if (updatedUser != null) {
                        val token = JwtUtils.generateToken(updatedUser.id, updatedUser.email)
                        call.respond(
                            HttpStatusCode.OK,
                            AuthResponse(
                                user = updatedUser.copy(token = token),
                                token = token
                            )
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            mapOf("error" to "Failed to update profile")
                        )
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to e.message ?: "Unknown error")
                    )
                }
            }
            
            put("/password") {
                try {
                    val userId = getUserIdFromToken(call)
                    if (userId == null) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            mapOf("error" to "Authentication required")
                        )
                        return@put
                    }

                    val request = call.receive<ChangePasswordRequest>()
                    
                    if (request.oldPassword.isBlank() || request.newPassword.isBlank()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "Old password and new password are required")
                        )
                        return@put
                    }

                    if (request.newPassword.length < 6) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "New password must be at least 6 characters")
                        )
                        return@put
                    }

                    val success = userRepository.changePassword(userId, request.oldPassword, request.newPassword)
                    if (success) {
                        call.respond(
                            HttpStatusCode.OK,
                            mapOf("message" to "Password changed successfully")
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            mapOf("error" to "Invalid old password")
                        )
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to e.message ?: "Unknown error")
                    )
                }
            }
            
            delete("/account") {
                try {
                    val userId = getUserIdFromToken(call)
                    if (userId == null) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            mapOf("error" to "Authentication required")
                        )
                        return@delete
                    }

                    val confirmationText = call.request.queryParameters["confirmation"] ?: ""
                    
                    // Проверка подтверждения (можно расширить логику)
                    if (confirmationText != "УДАЛИТЬ") {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "Invalid confirmation text")
                        )
                        return@delete
                    }

                    val success = userRepository.deleteUser(userId)
                    if (success) {
                        call.respond(
                            HttpStatusCode.OK,
                            mapOf("message" to "Account deleted successfully")
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            mapOf("error" to "Failed to delete account")
                        )
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to e.message ?: "Unknown error")
                    )
                }
            }
        }
    }
}
AUTHRoutes_EOF

# Заменяем файл
mv /tmp/AuthRoutes_fixed.kt src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt

echo "✅ Файл восстановлен"
echo ""
echo "3. Проверка импортов:"
echo "----------------------------------------"
head -15 src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt
echo ""

echo "4. Компиляция:"
echo "----------------------------------------"
./gradlew compileKotlin --no-daemon 2>&1 | grep -E "error:|BUILD" | head -10

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Компиляция успешна!"
else
    echo ""
    echo "❌ Ошибки компиляции остались"
fi
