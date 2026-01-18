package com.trusttheroute.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trusttheroute.app.data.repository.RouteRepository
import com.trusttheroute.app.domain.model.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class RouteDetailsViewModel @Inject constructor(
    private val routeRepository: RouteRepository
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

    fun loadRoute(routeId: String) {
        _routeId.value = routeId
    }
}
