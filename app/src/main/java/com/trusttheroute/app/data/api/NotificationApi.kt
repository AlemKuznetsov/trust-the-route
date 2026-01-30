package com.trusttheroute.app.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API для работы с уведомлениями через Yandex Cloud Notification Service
 * 
 * Бэкенд использует Yandex CNS для отправки push-уведомлений
 * Клиент регистрирует устройство и периодически проверяет наличие новых уведомлений
 */
interface NotificationApi {
    
    /**
     * Регистрация устройства для получения уведомлений
     * @param request Данные устройства для регистрации
     * @return Результат регистрации
     */
    @POST("notifications/register")
    suspend fun registerDevice(@Body request: DeviceRegistrationRequest): Response<DeviceRegistrationResponse>
    
    /**
     * Получение списка непрочитанных уведомлений
     * @param lastNotificationId ID последнего полученного уведомления (опционально)
     * @return Список уведомлений
     */
    @GET("notifications")
    suspend fun getNotifications(
        @Query("lastNotificationId") lastNotificationId: String? = null
    ): Response<List<NotificationResponse>>
    
    /**
     * Отметить уведомление как прочитанное
     * @param notificationId ID уведомления
     */
    @PUT("notifications/{notificationId}/read")
    suspend fun markAsRead(@Path("notificationId") notificationId: String): Response<Unit>
    
    /**
     * Отметить все уведомления как прочитанные
     */
    @PUT("notifications/read-all")
    suspend fun markAllAsRead(): Response<Unit>
}

/**
 * Запрос на регистрацию устройства
 */
data class DeviceRegistrationRequest(
    val deviceId: String, // Уникальный ID устройства (Android ID или UUID)
    val deviceToken: String? = null, // Токен для push-уведомлений (если используется)
    val platform: String = "android",
    val appVersion: String,
    val osVersion: String
)

/**
 * Ответ при регистрации устройства
 */
data class DeviceRegistrationResponse(
    val deviceId: String,
    val registered: Boolean,
    val message: String? = null
)

/**
 * Модель уведомления
 */
data class NotificationResponse(
    val id: String,
    val title: String,
    val body: String,
    val type: NotificationType,
    val data: Map<String, String>? = null, // Дополнительные данные
    val timestamp: Long,
    val read: Boolean = false
)

/**
 * Типы уведомлений
 */
enum class NotificationType {
    NEW_ROUTE,           // Новый маршрут
    ROUTE_UPDATE,        // Обновление маршрута
    ATTRACTION_UPDATE,   // Обновление достопримечательности
    SYSTEM,              // Системное уведомление
    PROMOTION            // Рекламное/промо уведомление
}
