package com.trusttheroute.backend.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class DeviceRegistrationRequest(
    val deviceId: String,
    val deviceToken: String? = null,
    val platform: String = "android",
    val appVersion: String,
    val osVersion: String
)

@Serializable
data class DeviceRegistrationResponse(
    val deviceId: String,
    val registered: Boolean,
    val message: String? = null
)

@Serializable
enum class NotificationType {
    NEW_ROUTE,
    ROUTE_UPDATE,
    ATTRACTION_UPDATE,
    SYSTEM,
    PROMOTION
}

@Serializable
data class NotificationResponse(
    val id: String,
    val title: String,
    val body: String,
    val type: NotificationType,
    val data: Map<String, String>? = null,
    val timestamp: Long,
    val read: Boolean = false
)
