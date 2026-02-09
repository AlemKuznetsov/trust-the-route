package com.trusttheroute.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trusttheroute.app.data.api.RouteApi
import com.trusttheroute.app.data.local.PreferencesManager
import com.trusttheroute.app.domain.model.SubmitReviewRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReviewUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val rating: Int = 0,
    val review: String = ""
)

@HiltViewModel
class RouteReviewViewModel @Inject constructor(
    private val routeApi: RouteApi,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()
    
    fun setRating(rating: Int) {
        _uiState.value = _uiState.value.copy(rating = rating)
    }
    
    fun setReview(review: String) {
        _uiState.value = _uiState.value.copy(review = review)
    }
    
    fun submitReview(
        routeId: String,
        visitedAttractions: List<String>
    ) {
        val currentState = _uiState.value
        
        android.util.Log.d("RouteReviewViewModel", "Submitting review for routeId: $routeId, rating: ${currentState.rating}")
        
        if (currentState.rating == 0) {
            _uiState.value = currentState.copy(error = "Пожалуйста, выберите оценку")
            return
        }
        
        _uiState.value = currentState.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            try {
                val token = preferencesManager.getToken()
                if (token == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Требуется авторизация"
                    )
                    return@launch
                }
                
                val request = SubmitReviewRequest(
                    routeId = routeId,
                    rating = currentState.rating,
                    review = currentState.review.takeIf { it.isNotBlank() },
                    visitedAttractions = visitedAttractions
                )
                
                android.util.Log.d("RouteReviewViewModel", "Sending request: routeId=$routeId, rating=${request.rating}, review=${request.review?.take(50)}")
                val response = routeApi.submitReview(routeId, request)
                android.util.Log.d("RouteReviewViewModel", "Response code: ${response.code()}, isSuccessful: ${response.isSuccessful()}")
                
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        error = null
                    )
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = when (response.code()) {
                        404 -> "Endpoint не найден. Проверьте, что таблица route_reviews создана в БД."
                        401 -> "Требуется авторизация"
                        400 -> "Неверный запрос: ${errorBody ?: response.message()}"
                        500 -> "Ошибка сервера: ${errorBody ?: response.message()}"
                        else -> "Ошибка ${response.code()}: ${errorBody ?: response.message()}"
                    }
                    
                    android.util.Log.e("RouteReviewViewModel", "Ошибка отправки отзыва: ${response.code()} - $errorMessage")
                    android.util.Log.e("RouteReviewViewModel", "Response body: $errorBody")
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Ошибка при отправке отзыва"
                )
            }
        }
    }
    
    fun reset() {
        _uiState.value = ReviewUiState()
    }
}
