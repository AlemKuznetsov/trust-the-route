package com.trusttheroute.app.domain.model

/**
 * Модель оценки и отзыва маршрута
 */
data class RouteReview(
    val id: Int,
    val routeId: String,
    val userId: String,
    val userName: String?,
    val rating: Int, // Оценка от 1 до 5
    val review: String? = null, // Текст отзыва (опционально)
    val visitedAttractions: List<String> = emptyList(), // Список ID посещенных достопримечательностей
    val createdAt: Long // Время создания в миллисекундах
)

/**
 * Запрос на отправку оценки и отзыва
 */
data class SubmitReviewRequest(
    val routeId: String,
    val rating: Int,
    val review: String? = null,
    val visitedAttractions: List<String> = emptyList()
)

/**
 * Ответ при отправке оценки
 */
data class SubmitReviewResponse(
    val success: Boolean,
    val message: String
)
