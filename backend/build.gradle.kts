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
    implementation("io.ktor:ktor-server-config-yaml:2.3.6")
    // Multipart входит в ktor-server-core, отдельный модуль не нужен
    
    // Database
    implementation("org.jetbrains.exposed:exposed-core:0.44.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.44.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.44.1")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.44.1")
    implementation("org.postgresql:postgresql:42.7.1")
    implementation("com.zaxxer:HikariCP:5.1.0")
    
    // Password hashing
    implementation("org.mindrot:jbcrypt:0.4")
    
    // JWT
    implementation("com.auth0:java-jwt:4.4.0")
    
    // AWS SDK для работы с Yandex Object Storage (S3-совместимый)
    implementation("software.amazon.awssdk:s3:2.20.162")
    implementation("software.amazon.awssdk:auth:2.20.162")
    implementation("software.amazon.awssdk:regions:2.20.162")
    
    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.14")
    
    // Testing
    testImplementation("io.ktor:ktor-server-tests:2.3.6")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.22")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

// Настройка задачи jar для создания исполняемого JAR
tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to "com.trusttheroute.backend.ApplicationKt"
            )
        )
    }
    
    // Включаем все зависимости в JAR (fat jar)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
    
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
