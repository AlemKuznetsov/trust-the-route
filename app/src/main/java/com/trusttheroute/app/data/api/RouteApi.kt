package com.trusttheroute.app.data.api

import com.trusttheroute.app.domain.model.Route
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RouteApi {
    @GET("routes")
    suspend fun getRoutes(): Response<List<Route>>

    @GET("routes/{routeId}")
    suspend fun getRouteById(@Path("routeId") routeId: String): Response<Route>

    @GET("routes/search")
    suspend fun searchRoutes(@Query("query") query: String): Response<List<Route>>
}
