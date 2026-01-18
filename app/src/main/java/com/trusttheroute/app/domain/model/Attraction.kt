package com.trusttheroute.app.domain.model

data class Attraction(
    val id: String,
    val routeId: String,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrls: List<String> = emptyList(), // Список URL изображений
    val audioUrl: String,
    val localImagePaths: List<String> = emptyList(), // Список локальных путей к изображениям
    val localAudioPath: String? = null,
    val order: Int = 0 // Order along the route
) {
    // Обратная совместимость: получить первое изображение
    val imageUrl: String get() = imageUrls.firstOrNull() ?: ""
    val localImagePath: String? get() = localImagePaths.firstOrNull()
}
