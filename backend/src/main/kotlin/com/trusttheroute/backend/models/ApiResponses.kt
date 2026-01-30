package com.trusttheroute.backend.models

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val error: String
)

@Serializable
data class MessageResponse(
    val message: String
)
