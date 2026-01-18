package com.trusttheroute.app.data.repository

import com.trusttheroute.app.data.api.RouteApi
import com.trusttheroute.app.data.database.dao.AttractionDao
import com.trusttheroute.app.data.database.dao.RouteDao
import com.trusttheroute.app.data.local.RouteDataLoader
import com.trusttheroute.app.data.mapper.toDomain
import com.trusttheroute.app.data.mapper.toEntity
import com.trusttheroute.app.domain.model.Attraction
import com.trusttheroute.app.domain.model.Route
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouteRepository @Inject constructor(
    private val routeApi: RouteApi,
    private val routeDao: RouteDao,
    private val attractionDao: AttractionDao,
    private val routeDataLoader: RouteDataLoader
) {
    fun getAllRoutes(): Flow<List<Route>> {
        return routeDao.getAllRoutes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getRouteById(routeId: String): Flow<Route?> {
        return routeDao.getRouteByIdFlow(routeId).map { entity ->
            entity?.toDomain()
        }
    }

    fun searchRoutes(query: String): Flow<List<Route>> {
        return routeDao.searchRoutes(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun syncRoutes() {
        try {
            // Сначала загружаем маршруты из assets (если база данных пустая)
            val hasRoutes = routeDataLoader.hasRoutes()
            if (!hasRoutes) {
                routeDataLoader.loadRoutesFromAssets()
            }
            
            // Затем пытаемся синхронизировать с API (если доступно)
            try {
                val routes = routeApi.getRoutes().body()
                if (routes != null && routes.isNotEmpty()) {
                    routeDao.insertRoutes(routes.map { it.toEntity() })
                    
                    // Sync attractions for each route
                    routes.forEach { route ->
                        val attractions = route.attractions.map { it.toEntity() }
                        attractionDao.insertAttractions(attractions)
                    }
                }
            } catch (e: Exception) {
                // API недоступно - используем данные из assets
                android.util.Log.d("RouteRepository", "API недоступно, используем данные из assets")
            }
        } catch (e: Exception) {
            // Handle error - use cached data
            android.util.Log.e("RouteRepository", "Ошибка синхронизации маршрутов", e)
        }
    }

    fun getAttractionsByRouteId(routeId: String): Flow<List<Attraction>> {
        android.util.Log.d("RouteRepository", "getAttractionsByRouteId called with routeId: $routeId")
        return attractionDao.getAttractionsByRouteId(routeId).map { entities ->
            android.util.Log.d("RouteRepository", "Found ${entities.size} attraction entities for routeId: $routeId")
            val attractions = entities.map { it.toDomain() }
            android.util.Log.d("RouteRepository", "Mapped to ${attractions.size} attractions")
            attractions
        }
    }
}
