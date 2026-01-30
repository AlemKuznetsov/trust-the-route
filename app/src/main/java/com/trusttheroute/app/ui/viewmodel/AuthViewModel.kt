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
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
    data class Message(val message: String) : AuthUiState() // Для сообщений (например, успешный сброс пароля)
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val yandexAuthManager: YandexAuthManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn.asStateFlow()

    init {
        // Сбрасываем состояние авторизации при инициализации
        _authState.value = AuthUiState.Idle
        // Проверяем статус авторизации синхронно при инициализации для правильного startDestination
        // Используем runBlocking для синхронной проверки при первом запуске
        runBlocking {
            try {
                val loggedIn = authRepository.isLoggedIn()
                _isLoggedIn.value = loggedIn
                android.util.Log.d("AuthViewModel", "Статус авторизации определен при инициализации: $loggedIn")
            } catch (e: Exception) {
                android.util.Log.e("AuthViewModel", "Ошибка при проверке статуса авторизации в init", e)
                _isLoggedIn.value = false
            }
        }
    }

    fun checkAuthStatus() {
        viewModelScope.launch {
            try {
                val loggedIn = authRepository.isLoggedIn()
                _isLoggedIn.value = loggedIn
            } catch (e: Exception) {
                _isLoggedIn.value = false
            }
        }
    }

    fun loginWithYandex(activity: Activity) {
        viewModelScope.launch {
            try {
                // НЕ устанавливаем Loading здесь, так как браузер должен открыться
                // Loading будет установлен только после успешного callback
                yandexAuthManager.startAuth(activity)
                // После открытия браузера состояние остается Idle
                // Success будет установлен только после успешного callback в YandexOAuthActivity
            } catch (e: Exception) {
                _authState.value = AuthUiState.Error("Ошибка при запуске авторизации: ${e.message}")
            }
        }
    }

    fun handleYandexOAuthCallback(uri: Uri) {
        viewModelScope.launch {
            try {
                _authState.value = AuthUiState.Loading
                val result = yandexAuthManager.handleAuthCallback(uri)
                
                result.fold(
                    onSuccess = { user ->
                        _authState.value = AuthUiState.Success(user)
                        _isLoggedIn.value = true
                    },
                    onFailure = { error ->
                        _authState.value = AuthUiState.Error(error.message ?: "Ошибка авторизации")
                        _isLoggedIn.value = false
                    }
                )
            } catch (e: Exception) {
                _authState.value = AuthUiState.Error("Ошибка обработки callback: ${e.message}")
                _isLoggedIn.value = false
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthUiState.Loading
                val result = authRepository.login(email, password)
                result.fold(
                    onSuccess = { user ->
                        _authState.value = AuthUiState.Success(user)
                        _isLoggedIn.value = true
                    },
                    onFailure = { error ->
                        _authState.value = AuthUiState.Error(error.message ?: "Ошибка входа")
                    }
                )
            } catch (e: Exception) {
                _authState.value = AuthUiState.Error("Ошибка входа: ${e.message}")
            }
        }
    }

    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthUiState.Loading
                val result = authRepository.register(email, password, name)
                result.fold(
                    onSuccess = { user ->
                        _authState.value = AuthUiState.Success(user)
                        _isLoggedIn.value = true
                    },
                    onFailure = { error ->
                        _authState.value = AuthUiState.Error(error.message ?: "Ошибка регистрации")
                    }
                )
            } catch (e: Exception) {
                _authState.value = AuthUiState.Error("Ошибка регистрации: ${e.message}")
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthUiState.Loading
                val result = authRepository.resetPassword(email)
                result.fold(
                    onSuccess = { message ->
                        _authState.value = AuthUiState.Message(message)
                    },
                    onFailure = { error ->
                        _authState.value = AuthUiState.Error(error.message ?: "Ошибка восстановления пароля")
                    }
                )
            } catch (e: Exception) {
                _authState.value = AuthUiState.Error("Ошибка восстановления пароля: ${e.message}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.logout()
                _isLoggedIn.value = false
                _authState.value = AuthUiState.Idle
            } catch (e: Exception) {
                _authState.value = AuthUiState.Error("Ошибка выхода: ${e.message}")
            }
        }
    }
}
