package com.trusttheroute.app.util

import com.yandex.mapkit.geometry.Point

/**
 * Утилита для кодирования координат в polyline формат
 * Использует Google Polyline Encoding Algorithm
 */
object PolylineEncoder {
    
    /**
     * Кодирует список точек в encoded polyline строку
     */
    fun encode(points: List<Point>): String {
        if (points.isEmpty()) return ""
        
        val encoded = StringBuilder()
        var prevLat = 0
        var prevLng = 0
        
        for (point in points) {
            val lat = (point.latitude * 1e5).toInt()
            val lng = (point.longitude * 1e5).toInt()
            
            val deltaLat = lat - prevLat
            val deltaLng = lng - prevLng
            
            encodeValue(encoded, deltaLat)
            encodeValue(encoded, deltaLng)
            
            prevLat = lat
            prevLng = lng
        }
        
        return encoded.toString()
    }
    
    /**
     * Кодирует одно значение (широта или долгота)
     */
    private fun encodeValue(encoded: StringBuilder, value: Int) {
        var value = value
        // Отрицательные значения
        value = if (value < 0) (value shl 1).inv() else value shl 1
        
        // Разбиваем на 5-битные части
        while (value >= 0x20) {
            encoded.append((0x20 or (value and 0x1F) + 63).toChar())
            value = value shr 5
        }
        
        encoded.append((value + 63).toChar())
    }
    
    /**
     * Создает polyline по координатам достопримечательностей
     * Использует координаты достопримечательностей в порядке их следования
     */
    fun createPolylineFromAttractions(attractions: List<Pair<Double, Double>>): String {
        val points = attractions.map { (lat, lng) ->
            Point(lat, lng)
        }
        return encode(points)
    }
}
