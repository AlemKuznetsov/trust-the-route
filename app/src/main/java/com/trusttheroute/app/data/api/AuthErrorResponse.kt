package com.trusttheroute.app.data.api

/**
 * Стандартный формат ошибки от API
 */
data class AuthErrorResponse(
    val error: String,
    val message: String? = null,
    val details: Map<String, String>? = null
)

/**
 * Типы ошибок аутентификации
 */
enum class AuthErrorType {
    NETWORK_ERROR,
    INVALID_CREDENTIALS,
    EMAIL_ALREADY_EXISTS,
    WEAK_PASSWORD,
    INVALID_EMAIL,
    SERVER_ERROR,
    UNKNOWN_ERROR
}

/**
 * Класс для обработки ошибок аутентификации
 */
class AuthException(
    val type: AuthErrorType,
    message: String,
    val originalException: Throwable? = null
) : Exception(message, originalException)
