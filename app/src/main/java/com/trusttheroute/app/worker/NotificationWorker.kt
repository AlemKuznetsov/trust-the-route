package com.trusttheroute.app.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.trusttheroute.app.data.api.NotificationApi
import com.trusttheroute.app.data.local.PreferencesManager
import com.trusttheroute.app.service.YandexPushService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * Worker для периодической проверки новых уведомлений через Yandex Cloud Notification Service
 * 
 * Работает в фоновом режиме и периодически запрашивает новые уведомления с сервера
 */
@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val notificationApi: NotificationApi,
    private val pushService: YandexPushService,
    private val preferencesManager: PreferencesManager
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "NotificationWorker"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Проверяем, включены ли уведомления
            val notificationsEnabled = preferencesManager.notificationsEnabled.first() ?: true
            if (!notificationsEnabled) {
                Log.d(TAG, "Notifications disabled, skipping check")
                return@withContext Result.success()
            }

            // Получаем ID последнего уведомления (если есть)
            val lastNotificationId = preferencesManager.lastNotificationId.first()

            // Запрашиваем новые уведомления
            // TODO: Передать lastNotificationId как query параметр, когда API будет готово
            val response = notificationApi.getNotifications(null)
            
            if (response.isSuccessful) {
                val notifications = response.body() ?: emptyList()
                
                if (notifications.isNotEmpty()) {
                    Log.d(TAG, "Received ${notifications.size} new notifications")
                    
                    // Показываем уведомления
                    if (notifications.size == 1) {
                        pushService.showNotification(notifications.first())
                    } else {
                        pushService.showNotificationGroup(notifications)
                    }
                    
                    // Сохраняем ID последнего уведомления
                    val latestId = notifications.maxByOrNull { it.timestamp }?.id
                    latestId?.let { id ->
                        preferencesManager.setLastNotificationId(id)
                    }
                    
                    // Отмечаем уведомления как прочитанные (опционально)
                    // Можно раскомментировать, если нужно автоматически отмечать как прочитанные
                    // notifications.forEach { notification ->
                    //     try {
                    //         notificationApi.markAsRead(notification.id)
                    //     } catch (e: Exception) {
                    //         Log.e(TAG, "Error marking notification as read", e)
                    //     }
                    // }
                } else {
                    Log.d(TAG, "No new notifications")
                }
                
                Result.success()
            } else {
                Log.e(TAG, "Failed to fetch notifications: ${response.code()}")
                Result.retry() // Повторить попытку при ошибке
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking notifications", e)
            Result.retry() // Повторить попытку при исключении
        }
    }
}
