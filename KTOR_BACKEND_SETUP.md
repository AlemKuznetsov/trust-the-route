# Создание Ktor бэкенда для Trust The Route

Пошаговое руководство по созданию и настройке Ktor бэкенда.

---

## 🎯 Что мы создадим

- ✅ Ktor сервер с REST API
- ✅ Подключение к PostgreSQL
- ✅ API endpoints для аутентификации:
  - `/api/v1/auth/register`
  - `/api/v1/auth/login`
  - `/api/v1/auth/reset-password`
- ✅ JWT аутентификация
- ✅ Структура проекта

---

## 📁 Где создать проект

### Вариант 1: На сервере (для быстрого старта)

```bash
# Создать директорию для проекта
mkdir -p ~/trust-the-route-backend
cd ~/trust-the-route-backend
```

### Вариант 2: Локально (рекомендуется для разработки)

Создайте проект на вашем компьютере, затем загрузите на сервер.

---

## 🚀 Создание проекта Ktor

### Шаг 1: Установить Ktor CLI (опционально)

```bash
# На сервере или локально
curl -s https://get.ktor.io/install.sh | bash
export PATH="$HOME/.local/bin:$PATH"
```

### Шаг 2: Создать проект через Ktor CLI

```bash
# Создать новый проект
ktor create trust-the-route-backend

# Или создать вручную (см. ниже)
```

---

## 📝 Создание проекта вручную

### Структура проекта:

```
trust-the-route-backend/
├── build.gradle.kts          # Конфигурация Gradle
├── settings.gradle.kts        # Настройки проекта
├── gradle.properties          # Свойства Gradle
├── src/
│   └── main/
│       ├── kotlin/
│       │   └── com/
│       │       └── trusttheroute/
│       │           └── backend/
│       │               ├── Application.kt          # Главный файл
│       │               ├── config/
│       │               │   ├── DatabaseConfig.kt # Конфигурация БД
│       │               │   └── ApplicationConfig.kt
│       │               ├── routes/
│       │               │   └── auth/
│       │               │       └── AuthRoutes.kt   # Маршруты аутентификации
│       │               ├── models/
│       │               │   └── User.kt            # Модели данных
│       │               ├── repositories/
│       │               │   └── UserRepository.kt  # Репозиторий пользователей
│       │               └── utils/
│       │                   └── JwtUtils.kt        # Утилиты для JWT
│       └── resources/
│           └── application.conf                   # Конфигурация приложения
└── README.md
```

---

## 📄 Файлы проекта

### 1. `build.gradle.kts`

```kotlin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.22"
    application
    id("io.ktor.plugin") version "2.3.6"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
}

group = "com.trusttheroute"
version = "1.0.0"

application {
    mainClass.set("com.trusttheroute.backend.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor
    implementation("io.ktor:ktor-server-core:2.3.6")
    implementation("io.ktor:ktor-server-netty:2.3.6")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.6")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.6")
    implementation("io.ktor:ktor-server-cors:2.3.6")
    implementation("io.ktor:ktor-server-auth:2.3.6")
    implementation("io.ktor:ktor-server-auth-jwt:2.3.6")
    implementation("io.ktor:ktor-server-status-pages:2.3.6")
    
    // Database
    implementation("org.jetbrains.exposed:exposed-core:0.44.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.44.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.44.1")
    implementation("org.postgresql:postgresql:42.7.1")
    implementation("com.zaxxer:HikariCP:5.1.0")
    
    // Password hashing
    implementation("org.mindrot:jbcrypt:0.4")
    
    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.14")
    
    // Testing
    testImplementation("io.ktor:ktor-server-tests:2.3.6")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.22")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}
```

### 2. `settings.gradle.kts`

```kotlin
rootProject.name = "trust-the-route-backend"
```

### 3. `src/main/resources/application.conf`

