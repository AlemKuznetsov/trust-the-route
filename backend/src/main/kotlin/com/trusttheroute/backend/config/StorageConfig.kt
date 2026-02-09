package com.trusttheroute.backend.config

import io.ktor.server.config.*

/**
 * Конфигурация для Yandex Object Storage
 */
object StorageConfig {
    // Получаем из переменных окружения или используем значения по умолчанию
    val bucketName: String = System.getenv("YANDEX_STORAGE_BUCKET") ?: "trust-the-route-media"
    val endpointUrl: String = System.getenv("YANDEX_STORAGE_ENDPOINT") ?: "https://storage.yandexcloud.net"
    val region: String = System.getenv("YANDEX_STORAGE_REGION") ?: "ru-central1"
    val accessKeyId: String = System.getenv("YANDEX_STORAGE_ACCESS_KEY") ?: ""
    val secretAccessKey: String = System.getenv("YANDEX_STORAGE_SECRET_KEY") ?: ""
    
    // Базовый URL для публичного доступа
    val baseUrl: String = "$endpointUrl/$bucketName"
    
    // Пути для разных типов медиафайлов
    val imagesPath: String = "images/routes"
    val audioPath: String = "audio/routes"
    
    // Максимальные размеры файлов (в байтах)
    const val MAX_IMAGE_SIZE = 10 * 1024 * 1024L // 10 MB
    const val MAX_AUDIO_SIZE = 50 * 1024 * 1024L // 50 MB
    
    // Разрешенные типы файлов
    val allowedImageTypes = setOf("image/jpeg", "image/jpg", "image/png", "image/webp")
    val allowedAudioTypes = setOf("audio/mpeg", "audio/mp3", "audio/ogg", "audio/wav")
    
    /**
     * Проверяет, настроена ли конфигурация
     */
    fun isConfigured(): Boolean {
        return accessKeyId.isNotBlank() && secretAccessKey.isNotBlank()
    }
}
