package com.trusttheroute.app.ui.viewmodel

import android.app.Activity
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trusttheroute.app.data.auth.YandexAuthManager
import com.trusttheroute.app.data.repository.AuthRepository
import com.trusttheroute.app.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Состояния UI для экранов авторизации
 */
sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val yandexAuthManager: YandexAuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /**
     * Регистрация пользователя через email и пароль
     */
    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.register(email, password, name)
                .onSuccess { user ->
                    _uiState.value = AuthUiState.Success(user)
                }
                .onFailure { error ->
                    val message = when (error) {
                        is com.trusttheroute.app.data.api.AuthException -> error.message ?: "Ошибка регистрации"
                        else -> "Ошибка регистрации: ${error.message ?: "Неизвестная ошибка"}"
                    }
                    _uiState.value = AuthUiState.Error(message)
                }
        }
    }

    /**
     * Вход через email и пароль
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.login(email, password)
                .onSuccess { user ->
                    _uiState.value = AuthUiState.Success(user)
                }
                .onFailure { error ->
                    val message = when (error) {
                        is com.trusttheroute.app.data.api.AuthException -> error.message ?: "Ошибка входа"
                        else -> "Ошибка входа: ${error.message ?: "Неизвестная ошибка"}"
                    }
                    _uiState.value = AuthUiState.Error(message)
                }
        }
    }

    /**
     * Восстановление пароля
     */
    fun resetPassword(email: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.resetPassword(email)
                .onSuccess {
                    _uiState.value = AuthUiState.Success(
                        User(id = "", email = email, name = "")
                    )
                }
                .onFailure { error ->
                    val message = when (error) {
                        is com.trusttheroute.app.data.api.AuthException -> error.message ?: "Ошибка восстановления пароля"
                        else -> "Ошибка восстановления пароля: ${error.message ?: "Неизвестная ошибка"}"
                    }
                    _uiState.value = AuthUiState.Error(message)
                }
        }
    }

    /**
     * Вход через Yandex ID
     */
    fun loginWithYandex(activity: Activity) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            yandexAuthManager.startAuth(activity)
        }
    }

    /**
     * Обработка OAuth callback от Yandex ID
     */
    fun handleYandexOAuthCallback(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.handleYandexOAuthCallback(uri)
                .onSuccess { user ->
                    _uiState.value = AuthUiState.Success(user)
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error(
                        error.message ?: "Ошибка авторизации через Yandex ID"
                    )
                }
        }
    }

    /**
     * Выход из аккаунта
     */
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = AuthUiState.Idle
        }
    }

    /**
     * Проверка статуса авторизации
     */
    fun checkAuthStatus() {
        viewModelScope.launch {
            val isLoggedIn = authRepository.isLoggedIn()
            if (isLoggedIn) {
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    _uiState.value = AuthUiState.Success(user)
                } else {
                    _uiState.value = AuthUiState.Idle
                }
            } else {
                _uiState.value = AuthUiState.Idle
            }
        }
    }

    /**
     * Сброс состояния ошибки
     */
    fun clearError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }
}
