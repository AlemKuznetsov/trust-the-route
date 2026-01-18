package com.trusttheroute.app.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attractions",
    foreignKeys = [
        ForeignKey(
            entity = RouteEntity::class,
            parentColumns = ["id"],
            childColumns = ["routeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["routeId"])]
)
data class AttractionEntity(
    @PrimaryKey
    val id: String,
    val routeId: String,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrls: String = "[]", // JSON строка со списком URL изображений
    val audioUrl: String,
    val localImagePaths: String = "[]", // JSON строка со списком локальных путей
    val localAudioPath: String? = null,
    val order: Int = 0 // Order along the route
)
