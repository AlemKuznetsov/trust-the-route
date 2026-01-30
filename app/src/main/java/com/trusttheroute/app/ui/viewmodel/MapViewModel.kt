package com.trusttheroute.app.ui.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trusttheroute.app.data.local.PreferencesManager
import com.trusttheroute.app.data.repository.RouteRepository
import com.trusttheroute.app.domain.model.Attraction
import com.trusttheroute.app.domain.model.Route
import com.trusttheroute.app.util.AudioPlayer
import com.trusttheroute.app.util.Constants
import com.trusttheroute.app.util.LocationManager
import com.trusttheroute.app.util.LocationUtils
import com.trusttheroute.app.util.StorageUrlHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapUiState(
    val route: Route? = null,
    val attractions: List<Attraction> = emptyList(),
    val currentLocation: Location? = null,
    val nearbyAttraction: Attraction? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAudioPlaying: Boolean = false,
    val audioGuideEnabled: Boolean = true
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val routeRepository: RouteRepository,
    private val locationManager: LocationManager,
    private val audioPlayer: AudioPlayer,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private var currentPlayingAttractionId: String? = null
    private var isLocationTrackingActive = false
    private var isAttractionCardManuallyOpened = false // Флаг для отслеживания ручного открытия карточки

    init {
        viewModelScope.launch {
            preferencesManager.audioGuideEnabled.collect { enabled ->
                _uiState.value = _uiState.value.copy(audioGuideEnabled = enabled)
            }
        }
    }

    fun loadRoute(routeId: String) {
        android.util.Log.d("MapViewModel", "loadRoute called with routeId: $routeId")
        if (routeId.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "ID маршрута не указан"
            )
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                // Убеждаемся, что данные синхронизированы
                routeRepository.syncRoutes()
                
                routeRepository.getRouteById(routeId)
                    .catch { e ->
                        android.util.Log.e("MapViewModel", "Error loading route", e)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = e.message ?: "Ошибка загрузки маршрута"
                        )
                    }
                    .collect { route ->
                        if (route != null) {
                            android.util.Log.d("MapViewModel", "Route loaded: ${route.id}, ${route.name}")
                            _uiState.value = _uiState.value.copy(
                                route = route,
                                isLoading = false
                            )
                            loadAttractions(routeId)
                        } else {
                            android.util.Log.w("MapViewModel", "Route is null for routeId: $routeId, trying to sync...")
                            // Попробуем синхронизировать еще раз
                            routeRepository.syncRoutes()
                        }
                    }
            } catch (e: Exception) {
                android.util.Log.e("MapViewModel", "Exception in loadRoute", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Ошибка загрузки маршрута"
                )
            }
        }
    }

    private fun loadAttractions(routeId: String) {
        android.util.Log.d("MapViewModel", "Loading attractions for routeId: $routeId")
        viewModelScope.launch {
            routeRepository.getAttractionsByRouteId(routeId)
                .catch { e ->
                    android.util.Log.e("MapViewModel", "Error loading attractions", e)
                    _uiState.value = _uiState.value.copy(
                        error = e.message ?: "Ошибка загрузки достопримечательностей"
                    )
                }
                .collect { attractions ->
                    android.util.Log.d("MapViewModel", "Loaded ${attractions.size} attractions")
                    _uiState.value = _uiState.value.copy(attractions = attractions)
                }
        }
    }

    fun startLocationTracking() {
        if (isLocationTrackingActive) {
            android.util.Log.d("MapViewModel", "Отслеживание местоположения уже активно")
            return
        }
        
        if (!locationManager.hasLocationPermission()) {
            android.util.Log.w("MapViewModel", "Нет разрешения на геолокацию")
            _uiState.value = _uiState.value.copy(
                error = "Необходимо разрешение на геолокацию"
            )
            return
        }

        android.util.Log.d("MapViewModel", "Запуск отслеживания местоположения")
        isLocationTrackingActive = true

        locationManager.getLocationUpdates()
            .onEach { location ->
                android.util.Log.d("MapViewModel", "Получено местоположение: lat=${location.latitude}, lng=${location.longitude}")
                _uiState.value = _uiState.value.copy(currentLocation = location)
                checkNearbyAttractions(location)
            }
            .catch { e ->
                android.util.Log.e("MapViewModel", "Ошибка отслеживания местоположения", e)
                isLocationTrackingActive = false
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Ошибка отслеживания местоположения"
                )
            }
            .launchIn(viewModelScope)
    }

    private fun checkNearbyAttractions(location: Location) {
        val attractions = _uiState.value.attractions
        if (attractions.isEmpty()) return

        val nearbyAttractions = LocationUtils.findAttractionsInRadius(
            location.latitude,
            location.longitude,
            attractions,
            radius = Constants.MAX_DISTANCE_TO_ATTRACTION
        )

        if (nearbyAttractions.isNotEmpty()) {
            val nearest = LocationUtils.findNearestAttraction(
                location.latitude,
                location.longitude,
                nearbyAttractions
            )

            nearest?.let { attraction ->
                // Если это новая достопримечательность или пользователь еще не был рядом
                if (_uiState.value.nearbyAttraction?.id != attraction.id) {
                    // Если карточка была открыта вручную, сбрасываем флаг при смене достопримечательности
                    if (isAttractionCardManuallyOpened && _uiState.value.nearbyAttraction != null) {
                        isAttractionCardManuallyOpened = false
                    }
                    _uiState.value = _uiState.value.copy(nearbyAttraction = attraction)
                    
                    // Автоматическое воспроизведение аудио, если включено
                    if (_uiState.value.audioGuideEnabled) {
                        playAttractionAudio(attraction)
                    }
                }
            }
        } else {
            // Если пользователь удалился от достопримечательности
            // НЕ закрываем карточку автоматически, если она была открыта вручную
            if (_uiState.value.nearbyAttraction != null && !isAttractionCardManuallyOpened) {
                android.util.Log.d("MapViewModel", "Пользователь удалился от достопримечательности, закрываем карточку автоматически")
                stopAudio()
                _uiState.value = _uiState.value.copy(nearbyAttraction = null)
            } else if (_uiState.value.nearbyAttraction != null && isAttractionCardManuallyOpened) {
                android.util.Log.d("MapViewModel", "Пользователь удалился от достопримечательности, но карточка открыта вручную - не закрываем")
            }
        }
    }

    fun playAttractionAudio(attraction: Attraction) {
        // Если это тот же трек и он на паузе - возобновляем
        if (currentPlayingAttractionId == attraction.id && !audioPlayer.isPlaying) {
            android.util.Log.d("MapViewModel", "Resuming paused audio for ${attraction.name}")
            audioPlayer.resume()
            _uiState.value = _uiState.value.copy(isAudioPlaying = true)
            return
        }
        
        // Если уже воспроизводится - ничего не делаем
        if (currentPlayingAttractionId == attraction.id && audioPlayer.isPlaying) {
            android.util.Log.d("MapViewModel", "Audio already playing for ${attraction.name}")
            return
        }

        stopAudio()

        // Используем StorageUrlHelper для получения приоритетного URL
        // Приоритет: облачный URL -> локальный файл (fallback)
        val audioUrl = StorageUrlHelper.getPrioritizedAudioUrl(
            cloudUrl = attraction.audioUrl.takeIf { it.isNotEmpty() },
            localPath = attraction.localAudioPath,
            routeId = attraction.routeId
        )
        
        android.util.Log.d("MapViewModel", "Playing audio for ${attraction.name}, URL: $audioUrl")
        
        if (audioUrl != null) {
            currentPlayingAttractionId = attraction.id
            _uiState.value = _uiState.value.copy(isAudioPlaying = true)
            
            audioPlayer.play(audioUrl) {
                // Когда аудио завершилось
                viewModelScope.launch {
                    _uiState.value = _uiState.value.copy(
                        isAudioPlaying = false
                    )
                    currentPlayingAttractionId = null
                }
            }
        } else {
            android.util.Log.w("MapViewModel", "No audio URL found for ${attraction.name}")
        }
    }

    fun pauseAudio() {
        android.util.Log.d("MapViewModel", "Pausing audio")
        audioPlayer.pause()
        _uiState.value = _uiState.value.copy(isAudioPlaying = false)
    }
    
    fun restartAudio() {
        android.util.Log.d("MapViewModel", "Restarting audio")
        val attraction = _uiState.value.nearbyAttraction
        if (attraction != null) {
            stopAudio()
            playAttractionAudio(attraction)
        }
    }

    fun stopAudio() {
        android.util.Log.d("MapViewModel", "Stopping audio")
        audioPlayer.stop()
        _uiState.value = _uiState.value.copy(isAudioPlaying = false)
        currentPlayingAttractionId = null
    }

    fun selectAttraction(attraction: Attraction) {
        android.util.Log.d("MapViewModel", "selectAttraction called: ${attraction.name}")
        isAttractionCardManuallyOpened = true // Карточка открыта вручную пользователем
        _uiState.value = _uiState.value.copy(nearbyAttraction = attraction)
        android.util.Log.d("MapViewModel", "nearbyAttraction updated: ${_uiState.value.nearbyAttraction?.name}, isManuallyOpened=$isAttractionCardManuallyOpened")
    }

    fun dismissAttractionCard() {
        isAttractionCardManuallyOpened = false // Сбрасываем флаг при закрытии карточки
        _uiState.value = _uiState.value.copy(nearbyAttraction = null)
        stopAudio()
    }

    suspend fun getCurrentLocation(): Location? {
        return try {
            val location = locationManager.getCurrentLocation()
            if (location != null) {
                android.util.Log.d("MapViewModel", "Получено текущее местоположение: lat=${location.latitude}, lng=${location.longitude}")
                _uiState.value = _uiState.value.copy(currentLocation = location)
            }
            location
        } catch (e: Exception) {
            android.util.Log.e("MapViewModel", "Ошибка при получении текущего местоположения", e)
            null
        }
    }

    override fun onCleared() {
        super.onCleared()
        isLocationTrackingActive = false
        locationManager.stopLocationUpdates()
        audioPlayer.release()
    }
}
