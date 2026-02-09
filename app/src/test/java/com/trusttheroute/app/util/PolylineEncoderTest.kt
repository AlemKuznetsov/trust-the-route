package com.trusttheroute.app.util

import com.yandex.mapkit.geometry.Point
import org.junit.Assert.*
import org.junit.Test

class PolylineEncoderTest {

    @Test
    fun `test encode with empty list returns empty string`() {
        val result = PolylineEncoder.encode(emptyList())
        assertEquals("Should return empty string", "", result)
    }

    @Test
    fun `test encode with single point`() {
        val points = listOf(Point(55.7558, 37.6173))
        val result = PolylineEncoder.encode(points)
        
        assertNotNull("Should return encoded string", result)
        assertTrue("Should not be empty", result.isNotEmpty())
    }

    @Test
    fun `test encode with multiple points`() {
        val points = listOf(
            Point(55.7558, 37.6173),
            Point(55.7520, 37.6155),
            Point(55.7500, 37.6100)
        )
        val result = PolylineEncoder.encode(points)
        
        assertNotNull("Should return encoded string", result)
        assertTrue("Should not be empty", result.isNotEmpty())
    }

    @Test
    fun `test encode produces consistent results`() {
        val points = listOf(
            Point(55.7558, 37.6173),
            Point(55.7520, 37.6155)
        )
        
        val result1 = PolylineEncoder.encode(points)
        val result2 = PolylineEncoder.encode(points)
        
        assertEquals("Should produce same result for same input", result1, result2)
    }

    @Test
    fun `test createPolylineFromAttractions with empty list`() {
        val result = PolylineEncoder.createPolylineFromAttractions(emptyList())
        assertEquals("Should return empty string", "", result)
    }

    @Test
    fun `test createPolylineFromAttractions with single attraction`() {
        val attractions = listOf(Pair(55.7558, 37.6173))
        val result = PolylineEncoder.createPolylineFromAttractions(attractions)
        
        assertNotNull("Should return encoded string", result)
        assertTrue("Should not be empty", result.isNotEmpty())
    }

    @Test
    fun `test createPolylineFromAttractions with multiple attractions`() {
        val attractions = listOf(
            Pair(55.7558, 37.6173),
            Pair(55.7520, 37.6155),
            Pair(55.7500, 37.6100)
        )
        val result = PolylineEncoder.createPolylineFromAttractions(attractions)
        
        assertNotNull("Should return encoded string", result)
        assertTrue("Should not be empty", result.isNotEmpty())
    }

    @Test
    fun `test createPolylineFromAttractions preserves order`() {
        val attractions = listOf(
            Pair(55.7558, 37.6173), // First
            Pair(55.7520, 37.6155), // Second
            Pair(55.7500, 37.6100)  // Third
        )
        
        val result1 = PolylineEncoder.createPolylineFromAttractions(attractions)
        val result2 = PolylineEncoder.createPolylineFromAttractions(attractions.reversed())
        
        assertNotEquals("Should produce different results for different order", result1, result2)
    }
}
