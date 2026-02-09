package com.trusttheroute.backend.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object DeviceRegistrations : Table("device_registrations") {
    val id = uuid("id").autoGenerate()
    val userId = uuid("user_id").references(Users.id, onDelete = org.jetbrains.exposed.sql.ForeignKeyAction.CASCADE)
    val deviceId = varchar("device_id", 255)
    val platform = varchar("platform", 50).default("android")
    val appVersion = varchar("app_version", 50)
    val osVersion = varchar("os_version", 50)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at").nullable()
    
    init {
        uniqueIndex(userId, deviceId)
    }
}

object Notifications : Table("notifications") {
    val id = uuid("id").autoGenerate()
    val userId = uuid("user_id").references(Users.id, onDelete = org.jetbrains.exposed.sql.ForeignKeyAction.CASCADE)
    val title = varchar("title", 255)
    val body = text("body")
    val type = varchar("type", 50)
    val data = text("data").nullable() // JSON строка
    val read = bool("read").default(false)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at").nullable()
    
    init {
        index(userId, read, createdAt)
    }
}
