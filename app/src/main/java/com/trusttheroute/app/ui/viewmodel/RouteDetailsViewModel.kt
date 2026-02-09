package com.trusttheroute.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trusttheroute.app.data.api.RouteApi
import com.trusttheroute.app.data.repository.RouteRepository
import com.trusttheroute.app.domain.model.Route
import com.trusttheroute.app.domain.model.RouteReview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteDetailsViewModel @Inject constructor(
    private val routeRepository: RouteRepository,
    private val routeApi: RouteApi
) : ViewModel() {

    private val _routeId = MutableStateFlow<String?>(null)
    
    val route: StateFlow<Route?> = _routeId
        .filterNotNull()
        .flatMapLatest { routeId ->
            routeRepository.getRouteById(routeId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val isLoading: StateFlow<Boolean> = combine(
        _routeId,
        route
    ) { routeId, route ->
        routeId != null && route == null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )
    
    private val _reviews = MutableStateFlow<List<RouteReview>>(emptyList())
    val reviews: StateFlow<List<RouteReview>> = _reviews.asStateFlow()
    
    private val _isLoadingReviews = MutableStateFlow(false)
    val isLoadingReviews: StateFlow<Boolean> = _isLoadingReviews.asStateFlow()

    fun loadRoute(routeId: String) {
        _routeId.value = routeId
        loadReviews(routeId)
        loadRating(routeId)
    }
    
    fun refreshReviews(routeId: String) {
        loadReviews(routeId)
        loadRating(routeId)
    }
    
    private fun loadReviews(routeId: String) {
        viewModelScope.launch {
            _isLoadingReviews.value = true
            try {
                android.util.Log.d("RouteDetailsViewModel", "Loading reviews for routeId: $routeId")
                val response = routeApi.getRouteReviews(routeId)
                android.util.Log.d("RouteDetailsViewModel", "Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")
                
                if (response.isSuccessful) {
                    val reviewsList = response.body() ?: emptyList()
                    android.util.Log.d("RouteDetailsViewModel", "Loaded ${reviewsList.size} reviews")
                    reviewsList.forEachIndexed { index, review ->
                        android.util.Log.d("RouteDetailsViewModel", "Review $index: id=${review.id}, rating=${review.rating}, review=${review.review?.take(50)}")
                    }
                    _reviews.value = reviewsList
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("RouteDetailsViewModel", "Failed to load reviews: ${response.code()}, error: $errorBody")
                }
            } catch (e: Exception) {
                android.util.Log.e("RouteDetailsViewModel", "Error loading reviews", e)
                e.printStackTrace()
            } finally {
                _isLoadingReviews.value = false
            }
        }
    }
    
    private fun loadRating(routeId: String) {
        viewModelScope.launch {
            try {
                val response = routeApi.getRouteRating(routeId)
                if (response.isSuccessful) {
                    val ratingMap = response.body()
                    val averageRating = ratingMap?.get("averageRating") ?: 0.0
                    if (averageRating > 0) {
                        // Обновляем рейтинг в базе данных через repository
                        routeRepository.updateRouteRating(routeId, averageRating)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("RouteDetailsViewModel", "Error loading rating", e)
            }
        }
    }
}
