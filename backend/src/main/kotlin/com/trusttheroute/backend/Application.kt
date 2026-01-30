package com.trusttheroute.backend

import com.trusttheroute.backend.config.DatabaseConfig
import com.trusttheroute.backend.models.ErrorResponse
import com.trusttheroute.backend.routes.auth.configureAuthRoutes
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
    DatabaseConfig.init()
    
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
    
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(cause.message ?: "Unknown error")
            )
        }
    }
    
    configureAuthRoutes()
}
