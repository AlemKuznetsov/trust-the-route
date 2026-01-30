package com.trusttheroute.app.data.api

import com.trusttheroute.app.domain.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

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

data class UpdateProfileRequest(
    val name: String
)

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)

data class AuthResponse(
    val user: User,
    val token: String
)

data class MessageResponse(
    val message: String
)

interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<MessageResponse>

    @retrofit2.http.PUT("auth/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<AuthResponse>

    @retrofit2.http.PUT("auth/password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<MessageResponse>

    @retrofit2.http.DELETE("auth/account")
    suspend fun deleteAccount(@retrofit2.http.Query("confirmation") confirmationText: String): Response<MessageResponse>

    @POST("auth/yandex")
    suspend fun authWithYandex(@Body request: YandexAuthRequest): Response<AuthResponse>
}

data class YandexAuthRequest(
    val yandexToken: String,
    val email: String,
    val name: String
)
