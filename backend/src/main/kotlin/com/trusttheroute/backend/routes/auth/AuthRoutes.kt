package com.trusttheroute.backend.routes.auth

import com.trusttheroute.backend.models.*
import com.trusttheroute.backend.repositories.UserRepository
import com.trusttheroute.backend.utils.JwtUtils
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.configureAuthRoutes() {
    val userRepository = UserRepository()
    
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
        }
    }
}
