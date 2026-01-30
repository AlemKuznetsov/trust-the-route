#!/bin/bash
# Финальное исправление сериализации

cd ~/trust-the-route-backend/backend

echo "=== Финальное исправление сериализации ==="
echo ""

# Проверяем текущий AuthRoutes.kt
echo "Проверяем AuthRoutes.kt..."
if grep -q "@Serializable" src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt; then
    echo "✅ @Serializable найден"
    grep -n "@Serializable\|ErrorResponse\|MessageResponse" src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt | tail -5
else
    echo "❌ @Serializable не найден"
fi

echo ""
echo "Пересоздаем AuthRoutes.kt полностью..."

# Полностью пересоздаем AuthRoutes.kt
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

@Serializable
data class ErrorResponse(val error: String)

@Serializable
data class MessageResponse(val message: String)
ROUTESEOF

echo "✅ Файл пересоздан"
echo ""
echo "Проверяем наличие @Serializable:"
grep -n "@Serializable\|ErrorResponse\|MessageResponse" src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt | tail -3
echo ""
echo "Пересобираем проект..."
./gradlew clean build

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Сборка успешна!"
    echo ""
    echo "Останавливаем старое приложение..."
    pkill -f 'gradlew run' 2>/dev/null
    sleep 2
    
    echo "Запускаем новое приложение..."
    if [ -f .env ]; then
        source .env
    fi
    nohup ./gradlew run > app.log 2>&1 &
    APP_PID=$!
    sleep 5
    
    if ps -p $APP_PID > /dev/null 2>&1; then
        echo "✅ Приложение запущено (PID: $APP_PID)"
        echo ""
        echo "Последние строки лога:"
        tail -15 app.log
        echo ""
        echo "Тестируем API..."
        sleep 2
        curl -s http://localhost:8080/api/v1/auth/register -X POST \
          -H "Content-Type: application/json" \
          -d '{"email":"test@test.com","password":"test123","name":"Test"}' | head -c 200
        echo ""
    else
        echo "❌ Приложение не запустилось. Проверьте логи:"
        tail -30 app.log
    fi
else
    echo ""
    echo "❌ Ошибка сборки. Проверьте вывод выше."
fi
