package com.trusttheroute.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.trusttheroute.app.MainActivity
import com.trusttheroute.app.R
import com.trusttheroute.app.data.api.NotificationResponse
import com.trusttheroute.app.data.api.NotificationType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Сервис для обработки push-уведомлений через Yandex Cloud Notification Service
 * 
 * Уведомления приходят через наш бэкенд, который использует Yandex CNS
 * для отправки push-уведомлений на устройства пользователей.
 */
@Singleton
class YandexPushService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    companion object {
        private const val TAG = "YandexPushService"
        private const val CHANNEL_ID = "trust_the_route_notifications"
        private const val CHANNEL_NAME = "Trust The Route Уведомления"
        private const val CHANNEL_DESCRIPTION = "Уведомления о новых маршрутах и обновлениях"
    }
    
    init {
        createNotificationChannel()
    }
    
    /**
     * Создание канала уведомлений (требуется для Android 8.0+)
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Отображение уведомления
     * @param notification Данные уведомления
     */
    fun showNotification(notification: NotificationResponse) {
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                // Добавляем данные уведомления в Intent
                putExtra("notification_id", notification.id)
                putExtra("notification_type", notification.type.name)
            }
            
            val pendingIntent = PendingIntent.getActivity(
                context,
                notification.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(notification.title)
                .setContentText(notification.body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(notification.body))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setWhen(notification.timestamp)
            
            // Добавляем действия в зависимости от типа уведомления
            when (notification.type) {
                NotificationType.NEW_ROUTE -> {
                    // Можно добавить кнопку "Открыть маршрут"
                    notificationBuilder.setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
                }
                NotificationType.ROUTE_UPDATE -> {
                    notificationBuilder.setCategory(NotificationCompat.CATEGORY_STATUS)
                }
                else -> {
                    notificationBuilder.setCategory(NotificationCompat.CATEGORY_MESSAGE)
                }
            }
            
            notificationManager.notify(notification.id.hashCode(), notificationBuilder.build())
            Log.d(TAG, "Notification shown: ${notification.title}")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error showing notification", e)
        }
    }
    
    /**
     * Отображение нескольких уведомлений (группа)
     */
    fun showNotificationGroup(notifications: List<NotificationResponse>) {
        if (notifications.isEmpty()) return
        
        // Если одно уведомление, показываем его обычным способом
        if (notifications.size == 1) {
            showNotification(notifications.first())
            return
        }
        
        // Для нескольких уведомлений создаем группу
        val summaryId = "summary_${System.currentTimeMillis()}".hashCode()
        
        // Создаем сводное уведомление
        val inboxStyle = NotificationCompat.InboxStyle()
            .setBigContentTitle("Trust The Route")
        
        // Добавляем строки для первых 5 уведомлений
        notifications.take(5).forEach { notification ->
            inboxStyle.addLine(notification.title)
        }
        
        // Устанавливаем текст сводки
        val summaryText = if (notifications.size > 5) {
            "У вас ${notifications.size} новых уведомлений (+${notifications.size - 5} еще)"
        } else {
            "У вас ${notifications.size} новых уведомлений"
        }
        inboxStyle.setSummaryText(summaryText)
        
        val summaryNotification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle("Trust The Route")
            .setContentText("У вас ${notifications.size} новых уведомлений")
            .setStyle(inboxStyle)
            .setGroupSummary(true)
            .setGroup(CHANNEL_ID)
            .setAutoCancel(true)
            .build()
        
        // Показываем отдельные уведомления
        notifications.forEach { notification ->
            showNotification(notification)
        }
        
        // Показываем сводное уведомление
        notificationManager.notify(summaryId, summaryNotification)
    }
    
    /**
     * Отмена уведомления
     */
    fun cancelNotification(notificationId: String) {
        notificationManager.cancel(notificationId.hashCode())
    }
    
    /**
     * Отмена всех уведомлений
     */
    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }
}
