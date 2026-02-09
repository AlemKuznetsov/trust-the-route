package com.trusttheroute.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trusttheroute.app.data.local.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainMenuViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    
    val notificationPermissionRequested: StateFlow<Boolean> = preferencesManager
        .notificationPermissionRequested
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    
    fun setNotificationPermissionRequested(requested: Boolean) {
        viewModelScope.launch {
            preferencesManager.setNotificationPermissionRequested(requested)
        }
    }
}
