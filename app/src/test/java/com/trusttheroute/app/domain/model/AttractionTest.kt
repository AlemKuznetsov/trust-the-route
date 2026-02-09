package com.trusttheroute.app.domain.model

import org.junit.Assert.*
import org.junit.Test

class AttractionTest {

    @Test
    fun `test Attraction creation with all fields`() {
        val attraction = Attraction(
            id = "att1",
            routeId = "route1",
            name = "Красная площадь",
            description = "Главная площадь Москвы",
            latitude = 55.7558,
            longitude = 37.6173,
            imageUrls = listOf("image1.jpg", "image2.jpg"),
            audioUrl = "audio.mp3",
            localImagePaths = listOf("/local/image1.jpg"),
            localAudioPath = "/local/audio.mp3",
            order = 1
        )

        assertEquals("att1", attraction.id)
        assertEquals("route1", attraction.routeId)
        assertEquals("Красная площадь", attraction.name)
        assertEquals(55.7558, attraction.latitude, 0.0001)
        assertEquals(37.6173, attraction.longitude, 0.0001)
        assertEquals(2, attraction.imageUrls.size)
        assertEquals("audio.mp3", attraction.audioUrl)
        assertEquals(1, attraction.order)
    }

    @Test
    fun `test Attraction creation with default values`() {
        val attraction = Attraction(
            id = "att1",
            routeId = "route1",
            name = "Test",
            description = "Description",
            latitude = 55.7558,
            longitude = 37.6173,
            audioUrl = "audio.mp3"
        )

        assertTrue("ImageUrls should be empty by default", attraction.imageUrls.isEmpty())
        assertTrue("LocalImagePaths should be empty by default", attraction.localImagePaths.isEmpty())
        assertNull("LocalAudioPath should be null by default", attraction.localAudioPath)
        assertEquals("Order should be 0 by default", 0, attraction.order)
    }

    @Test
    fun `test Attraction imageUrl property returns first image`() {
        val attraction = Attraction(
            id = "att1",
            routeId = "route1",
            name = "Test",
            description = "Description",
            latitude = 55.7558,
            longitude = 37.6173,
            imageUrls = listOf("image1.jpg", "image2.jpg"),
            audioUrl = "audio.mp3"
        )

        assertEquals("Should return first image URL", "image1.jpg", attraction.imageUrl)
    }

    @Test
    fun `test Attraction imageUrl property returns empty string when no images`() {
        val attraction = Attraction(
            id = "att1",
            routeId = "route1",
            name = "Test",
            description = "Description",
            latitude = 55.7558,
            longitude = 37.6173,
            audioUrl = "audio.mp3"
        )

        assertEquals("Should return empty string", "", attraction.imageUrl)
    }

    @Test
    fun `test Attraction localImagePath property returns first local path`() {
        val attraction = Attraction(
            id = "att1",
            routeId = "route1",
            name = "Test",
            description = "Description",
            latitude = 55.7558,
            longitude = 37.6173,
            localImagePaths = listOf("/local/image1.jpg", "/local/image2.jpg"),
            audioUrl = "audio.mp3"
        )

        assertEquals("Should return first local image path", "/local/image1.jpg", attraction.localImagePath)
    }

    @Test
    fun `test Attraction localImagePath property returns null when no local images`() {
        val attraction = Attraction(
            id = "att1",
            routeId = "route1",
            name = "Test",
            description = "Description",
            latitude = 55.7558,
            longitude = 37.6173,
            audioUrl = "audio.mp3"
        )

        assertNull("Should return null", attraction.localImagePath)
    }

    @Test
    fun `test Attraction order property`() {
        val attraction1 = Attraction(
            id = "att1",
            routeId = "route1",
            name = "First",
            description = "Description",
            latitude = 55.7558,
            longitude = 37.6173,
            audioUrl = "audio.mp3",
            order = 1
        )

        val attraction2 = Attraction(
            id = "att2",
            routeId = "route1",
            name = "Second",
            description = "Description",
            latitude = 55.7520,
            longitude = 37.6155,
            audioUrl = "audio2.mp3",
            order = 2
        )

        assertTrue("attraction1 should come before attraction2", attraction1.order < attraction2.order)
    }
}
