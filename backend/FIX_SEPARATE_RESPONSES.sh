#!/bin/bash
# Создаем отдельный файл для ErrorResponse и MessageResponse

cd ~/trust-the-route-backend/backend

echo "=== Создание отдельного файла для ответов ==="
echo ""

# Создаем отдельный файл для ErrorResponse и MessageResponse
cat > src/main/kotlin/com/trusttheroute/backend/models/ApiResponses.kt << 'RESPONSESEOF'
package com.trusttheroute.backend.models

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val error: String)

@Serializable
data class MessageResponse(val message: String)
RESPONSESEOF

echo "✅ Создан файл ApiResponses.kt"

# Обновляем AuthRoutes.kt - убираем определения классов из конца
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

fun Application.configureAuthRoutes() {
    val userRepository = UserRepository()
    routing {
        route("/api/v1/auth") {
            post("/register") {
                try {
                    val request = call.receive<RegisterRequest>()
                    if (request.email.isBlank() || request.password.isBlank() || request.name.isBlank()) {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse("Email, password, and name are required"))
                        return@post
                    }
                    if (userRepository.userExists(request.email)) {
                        call.respond(HttpStatusCode.Conflict, ErrorResponse("User with this email already exists"))
                        return@post
                    }
                    val user = userRepository.createUser(request.email, request.password, request.name)
                    if (user != null) {
                        val token = JwtUtils.generateToken(user.id, user.email)
                        call.respond(HttpStatusCode.Created, AuthResponse(user = user.copy(token = token), token = token))
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Failed to create user"))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse(e.message ?: "Unknown error"))
                }
            }
            post("/login") {
                try {
                    val request = call.receive<LoginRequest>()
                    if (request.email.isBlank() || request.password.isBlank()) {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse("Email and password are required"))
                        return@post
                    }
                    val user = userRepository.verifyPassword(request.email, request.password)
                    if (user != null) {
                        val token = JwtUtils.generateToken(user.id, user.email)
                        call.respond(HttpStatusCode.OK, AuthResponse(user = user.copy(token = token), token = token))
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Invalid email or password"))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse(e.message ?: "Unknown error"))
                }
            }
            post("/reset-password") {
                try {
                    val request = call.receive<ResetPasswordRequest>()
                    if (request.email.isBlank()) {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse("Email is required"))
                        return@post
                    }
                    if (!userRepository.userExists(request.email)) {
                        call.respond(HttpStatusCode.OK, MessageResponse("If the email exists, a password reset link has been sent"))
                        return@post
                    }
                    call.respond(HttpStatusCode.OK, MessageResponse("If the email exists, a password reset link has been sent"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse(e.message ?: "Unknown error"))
                }
            }
        }
    }
}
ROUTESEOF

echo "✅ Обновлен AuthRoutes.kt (убраны классы из конца файла)"
echo ""
echo "Проверяем файлы:"
ls -la src/main/kotlin/com/trusttheroute/backend/models/ | grep -E "User|Api"
echo ""
echo "Пересобираем..."
./gradlew clean build

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Сборка успешна!"
    echo ""
    echo "Останавливаем старое приложение..."
    pkill -f 'gradlew run' 2>/dev/null
    sleep 2
    echo "Запускаем новое..."
    if [ -f .env ]; then
        source .env
    fi
    nohup ./gradlew run > app.log 2>&1 &
    sleep 5
    echo ""
    echo "Тест API:"
    curl -s http://localhost:8080/api/v1/auth/register -X POST \
      -H "Content-Type: application/json" \
      -d '{"email":"test@test.com","password":"test123","name":"Test"}'
    echo ""
else
    echo "❌ Ошибка сборки"
fi
