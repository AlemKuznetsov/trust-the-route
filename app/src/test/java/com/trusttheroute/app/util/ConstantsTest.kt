package com.trusttheroute.app.util

import org.junit.Assert.*
import org.junit.Test

class ConstantsTest {

    @Test
    fun `test location constants have correct values`() {
        assertEquals("MIN_DISTANCE_TO_ATTRACTION should be 50", 50, Constants.MIN_DISTANCE_TO_ATTRACTION)
        assertEquals("MAX_DISTANCE_TO_ATTRACTION should be 100", 100, Constants.MAX_DISTANCE_TO_ATTRACTION)
        assertEquals("LOCATION_UPDATE_INTERVAL should be 2000ms", 2000L, Constants.LOCATION_UPDATE_INTERVAL)
    }

    @Test
    fun `test API constants have correct values`() {
        assertTrue("BASE_URL should contain api", Constants.BASE_URL.contains("api"))
        assertEquals("CONNECT_TIMEOUT should be 30 seconds", 30L, Constants.CONNECT_TIMEOUT)
        assertEquals("READ_TIMEOUT should be 30 seconds", 30L, Constants.READ_TIMEOUT)
        assertEquals("WRITE_TIMEOUT should be 30 seconds", 30L, Constants.WRITE_TIMEOUT)
    }

    @Test
    fun `test database constants have correct values`() {
        assertEquals("DATABASE_NAME should be correct", "trust_the_route_database", Constants.DATABASE_NAME)
        assertEquals("DATABASE_VERSION should be 1", 1, Constants.DATABASE_VERSION)
    }

    @Test
    fun `test preferences constants have correct values`() {
        assertEquals("PREFERENCES_NAME should be correct", "app_preferences", Constants.PREFERENCES_NAME)
    }

    @Test
    fun `test cache constants have correct values`() {
        val expectedCacheSize = 50 * 1024 * 1024L // 50 MB
        assertEquals("CACHE_SIZE should be 50 MB", expectedCacheSize, Constants.CACHE_SIZE)
    }

    @Test
    fun `test Yandex Storage constants have correct values`() {
        assertTrue("YANDEX_STORAGE_BASE_URL should contain storage", Constants.YANDEX_STORAGE_BASE_URL.contains("storage"))
        assertTrue("YANDEX_STORAGE_IMAGES_PATH should contain images", Constants.YANDEX_STORAGE_IMAGES_PATH.contains("images"))
        assertTrue("YANDEX_STORAGE_AUDIO_PATH should contain audio", Constants.YANDEX_STORAGE_AUDIO_PATH.contains("audio"))
        assertTrue("USE_CLOUD_STORAGE_FIRST should be true", Constants.USE_CLOUD_STORAGE_FIRST)
    }

    @Test
    fun `test distance constants are in correct range`() {
        assertTrue("MIN_DISTANCE should be less than MAX_DISTANCE", 
            Constants.MIN_DISTANCE_TO_ATTRACTION < Constants.MAX_DISTANCE_TO_ATTRACTION)
    }
}
