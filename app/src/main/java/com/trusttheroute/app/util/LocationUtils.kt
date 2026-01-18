package com.trusttheroute.app.util

import android.location.Location
import com.trusttheroute.app.domain.model.Attraction

object LocationUtils {
    /**
     * Вычисляет расстояние между двумя точками в метрах
     */
    fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    /**
     * Проверяет, находится ли пользователь в радиусе достопримечательности (50-100 метров)
     */
    fun isNearAttraction(
        userLat: Double,
        userLon: Double,
        attraction: Attraction,
        minDistance: Int = 50,
        maxDistance: Int = 100
    ): Boolean {
        val distance = calculateDistance(
            userLat,
            userLon,
            attraction.latitude,
            attraction.longitude
        )
        return distance >= minDistance && distance <= maxDistance
    }

    /**
     * Находит ближайшую достопримечательность к пользователю
     */
    fun findNearestAttraction(
        userLat: Double,
        userLon: Double,
        attractions: List<Attraction>
    ): Attraction? {
        if (attractions.isEmpty()) return null

        return attractions.minByOrNull { attraction ->
            calculateDistance(
                userLat,
                userLon,
                attraction.latitude,
                attraction.longitude
            )
        }
    }

    /**
     * Находит все достопримечательности в радиусе
     */
    fun findAttractionsInRadius(
        userLat: Double,
        userLon: Double,
        attractions: List<Attraction>,
        radius: Int = 100
    ): List<Attraction> {
        return attractions.filter { attraction ->
            val distance = calculateDistance(
                userLat,
                userLon,
                attraction.latitude,
                attraction.longitude
            )
            distance <= radius
        }
    }
}
