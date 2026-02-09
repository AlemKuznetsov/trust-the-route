package com.trusttheroute.backend.repositories

import com.trusttheroute.backend.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.datetime.Clock
import java.util.UUID

class NotificationRepository {
    
    /**
     * Регистрация устройства для получения уведомлений
     */
    fun registerDevice(
        userId: String,
        deviceId: String,
        platform: String,
        appVersion: String,
        osVersion: String
    ): Boolean {
        return transaction {
            try {
                val uuid = UUID.fromString(userId)
                val currentTime = Clock.System.now()
                
                // Проверяем, существует ли уже регистрация
                val existing = DeviceRegistrations.select {
                    (DeviceRegistrations.userId eq uuid) and (DeviceRegistrations.deviceId eq deviceId)
                }.firstOrNull()
                
                if (existing != null) {
                    // Обновляем существующую регистрацию
                    DeviceRegistrations.update({
                        (DeviceRegistrations.userId eq uuid) and (DeviceRegistrations.deviceId eq deviceId)
                    }) {
                        it[DeviceRegistrations.platform] = platform
                        it[DeviceRegistrations.appVersion] = appVersion
                        it[DeviceRegistrations.osVersion] = osVersion
                        it[DeviceRegistrations.updatedAt] = currentTime
                    }
                } else {
                    // Создаем новую регистрацию
                    DeviceRegistrations.insert {
                        it[DeviceRegistrations.userId] = uuid
                        it[DeviceRegistrations.deviceId] = deviceId
                        it[DeviceRegistrations.platform] = platform
                        it[DeviceRegistrations.appVersion] = appVersion
                        it[DeviceRegistrations.osVersion] = osVersion
                        it[DeviceRegistrations.createdAt] = currentTime
                    }
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
    
    /**
     * Получить непрочитанные уведомления для пользователя
     */
    fun getUnreadNotifications(userId: String, lastNotificationId: String? = null): List<NotificationResponse> {
        return transaction {
            try {
                val uuid = UUID.fromString(userId)
                
                val query = if (lastNotificationId != null) {
                    val lastId = UUID.fromString(lastNotificationId)
                    Notifications.select {
                        (Notifications.userId eq uuid) and 
                        (Notifications.read eq false) and
                        (Notifications.id greater lastId)
                    }.orderBy(Notifications.createdAt to SortOrder.DESC).limit(50)
                } else {
                    Notifications.select {
                        (Notifications.userId eq uuid) and (Notifications.read eq false)
                    }.orderBy(Notifications.createdAt to SortOrder.DESC).limit(50)
                }
                
                query.map { row ->
                    val createdAt = row[Notifications.createdAt]
                    NotificationResponse(
                        id = row[Notifications.id].toString(),
                        title = row[Notifications.title],
                        body = row[Notifications.body],
                        type = NotificationType.valueOf(row[Notifications.type]),
                        data = null, // TODO: Парсинг JSON из row[Notifications.data]
                        timestamp = createdAt.toEpochMilliseconds(),
                        read = row[Notifications.read]
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }
    
    /**
     * Отметить уведомление как прочитанное
     */
    fun markAsRead(userId: String, notificationId: String): Boolean {
        return transaction {
            try {
                val userUuid = UUID.fromString(userId)
                val notificationUuid = UUID.fromString(notificationId)
                val currentTime = Clock.System.now()
                
                val updated = Notifications.update({
                    (Notifications.id eq notificationUuid) and (Notifications.userId eq userUuid)
                }) {
                    it[Notifications.read] = true
                    it[Notifications.updatedAt] = currentTime
                }
                
                updated > 0
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
    
    /**
     * Отметить все уведомления как прочитанные
     */
    fun markAllAsRead(userId: String): Boolean {
        return transaction {
            try {
                val userUuid = UUID.fromString(userId)
                val currentTime = Clock.System.now()
                
                val updated = Notifications.update({
                    (Notifications.userId eq userUuid) and (Notifications.read eq false)
                }) {
                    it[Notifications.read] = true
                    it[Notifications.updatedAt] = currentTime
                }
                
                updated > 0
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
