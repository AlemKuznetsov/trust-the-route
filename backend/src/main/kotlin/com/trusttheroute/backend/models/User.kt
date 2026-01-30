package com.trusttheroute.backend.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import java.util.*
import kotlinx.serialization.Serializable

object Users : Table("users") {
    val id = uuid("id")
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val name = varchar("name", 255).nullable()
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    
    override val primaryKey = PrimaryKey(id, name = "users_pkey")
}

@Serializable
data class User(
    val id: String,
    val email: String,
    val name: String,
    val token: String? = null
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class ResetPasswordRequest(
    val email: String
)

@Serializable
data class UpdateProfileRequest(
    val name: String
)

@Serializable
data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)

@Serializable
data class DeleteAccountRequest(
    val confirmationText: String
)

@Serializable
data class YandexAuthRequest(
    val yandexToken: String,
    val email: String,
    val name: String
)

@Serializable
data class AuthResponse(
    val user: User,
    val token: String
)
