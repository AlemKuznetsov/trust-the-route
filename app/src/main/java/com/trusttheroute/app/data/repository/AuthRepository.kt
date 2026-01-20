package com.trusttheroute.app.data.repository

import com.google.gson.Gson
import com.trusttheroute.app.data.api.AuthApi
import com.trusttheroute.app.data.api.AuthErrorResponse
import com.trusttheroute.app.data.api.AuthErrorType
import com.trusttheroute.app.data.api.AuthException
import com.trusttheroute.app.data.api.AuthResponse
import com.trusttheroute.app.data.api.LoginRequest
import com.trusttheroute.app.data.api.RegisterRequest
import com.trusttheroute.app.data.api.ResetPasswordRequest
import com.trusttheroute.app.data.auth.YandexAuthManager
import com.trusttheroute.app.data.local.PreferencesManager
import com.trusttheroute.app.domain.model.User
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val preferencesManager: PreferencesManager,
    private val yandexAuthManager: YandexAuthManager,
    private val gson: Gson
) {
    suspend fun register(email: String, password: String, name: String): Result<User> {
        return try {
            val response = authApi.register(RegisterRequest(email, password, name))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                preferencesManager.saveToken(authResponse.token)
                preferencesManager.saveUser(authResponse.user)
                Result.success(authResponse.user)
            } else {
                val error = parseError(response.code(), response.errorBody()?.string())
                Result.failure(error)
            }
        } catch (e: HttpException) {
            val error = parseError(e.code(), e.response()?.errorBody()?.string())
            Result.failure(error)
        } catch (e: IOException) {
            Result.failure(
                AuthException(
                    AuthErrorType.NETWORK_ERROR,
                    "Ошибка сети. Проверьте подключение к интернету",
                    e
                )
            )
        } catch (e: Exception) {
            Result.failure(
                AuthException(
                    AuthErrorType.UNKNOWN_ERROR,
                    "Неизвестная ошибка: ${e.message}",
                    e
                )
            )
        }
    }

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = authApi.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                preferencesManager.saveToken(authResponse.token)
                preferencesManager.saveUser(authResponse.user)
                Result.success(authResponse.user)
            } else {
                val error = parseError(response.code(), response.errorBody()?.string())
                Result.failure(error)
            }
        } catch (e: HttpException) {
            val error = parseError(e.code(), e.response()?.errorBody()?.string())
            Result.failure(error)
        } catch (e: IOException) {
            Result.failure(
                AuthException(
                    AuthErrorType.NETWORK_ERROR,
                    "Ошибка сети. Проверьте подключение к интернету",
                    e
                )
            )
        } catch (e: Exception) {
            Result.failure(
                AuthException(
                    AuthErrorType.UNKNOWN_ERROR,
                    "Неизвестная ошибка: ${e.message}",
                    e
                )
            )
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            val response = authApi.resetPassword(ResetPasswordRequest(email))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val error = parseError(response.code(), response.errorBody()?.string())
                Result.failure(error)
            }
        } catch (e: HttpException) {
            val error = parseError(e.code(), e.response()?.errorBody()?.string())
            Result.failure(error)
        } catch (e: IOException) {
            Result.failure(
                AuthException(
                    AuthErrorType.NETWORK_ERROR,
                    "Ошибка сети. Проверьте подключение к интернету",
                    e
                )
            )
        } catch (e: Exception) {
            Result.failure(
                AuthException(
                    AuthErrorType.UNKNOWN_ERROR,
                    "Неизвестная ошибка: ${e.message}",
                    e
                )
            )
        }
    }
    
    /**
     * Парсит ошибку из ответа API
     */
    private fun parseError(code: Int, errorBody: String?): AuthException {
        return try {
            val errorResponse = if (errorBody != null) {
                try {
                    gson.fromJson(errorBody, AuthErrorResponse::class.java)
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
            
            val errorMessage = errorResponse?.message ?: errorResponse?.error ?: "Ошибка сервера"
            
            val finalMessage = errorMessage.ifEmpty { "Ошибка сервера" }
            
            when (code) {
                400 -> AuthException(
                    AuthErrorType.INVALID_EMAIL,
                    finalMessage
                )
                401 -> AuthException(
                    AuthErrorType.INVALID_CREDENTIALS,
                    finalMessage.ifEmpty { "Неверный email или пароль" }
                )
                409 -> AuthException(
                    AuthErrorType.EMAIL_ALREADY_EXISTS,
                    finalMessage.ifEmpty { "Пользователь с таким email уже существует" }
                )
                422 -> AuthException(
                    AuthErrorType.WEAK_PASSWORD,
                    finalMessage.ifEmpty { "Пароль не соответствует требованиям" }
                )
                500, 502, 503 -> AuthException(
                    AuthErrorType.SERVER_ERROR,
                    "Ошибка сервера. Попробуйте позже"
                )
                else -> AuthException(
                    AuthErrorType.UNKNOWN_ERROR,
                    finalMessage.ifEmpty { "Неизвестная ошибка (код: $code)" }
                )
            }
        } catch (e: Exception) {
            AuthException(
                AuthErrorType.UNKNOWN_ERROR,
                "Ошибка при обработке ответа сервера",
                e
            )
        }
    }

    suspend fun logout() {
        preferencesManager.clearToken()
        preferencesManager.clearUser()
    }

    suspend fun getCurrentUser(): User? {
        return preferencesManager.getUser()
    }

    suspend fun isLoggedIn(): Boolean {
        return preferencesManager.getToken() != null
    }

    /**
     * Получает URL для авторизации через Yandex ID
     */
    suspend fun getYandexAuthUrl(): String {
        return yandexAuthManager.getAuthUrl()
    }

    /**
     * Обрабатывает OAuth callback от Yandex ID
     */
    suspend fun handleYandexOAuthCallback(uri: android.net.Uri): Result<User> {
        return yandexAuthManager.handleAuthCallback(uri)
    }
}
