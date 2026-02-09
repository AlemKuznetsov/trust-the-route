package com.trusttheroute.backend.routes.notifications

import com.trusttheroute.backend.models.*
import com.trusttheroute.backend.repositories.NotificationRepository
import com.trusttheroute.backend.utils.JwtUtils
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.auth0.jwt.interfaces.DecodedJWT

fun Application.configureNotificationRoutes() {
    val notificationRepository = NotificationRepository()
    
    // Функция для получения userId из токена
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
        route("/api/v1/notifications") {
            
            /**
             * Регистрация устройства для получения уведомлений
             * POST /api/v1/notifications/register
             * Authorization: Bearer <token>
             * Body: DeviceRegistrationRequest
             */
            post("/register") {
                try {
                    val userId = getUserIdFromToken(call)
                    if (userId == null) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            ErrorResponse("Authentication required")
                        )
                        return@post
                    }
                    
                    val request = call.receive<DeviceRegistrationRequest>()
                    
                    // Валидация
                    if (request.deviceId.isBlank()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("Device ID is required")
                        )
                        return@post
                    }
                    
                    val registered = notificationRepository.registerDevice(
                        userId = userId,
                        deviceId = request.deviceId,
                        platform = request.platform,
                        appVersion = request.appVersion,
                        osVersion = request.osVersion
                    )
                    
                    if (registered) {
                        call.respond(
                            HttpStatusCode.OK,
                            DeviceRegistrationResponse(
                                deviceId = request.deviceId,
                                registered = true,
                                message = "Device registered successfully"
                            )
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            ErrorResponse("Failed to register device")
                        )
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(e.message ?: "Unknown error")
                    )
                }
            }
            
            /**
             * Получение непрочитанных уведомлений
             * GET /api/v1/notifications?lastNotificationId=<id>
             * Authorization: Bearer <token>
             */
            get {
                try {
                    val userId = getUserIdFromToken(call)
                    if (userId == null) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            ErrorResponse("Authentication required")
                        )
                        return@get
                    }
                    
                    val lastNotificationId = call.request.queryParameters["lastNotificationId"]
                    
                    val notifications = notificationRepository.getUnreadNotifications(
                        userId = userId,
                        lastNotificationId = lastNotificationId
                    )
                    
                    call.respond(HttpStatusCode.OK, notifications)
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(e.message ?: "Unknown error")
                    )
                }
            }
            
            /**
             * Отметить уведомление как прочитанное
             * PUT /api/v1/notifications/{notificationId}/read
             * Authorization: Bearer <token>
             */
            put("/{notificationId}/read") {
                try {
                    val userId = getUserIdFromToken(call)
                    if (userId == null) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            ErrorResponse("Authentication required")
                        )
                        return@put
                    }
                    
                    val notificationId = call.parameters["notificationId"]
                        ?: throw IllegalArgumentException("Notification ID is required")
                    
                    val success = notificationRepository.markAsRead(userId, notificationId)
                    
                    if (success) {
                        call.respond(HttpStatusCode.OK, MessageResponse("Notification marked as read"))
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            ErrorResponse("Notification not found")
                        )
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(e.message ?: "Unknown error")
                    )
                }
            }
            
            /**
             * Отметить все уведомления как прочитанные
             * PUT /api/v1/notifications/read-all
             * Authorization: Bearer <token>
             */
            put("/read-all") {
                try {
                    val userId = getUserIdFromToken(call)
                    if (userId == null) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            ErrorResponse("Authentication required")
                        )
                        return@put
                    }
                    
                    val success = notificationRepository.markAllAsRead(userId)
                    
                    if (success) {
                        call.respond(HttpStatusCode.OK, MessageResponse("All notifications marked as read"))
                    } else {
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            ErrorResponse("Failed to mark notifications as read")
                        )
                    }
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
