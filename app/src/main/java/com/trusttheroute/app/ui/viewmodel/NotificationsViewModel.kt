package com.trusttheroute.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trusttheroute.app.data.local.PreferencesManager
import com.trusttheroute.app.util.NotificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val notificationManager: NotificationManager
) : ViewModel() {
    
    val notificationsEnabled: StateFlow<Boolean> = preferencesManager.notificationsEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )
    
    init {
        // При инициализации проверяем статус уведомлений и запускаем/останавливаем проверку
        viewModelScope.launch {
            val enabled = notificationsEnabled.value
            if (enabled) {
                notificationManager.startPeriodicNotificationCheck()
            } else {
                notificationManager.stopPeriodicNotificationCheck()
            }
        }
    }
    
    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setNotificationsEnabled(enabled)
            // Запускаем или останавливаем проверку уведомлений
            if (enabled) {
                notificationManager.startPeriodicNotificationCheck()
            } else {
                notificationManager.stopPeriodicNotificationCheck()
            }
        }
    }
}
