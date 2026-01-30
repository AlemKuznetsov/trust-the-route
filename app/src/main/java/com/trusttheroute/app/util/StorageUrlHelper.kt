package com.trusttheroute.app.util

/**
 * Утилита для работы с URL медиафайлов из облачного хранилища
 * Поддерживает приоритет: облачные URL -> локальные файлы (fallback)
 */
object StorageUrlHelper {
    
    /**
     * Формирует полный URL для изображения из облачного хранилища
     * @param routeId ID маршрута (например, "bus_b")
     * @param fileName Имя файла изображения
     * @return Полный URL или null если параметры невалидны
     */
    fun getCloudImageUrl(routeId: String, fileName: String): String? {
        if (routeId.isBlank() || fileName.isBlank()) return null
        return "${Constants.YANDEX_STORAGE_BASE_URL}/${Constants.YANDEX_STORAGE_IMAGES_PATH}/$routeId/$fileName"
    }
    
    /**
     * Формирует полный URL для аудио из облачного хранилища
     * @param routeId ID маршрута (например, "bus_b")
     * @param fileName Имя файла аудио
     * @return Полный URL или null если параметры невалидны
     */
    fun getCloudAudioUrl(routeId: String, fileName: String): String? {
        if (routeId.isBlank() || fileName.isBlank()) return null
        return "${Constants.YANDEX_STORAGE_BASE_URL}/${Constants.YANDEX_STORAGE_AUDIO_PATH}/$routeId/$fileName"
    }
    
    /**
     * Формирует локальный URL для файла из assets
     * @param filePath Относительный путь к файлу в assets
     * @param isAudio true для аудио, false для изображений
     * @return Локальный URL
     */
    fun getLocalAssetUrl(filePath: String, isAudio: Boolean = false): String {
        val prefix = if (isAudio) "audio" else "images"
        return "file:///android_asset/$prefix/$filePath"
    }
    
    /**
     * Определяет, является ли URL облачным (http/https)
     */
    fun isCloudUrl(url: String): Boolean {
        return url.startsWith("http://") || url.startsWith("https://")
    }
    
    /**
     * Определяет, является ли URL локальным (file://)
     */
    fun isLocalUrl(url: String): Boolean {
        return url.startsWith("file://")
    }
    
    /**
     * Получает приоритетный URL для изображения
     * Сначала проверяет облачные URL, затем локальные
     * @param cloudUrls Список облачных URL
     * @param localPaths Список локальных путей
     * @param routeId ID маршрута (для генерации облачных URL из локальных путей)
     * @return Список URL с приоритетом облачных
     */
    fun getPrioritizedImageUrls(
        cloudUrls: List<String>,
        localPaths: List<String>,
        routeId: String? = null
    ): List<String> {
        val result = mutableListOf<String>()
        
        // Сначала добавляем облачные URL
        cloudUrls.forEach { url ->
            if (url.isNotBlank() && url !in result) {
                result.add(url)
            }
        }
        
        // Затем локальные файлы как fallback
        localPaths.forEach { path ->
            if (path.isNotBlank()) {
                // Если есть routeId, пытаемся сгенерировать облачный URL
                if (routeId != null && Constants.USE_CLOUD_STORAGE_FIRST) {
                    val cloudUrl = getCloudImageUrl(routeId, path)
                    if (cloudUrl != null && cloudUrl !in result) {
                        result.add(cloudUrl)
                    } else {
                        // Если облачный URL не сгенерирован, используем локальный
                        val localUrl = getLocalAssetUrl(path, isAudio = false)
                        if (localUrl !in result) {
                            result.add(localUrl)
                        }
                    }
                } else {
                    // Просто добавляем локальный URL
                    val localUrl = getLocalAssetUrl(path, isAudio = false)
                    if (localUrl !in result) {
                        result.add(localUrl)
                    }
                }
            }
        }
        
        return result
    }
    
    /**
     * Получает приоритетный URL для аудио
     * Сначала проверяет облачный URL, затем локальный
     * @param cloudUrl Облачный URL
     * @param localPath Локальный путь
     * @param routeId ID маршрута (для генерации облачного URL из локального пути)
     * @return URL с приоритетом облачного
     */
    fun getPrioritizedAudioUrl(
        cloudUrl: String?,
        localPath: String?,
        routeId: String? = null
    ): String {
        // Сначала проверяем облачный URL
        if (!cloudUrl.isNullOrBlank()) {
            return cloudUrl
        }
        
        // Если есть локальный путь и routeId, пытаемся сгенерировать облачный URL
        if (localPath != null && routeId != null && Constants.USE_CLOUD_STORAGE_FIRST) {
            val generatedCloudUrl = getCloudAudioUrl(routeId, localPath)
            if (generatedCloudUrl != null) {
                return generatedCloudUrl
            }
        }
        
        // В конце используем локальный файл
        return if (localPath != null) {
            getLocalAssetUrl(localPath, isAudio = true)
        } else {
            ""
        }
    }
}
