package com.trusttheroute.app.domain.model

import org.junit.Assert.*
import org.junit.Test

class RouteTest {

    @Test
    fun `test Route creation with all fields`() {
        val route = Route(
            id = "route1",
            number = "67",
            name = "Автобус Б",
            description = "Описание маршрута",
            polyline = "encoded_polyline_string",
            attractions = emptyList(),
            history = "История маршрута",
            attractionsDescription = "Описание достопримечательностей",
            stops = listOf("Остановка 1", "Остановка 2"),
            duration = "45 минут",
            interval = "15-20 минут",
            startPoint = "Центральная площадь",
            averageRating = 4.5
        )

        assertEquals("route1", route.id)
        assertEquals("67", route.number)
        assertEquals("Автобус Б", route.name)
        assertEquals("Описание маршрута", route.description)
        assertEquals("encoded_polyline_string", route.polyline)
        assertTrue(route.attractions.isEmpty())
        assertEquals("История маршрута", route.history)
        assertEquals(2, route.stops.size)
        assertEquals("45 минут", route.duration)
        assertEquals(4.5, route.averageRating, 0.01)
    }

    @Test
    fun `test Route creation with default values`() {
        val route = Route(
            id = "route1",
            number = "67",
            name = "Автобус Б",
            description = "Описание",
            polyline = "polyline"
        )

        assertTrue("Attractions should be empty by default", route.attractions.isEmpty())
        assertEquals("History should be empty by default", "", route.history)
        assertTrue("Stops should be empty by default", route.stops.isEmpty())
        assertEquals("Duration should be empty by default", "", route.duration)
        assertEquals("Average rating should be 0.0 by default", 0.0, route.averageRating, 0.01)
    }

    @Test
    fun `test Route with attractions`() {
        val attractions = listOf(
            Attraction(
                id = "att1",
                routeId = "route1",
                name = "Достопримечательность 1",
                description = "Описание 1",
                latitude = 55.7558,
                longitude = 37.6173,
                audioUrl = "audio1.mp3"
            ),
            Attraction(
                id = "att2",
                routeId = "route1",
                name = "Достопримечательность 2",
                description = "Описание 2",
                latitude = 55.7520,
                longitude = 37.6155,
                audioUrl = "audio2.mp3"
            )
        )

        val route = Route(
            id = "route1",
            number = "67",
            name = "Автобус Б",
            description = "Описание",
            polyline = "polyline",
            attractions = attractions
        )

        assertEquals("Should have 2 attractions", 2, route.attractions.size)
        assertEquals("att1", route.attractions[0].id)
        assertEquals("att2", route.attractions[1].id)
    }
}
