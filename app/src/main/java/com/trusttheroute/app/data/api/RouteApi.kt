package com.trusttheroute.app.data.api

import com.trusttheroute.app.domain.model.Route
import com.trusttheroute.app.domain.model.RouteReview
import com.trusttheroute.app.domain.model.SubmitReviewRequest
import com.trusttheroute.app.domain.model.SubmitReviewResponse
import retrofit2.Response
import retrofit2.http.*

interface RouteApi {
    @GET("routes")
    suspend fun getRoutes(): Response<List<Route>>

    @GET("routes/{routeId}")
    suspend fun getRouteById(@Path("routeId") routeId: String): Response<Route>

    @GET("routes/search")
    suspend fun searchRoutes(@Query("query") query: String): Response<List<Route>>
    
    /**
     * Отправка оценки и отзыва маршрута
     * POST /api/v1/routes/{routeId}/review
     */
    @POST("routes/{routeId}/review")
    suspend fun submitReview(
        @Path("routeId") routeId: String,
        @Body request: SubmitReviewRequest
    ): Response<SubmitReviewResponse>
    
    /**
     * Получение отзывов маршрута
     * GET /api/v1/routes/{routeId}/reviews
     */
    @GET("routes/{routeId}/reviews")
    suspend fun getRouteReviews(@Path("routeId") routeId: String): Response<List<RouteReview>>
    
    /**
     * Получение средней оценки маршрута
     * GET /api/v1/routes/{routeId}/rating
     */
    @GET("routes/{routeId}/rating")
    suspend fun getRouteRating(@Path("routeId") routeId: String): Response<Map<String, Double>>
}
