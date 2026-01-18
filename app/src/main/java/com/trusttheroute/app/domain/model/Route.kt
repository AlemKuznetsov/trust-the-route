package com.trusttheroute.app.domain.model

data class Route(
    val id: String,
    val number: String,
    val name: String,
    val description: String,
    val polyline: String, // Encoded polyline for route path
    val attractions: List<Attraction> = emptyList(),
    // Дополнительная информация для экрана описания маршрута
    val history: String = "", // История маршрута
    val attractionsDescription: String = "", // Описание достопримечательностей, с которыми познакомится пользователь
    val stops: List<String> = emptyList(), // Список остановок
    val duration: String = "", // Средняя продолжительность поездки (например: "45 минут")
    val interval: String = "", // Интервал автобуса (например: "15-20 минут")
    val startPoint: String = "" // Откуда начинать экскурсию (например: "Остановка 'Центральная площадь'")
)
