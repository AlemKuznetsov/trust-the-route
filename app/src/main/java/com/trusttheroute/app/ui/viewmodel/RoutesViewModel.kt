package com.trusttheroute.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trusttheroute.app.domain.model.Route
import com.trusttheroute.app.data.repository.RouteRepository
import com.trusttheroute.app.data.api.RouteApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutesViewModel @Inject constructor(
    private val routeRepository: RouteRepository,
    private val routeApi: RouteApi
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val routes: StateFlow<List<Route>> = combine(
        routeRepository.getAllRoutes(),
        _searchQuery
    ) { allRoutes, query ->
        if (query.isBlank()) {
            allRoutes
        } else {
            allRoutes.filter {
                it.number.contains(query, ignoreCase = true) ||
                it.name.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadRoutes()
        loadRatings()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
    
    fun refreshRatings() {
        loadRatings()
    }

    private fun loadRoutes() {
        viewModelScope.launch {
            routeRepository.syncRoutes()
        }
    }
    
    private fun loadRatings() {
        viewModelScope.launch {
            try {
                // Получаем текущий список маршрутов один раз
                val currentRoutes = routes.value
                currentRoutes.forEach { route ->
                    try {
                        val response = routeApi.getRouteRating(route.id)
                        if (response.isSuccessful) {
                            val ratingMap = response.body()
                            val averageRating = ratingMap?.get("averageRating") ?: 0.0
                            if (averageRating > 0) {
                                routeRepository.updateRouteRating(route.id, averageRating)
                            }
                        }
                    } catch (e: Exception) {
                        // Игнорируем ошибки загрузки рейтинга для отдельных маршрутов
                        android.util.Log.d("RoutesViewModel", "Error loading rating for ${route.id}: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("RoutesViewModel", "Error loading ratings", e)
            }
        }
    }
}
