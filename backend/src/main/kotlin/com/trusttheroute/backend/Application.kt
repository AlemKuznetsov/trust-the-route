package com.trusttheroute.backend

import com.trusttheroute.backend.config.DatabaseConfig
import com.trusttheroute.backend.models.ErrorResponse
import com.trusttheroute.backend.routes.auth.configureAuthRoutes
import com.trusttheroute.backend.routes.media.configureMediaRoutes
import com.trusttheroute.backend.routes.notifications.configureNotificationRoutes
import com.trusttheroute.backend.routes.routes.configureRouteRoutes
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.http.*
import io.ktor.server.response.*
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.json

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    println("DEBUG: Application.module() начал выполнение")
    DatabaseConfig.init()
    println("DEBUG: DatabaseConfig.init() завершен")
    
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
    
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
    }
    
    // Multipart в Ktor 2.x работает без установки плагина
    
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(cause.message ?: "Unknown error")
            )
        }
    }
    
    println("DEBUG: Начинаю регистрацию маршрутов...")
    configureAuthRoutes()
    println("DEBUG: configureAuthRoutes() завершен")
    configureMediaRoutes()
    println("DEBUG: configureMediaRoutes() завершен")
    configureNotificationRoutes()
    println("DEBUG: configureNotificationRoutes() завершен")
    println("DEBUG: Вызываю configureRouteRoutes()...")
    try {
        configureRouteRoutes()
        println("DEBUG: configureRouteRoutes() завершен успешно")
    } catch (e: Exception) {
        println("ERROR: configureRouteRoutes() упал с ошибкой: ${e.message}")
        e.printStackTrace()
        throw e
    }
    println("DEBUG: Все маршруты зарегистрированы")
}
