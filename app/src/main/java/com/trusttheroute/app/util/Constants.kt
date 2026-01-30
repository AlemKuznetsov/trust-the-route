package com.trusttheroute.app.util

object Constants {
    // Location
    const val MIN_DISTANCE_TO_ATTRACTION = 50 // meters
    const val MAX_DISTANCE_TO_ATTRACTION = 100 // meters
    const val LOCATION_UPDATE_INTERVAL = 2000L // 2 seconds in milliseconds
    
    // API
    const val BASE_URL = "https://api.trusttheroute.com/api/v1/"
    const val CONNECT_TIMEOUT = 30L // seconds
    const val READ_TIMEOUT = 30L // seconds
    const val WRITE_TIMEOUT = 30L // seconds
    
    // Database
    const val DATABASE_NAME = "trust_the_route_database"
    const val DATABASE_VERSION = 1
    
    // Preferences
    const val PREFERENCES_NAME = "app_preferences"
    
    // Cache
    const val CACHE_SIZE = 50 * 1024 * 1024L // 50 MB
    
    // Yandex Object Storage
    const val YANDEX_STORAGE_BASE_URL = "https://storage.yandexcloud.net/trust-the-route-media"
    const val YANDEX_STORAGE_IMAGES_PATH = "images/routes"
    const val YANDEX_STORAGE_AUDIO_PATH = "audio/routes"
    
    // Приоритет загрузки: сначала облако, потом локальные файлы
    const val USE_CLOUD_STORAGE_FIRST = true
}
