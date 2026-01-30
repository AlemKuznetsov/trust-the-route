package com.trusttheroute.app.util

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import com.trusttheroute.app.worker.NotificationWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Утилита для управления периодической проверкой уведомлений
 */
@Singleton
class NotificationManager @Inject constructor(
    private val workManager: WorkManager
) {
    companion object {
        private const val NOTIFICATION_WORK_NAME = "notification_check_work"
        private const val REPEAT_INTERVAL_MINUTES = 15L // Проверка каждые 15 минут
    }

    /**
     * Запустить периодическую проверку уведомлений
     */
    fun startPeriodicNotificationCheck() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Требуется интернет
            .setRequiresBatteryNotLow(false) // Не требует высокого уровня батареи
            .build()

        val notificationWork = PeriodicWorkRequestBuilder<NotificationWorker>(
            REPEAT_INTERVAL_MINUTES,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        // Используем KEEP для сохранения существующей работы, если она уже запущена
        workManager.enqueueUniquePeriodicWork(
            NOTIFICATION_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            notificationWork
        )
    }

    /**
     * Остановить периодическую проверку уведомлений
     */
    fun stopPeriodicNotificationCheck() {
        workManager.cancelUniqueWork(NOTIFICATION_WORK_NAME)
    }

    /**
     * Проверить, запущена ли периодическая проверка
     */
    suspend fun isPeriodicCheckRunning(): Boolean {
        val workInfos = workManager.getWorkInfosForUniqueWork(NOTIFICATION_WORK_NAME).await()
        return workInfos.any { it.state.isFinished.not() }
    }
}
