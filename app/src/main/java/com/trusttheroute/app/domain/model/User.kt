package com.trusttheroute.app.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val token: String? = null,
    val authMethod: String? = null // "yandex" или null (для email/password)
)
