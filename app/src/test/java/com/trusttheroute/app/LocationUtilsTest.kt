package com.trusttheroute.app

import com.trusttheroute.app.domain.model.Attraction
import com.trusttheroute.app.util.LocationUtils
import org.junit.Assert.*
import org.junit.Test

class LocationUtilsTest {

    @Test
    fun `test calculateDistance returns correct distance`() {
        // Москва - Красная площадь (примерно)
        val lat1 = 55.7558
        val lon1 = 37.6173
        
        // Москва - Кремль (примерно 500 метров)
        val lat2 = 55.7520
        val lon2 = 37.6155
        
        val distance = LocationUtils.calculateDistance(lat1, lon1, lat2, lon2)
        
        // Проверяем, что расстояние примерно 200-300 метров
        assertTrue("Distance should be between 200 and 300 meters", distance in 200f..300f)
    }

    @Test
    fun `test isNearAttraction returns true when within range`() {
        val attraction = Attraction(
            id = "1",
            routeId = "route1",
            name = "Test Attraction",
            description = "Test",
            latitude = 55.7558,
            longitude = 37.6173,
            imageUrl = "",
            audioUrl = ""
        )
        
        // Пользователь на расстоянии 75 метров
        val userLat = 55.7559
        val userLon = 37.6174
        
        val isNear = LocationUtils.isNearAttraction(userLat, userLon, attraction, 50, 100)
        assertTrue("Should be near attraction", isNear)
    }

    @Test
    fun `test isNearAttraction returns false when outside range`() {
        val attraction = Attraction(
            id = "1",
            routeId = "route1",
            name = "Test Attraction",
            description = "Test",
            latitude = 55.7558,
            longitude = 37.6173,
            imageUrl = "",
            audioUrl = ""
        )
        
        // Пользователь на расстоянии 200 метров
        val userLat = 55.7570
        val userLon = 37.6180
        
        val isNear = LocationUtils.isNearAttraction(userLat, userLon, attraction, 50, 100)
        assertFalse("Should not be near attraction", isNear)
    }

    @Test
    fun `test findNearestAttraction returns closest attraction`() {
        val attractions = listOf(
            Attraction(
                id = "1",
                routeId = "route1",
                name = "Far Attraction",
                description = "Test",
                latitude = 55.7600,
                longitude = 37.6200,
                imageUrl = "",
                audioUrl = ""
            ),
            Attraction(
                id = "2",
                routeId = "route1",
                name = "Near Attraction",
                description = "Test",
                latitude = 55.7558,
                longitude = 37.6173,
                imageUrl = "",
                audioUrl = ""
            )
        )
        
        val userLat = 55.7559
        val userLon = 37.6174
        
        val nearest = LocationUtils.findNearestAttraction(userLat, userLon, attractions)
        
        assertNotNull("Should find nearest attraction", nearest)
        assertEquals("Near Attraction", nearest?.name)
    }
}
