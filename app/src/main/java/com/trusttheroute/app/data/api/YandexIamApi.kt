package com.trusttheroute.app.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * API для работы с Yandex Cloud IAM
 * 
 * Документация: https://cloud.yandex.ru/docs/iam/api-ref/
 * 
 * ВАЖНО: Для работы с Yandex IAM нужен Service Account или OAuth токен
 * 
 * Если у вас есть собственный бэкенд, который использует Yandex IAM,
 * используйте AuthApi вместо этого интерфейса.
 */
interface YandexIamApi {
    
    /**
     * Создание пользователя через Yandex Cloud IAM
     * Требует Service Account токен в заголовке Authorization
     */
    @POST("iam/v1/serviceAccounts/{serviceAccountId}/accessBindings")
    suspend fun createUser(
        @Header("Authorization") token: String,
        @Body request: YandexIamCreateUserRequest
    ): Response<YandexIamUserResponse>
    
    /**
     * Получение токена для пользователя
     */
    @POST("iam/v1/tokens")
    suspend fun getToken(
        @Body request: YandexIamTokenRequest
    ): Response<YandexIamTokenResponse>
}

/**
 * Запрос на создание пользователя в Yandex IAM
 */
data class YandexIamCreateUserRequest(
    val email: String,
    val name: String,
    val password: String? = null // Если null, будет создан через Yandex ID
)

/**
 * Ответ при создании пользователя
 */
data class YandexIamUserResponse(
    val id: String,
    val email: String,
    val name: String
)

/**
 * Запрос на получение токена
 */
data class YandexIamTokenRequest(
    val yandexPassportOauthToken: String? = null, // OAuth токен Yandex
    val jwt: String? = null, // JWT токен
    val iamToken: String? = null // IAM токен
)

/**
 * Ответ с токеном
 */
data class YandexIamTokenResponse(
    val iamToken: String,
    val expiresAt: String
)
