#!/bin/bash
# Финальное исправление всех файлов с правильным синтаксисом

cd ~/trust-the-route-backend/backend

# 1. Application.kt - правильные импорты для Ktor 2.3.6
cat > src/main/kotlin/com/trusttheroute/backend/Application.kt << 'EOF'
package com.trusttheroute.backend

import com.trusttheroute.backend.config.DatabaseConfig
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
                mapOf("error" to (cause.message ?: "Unknown error"))
            )
        }
    }
    
    configureAuthRoutes()
}
EOF

# 2. User.kt - упрощенная версия без проблемных defaultExpression
cat > src/main/kotlin/com/trusttheroute/backend/models/User.kt << 'EOF'
package com.trusttheroute.backend.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import java.util.*

object Users : Table("users") {
    val id = uuid("id")
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val name = varchar("name", 255).nullable()
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    
    override val primaryKey = PrimaryKey(id, name = "users_pkey")
}

data class User(
    val id: String,
    val email: String,
    val name: String,
    val token: String? = null
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class ResetPasswordRequest(
    val email: String
)

data class AuthResponse(
    val user: User,
    val token: String
)
EOF

# 3. UserRepository.kt - правильный синтаксис для получения значений
cat > src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt << 'EOF'
package com.trusttheroute.backend.repositories

import com.trusttheroute.backend.models.Users
import com.trusttheroute.backend.models.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.*

class UserRepository {
    
    fun createUser(email: String, password: String, name: String): User? {
        return transaction {
            try {
                val existingUser = Users.select { Users.email eq email }.firstOrNull()
                if (existingUser != null) {
                    return@transaction null
                }
                
                val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())
                val newUserId = UUID.randomUUID()
                
                Users.insert {
                    it[Users.id] = newUserId
                    it[Users.email] = email
                    it[Users.passwordHash] = passwordHash
                    it[Users.name] = name
                }
                
                val userRow = Users.select { Users.id eq newUserId }.first()
                User(
                    id = userRow[Users.id].toString(),
                    email = userRow[Users.email],
                    name = userRow[Users.name] ?: ""
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    
    fun getUserByEmail(email: String): User? {
        return transaction {
            Users.select { Users.email eq email }.firstOrNull()?.let { row ->
                User(
                    id = row[Users.id].toString(),
                    email = row[Users.email],
                    name = row[Users.name] ?: ""
                )
            }
        }
    }
    
    fun verifyPassword(email: String, password: String): User? {
        return transaction {
            val userRow = Users.select { Users.email eq email }.firstOrNull()
            if (userRow != null) {
                val passwordHash = userRow[Users.passwordHash]
                if (BCrypt.checkpw(password, passwordHash)) {
                    User(
                        id = userRow[Users.id].toString(),
                        email = userRow[Users.email],
                        name = userRow[Users.name] ?: ""
                    )
                } else {
                    null
                }
            } else {
                null
            }
        }
    }
    
    fun userExists(email: String): Boolean {
        return transaction {
            Users.select { Users.email eq email }.count() > 0
        }
    }
}
EOF

echo "Все файлы исправлены!"
EOF
