package com.trusttheroute.backend.routes.routes

import com.trusttheroute.backend.models.ErrorResponse
import com.trusttheroute.backend.models.MessageResponse
import com.trusttheroute.backend.repositories.RouteReviewRepository
import com.trusttheroute.backend.utils.JwtUtils
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class SubmitReviewRequest(
    val routeId: String,
    val rating: Int,
    val review: String? = null,
    val visitedAttractions: List<String> = emptyList()
)

@Serializable
data class SubmitReviewResponse(
    val success: Boolean,
    val message: String
)

@Serializable
data class RouteReviewResponse(
    val id: Int,
    val routeId: String,
    val userId: String,
    val userName: String?,
    val rating: Int,
    val review: String?,
    val visitedAttractions: List<String>,
    val createdAt: Long
)

fun Application.configureRouteRoutes() {
    println("DEBUG: configureRouteRoutes() вызван!")
    val reviewRepository = RouteReviewRepository()
    println("DEBUG: RouteReviewRepository создан")
    
    // Функция для получения userId из токена
    fun getUserIdFromToken(call: ApplicationCall): String? {
        val authHeader = call.request.header("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null
        }
        val token = authHeader.removePrefix("Bearer ")
        val decodedJWT = JwtUtils.verifyToken(token)
        return decodedJWT?.getClaim("userId")?.asString()
    }
    
    routing {
        route("/api/v1/routes") {
            
            /**
             * Отправка оценки и отзыва маршрута
             * POST /api/v1/routes/{routeId}/review
             */
            post("/{routeId}/review") {
                try {
                    val routeId = call.parameters["routeId"] ?: run {
                        println("ERROR: Route ID is missing")
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("Route ID is required")
                        )
                        return@post
                    }
                    
                    println("DEBUG: Received review request for routeId: $routeId")
                    
                    val userId = getUserIdFromToken(call)
                    if (userId == null) {
                        println("ERROR: Authentication failed - no userId")
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            ErrorResponse("Authentication required")
                        )
                        return@post
                    }
                    
                    println("DEBUG: User ID: $userId")
                    
                    val request = call.receive<SubmitReviewRequest>()
                    println("DEBUG: Review request - rating: ${request.rating}, review: ${request.review?.take(50)}")
                    
                    // Валидация
                    if (request.rating < 1 || request.rating > 5) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("Rating must be between 1 and 5")
                        )
                        return@post
                    }
                    
                    if (request.routeId != routeId) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("Route ID mismatch")
                        )
                        return@post
                    }
                    
                    // Сохраняем отзыв
                    println("DEBUG: Attempting to save review to database...")
                    val success = reviewRepository.saveReview(
                        routeId = routeId,
                        userId = userId,
                        rating = request.rating,
                        review = request.review,
                        visitedAttractions = request.visitedAttractions
                    )
                    
                    if (success) {
                        println("DEBUG: Review saved successfully")
                        call.respond(
                            HttpStatusCode.OK,
                            SubmitReviewResponse(
                                success = true,
                                message = "Отзыв успешно отправлен"
                            )
                        )
                    } else {
                        println("ERROR: Failed to save review to database")
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            ErrorResponse("Failed to save review - check database connection and table existence")
                        )
                    }
                } catch (e: Exception) {
                    println("ERROR: Exception in review endpoint: ${e.message}")
                    e.printStackTrace()
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(e.message ?: "Unknown error")
                    )
                }
            }
            
            /**
             * Получение отзывов маршрута
             * GET /api/v1/routes/{routeId}/reviews
             */
            get("/{routeId}/reviews") {
                try {
                    val routeId = call.parameters["routeId"] ?: run {
                        println("ERROR: Route ID is missing in get reviews")
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("Route ID is required")
                        )
                        return@get
                    }
                    
                    println("DEBUG: Getting reviews for routeId: $routeId")
                    val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
                    println("DEBUG: Limit: $limit")
                    
                    val reviews = reviewRepository.getReviewsByRouteId(routeId, limit)
                    println("DEBUG: Found ${reviews.size} reviews from repository")
                    
                    val reviewResponses: List<RouteReviewResponse> = reviews.map { review ->
                        RouteReviewResponse(
                            id = review.id,
                            routeId = review.routeId,
                            userId = review.userId,
                            userName = review.userName,
                            rating = review.rating,
                            review = review.review,
                            visitedAttractions = review.visitedAttractions,
                            createdAt = review.createdAt
                        )
                    }
                    println("DEBUG: Sending ${reviewResponses.size} reviews")
                    call.respond(HttpStatusCode.OK, reviewResponses)
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(e.message ?: "Unknown error")
                    )
                }
            }
            
            /**
             * Получение средней оценки маршрута
             * GET /api/v1/routes/{routeId}/rating
             */
            get("/{routeId}/rating") {
                try {
                    val routeId = call.parameters["routeId"] ?: run {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("Route ID is required")
                        )
                        return@get
                    }
                    
                    val averageRating = reviewRepository.getAverageRating(routeId)
                    call.respond(HttpStatusCode.OK, mapOf<String, Double>("averageRating" to averageRating))
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(e.message ?: "Unknown error")
                    )
                }
            }
        }
    }
}