```hocon
ktor {
    deployment {
        port = 8080
        host = "0.0.0.0"
    }
    application {
        modules = [ com.trusttheroute.backend.ApplicationKt.module ]
    }
}

database {
    driver = "org.postgresql.Driver"
    url = "jdbc:postgresql://localhost:5432/trust_the_route"
    user = "trust_user"
    password = "ваш_пароль_здесь"
    maxPoolSize = 10
}

jwt {
    secret = "ваш_секретный_ключ_для_jwt_минимум_32_символа"
    issuer = "trust-the-route"
    audience = "trust-the-route-users"
    realm = "Trust The Route"
}
```

### 4. `src/main/kotlin/com/trusttheroute/backend/Application.kt`

```kotlin
package com.trusttheroute.backend

import com.trusttheroute.backend.config.*
import com.trusttheroute.backend.routes.auth.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    // Database
    DatabaseConfig.init()
    
    // Content Negotiation (JSON)
    install(ContentNegotiation) {
        json()
    }
    
    // CORS
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
    }
    
    // Status Pages
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to (cause.message ?: "Unknown error"))
            )
        }
    }
    
    // Routes
    configureAuthRoutes()
}
```

### 5. `src/main/kotlin/com/trusttheroute/backend/config/DatabaseConfig.kt`

```kotlin
package com.trusttheroute.backend.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import io.ktor.config.*

object DatabaseConfig {
    private lateinit var dataSource: HikariDataSource
    
    fun init() {
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = "jdbc:postgresql://localhost:5432/trust_the_route"
            username = "trust_user"
            password = System.getenv("DB_PASSWORD") ?: "ваш_пароль_здесь"
            maximumPoolSize = 10
            minimumIdle = 2
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }
        
        dataSource = HikariDataSource(config)
        Database.connect(dataSource)
    }
    
    fun close() {
        dataSource.close()
    }
}
```

### 6. `src/main/kotlin/com/trusttheroute/backend/models/User.kt`

```kotlin
package com.trusttheroute.backend.models

import org.jetbrains.exposed.sql.Table
import java.util.*

object Users : Table("users") {
    val id = uuid("id").primaryKey().defaultExpression(UUID.randomUUID())
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val name = varchar("name", 255).nullable()
    val createdAt = timestamp("created_at").defaultExpression(java.time.Instant.now())
    val updatedAt = timestamp("updated_at").defaultExpression(java.time.Instant.now())
}

data class User(
    val id: UUID,
    val email: String,
    val name: String?,
    val createdAt: java.time.Instant,
    val updatedAt: java.time.Instant
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

data class AuthResponse(
    val user: User,
    val token: String
)
```

---

## 🔐 Настройка JWT

### `src/main/kotlin/com/trusttheroute/backend/utils/JwtUtils.kt`

```kotlin
package com.trusttheroute.backend.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import java.util.*

object JwtUtils {
    private const val SECRET = "ваш_секретный_ключ_для_jwt_минимум_32_символа"
    private const val ISSUER = "trust-the-route"
    private const val AUDIENCE = "trust-the-route-users"
    private val algorithm = Algorithm.HMAC256(SECRET)
    
    fun generateToken(userId: UUID, email: String): String {
        return JWT.create()
            .withIssuer(ISSUER)
            .withAudience(AUDIENCE)
            .withClaim("userId", userId.toString())
            .withClaim("email", email)
            .withExpiresAt(Date(System.currentTimeMillis() + 86400000)) // 24 часа
            .sign(algorithm)
    }
    
    fun verifyToken(token: String): DecodedJWT? {
        return try {
            JWT.require(algorithm)
                .withIssuer(ISSUER)
                .withAudience(AUDIENCE)
                .build()
                .verify(token)
        } catch (e: Exception) {
            null
        }
    }
}
```

**Добавьте зависимость в `build.gradle.kts`:**
```kotlin
implementation("com.auth0:java-jwt:4.4.0")
```

---

## 📋 Следующие шаги

1. ✅ Создать структуру проекта
2. ✅ Настроить файлы конфигурации
3. ✅ Реализовать репозиторий пользователей
4. ✅ Реализовать маршруты аутентификации
5. ✅ Протестировать API

---

**Готов помочь создать полную структуру проекта! Скажите, где создавать проект - на сервере или локально?**
