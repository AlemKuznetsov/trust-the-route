package com.trusttheroute.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routes")
data class RouteEntity(
    @PrimaryKey
    val id: String,
    val number: String,
    val name: String,
    val description: String,
    val polyline: String, // Encoded polyline for route path
    val history: String = "", // История маршрута
    val attractionsDescription: String = "", // Описание достопримечательностей
    val stops: String = "", // JSON строка со списком остановок
    val duration: String = "", // Средняя продолжительность поездки
    val interval: String = "", // Интервал автобуса
    val startPoint: String = "", // Откуда начинать экскурсию
    val lastUpdated: Long = System.currentTimeMillis()
)
