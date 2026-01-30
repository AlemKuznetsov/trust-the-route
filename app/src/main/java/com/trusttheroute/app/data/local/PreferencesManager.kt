package com.trusttheroute.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.trusttheroute.app.domain.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val USER_KEY = stringPreferencesKey("user")
        private val THEME_KEY = stringPreferencesKey("theme")
        private val DARK_THEME_ENABLED_KEY = booleanPreferencesKey("dark_theme_enabled")
        private val LANGUAGE_KEY = stringPreferencesKey("language")
        private val FONT_SIZE_KEY = stringPreferencesKey("font_size")
        private val NOTIFICATIONS_ENABLED_KEY = stringPreferencesKey("notifications_enabled")
        private val AUDIO_GUIDE_ENABLED_KEY = stringPreferencesKey("audio_guide_enabled")
        private val OAUTH_STATE_KEY = stringPreferencesKey("oauth_state")
        private val LAST_NOTIFICATION_ID_KEY = stringPreferencesKey("last_notification_id")
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun getToken(): String? {
        return context.dataStore.data.map { it[TOKEN_KEY] }.first()
    }

    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }

    suspend fun saveUser(user: User) {
        context.dataStore.edit { preferences ->
            preferences[USER_KEY] = gson.toJson(user)
        }
    }

    suspend fun getUser(): User? {
        val userJson = context.dataStore.data.map { it[USER_KEY] }.first()
        return userJson?.let { gson.fromJson(it, User::class.java) }
    }

    suspend fun clearUser() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_KEY)
        }
    }

    suspend fun saveTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }

    val theme: Flow<String> = context.dataStore.data.map { it[THEME_KEY] ?: "system" }
    
    suspend fun setDarkThemeEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_THEME_ENABLED_KEY] = enabled
        }
    }
    
    val isDarkThemeEnabled: Flow<Boolean> = context.dataStore.data.map {
        it[DARK_THEME_ENABLED_KEY] ?: false
    }

    suspend fun saveLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }

    val language: Flow<String> = context.dataStore.data.map { it[LANGUAGE_KEY] ?: "ru" }

    suspend fun saveFontSize(size: String) {
        context.dataStore.edit { preferences ->
            preferences[FONT_SIZE_KEY] = size
        }
    }

    val fontSize: Flow<String> = context.dataStore.data.map { it[FONT_SIZE_KEY] ?: "medium" }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED_KEY] = enabled.toString()
        }
    }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map {
        it[NOTIFICATIONS_ENABLED_KEY]?.toBoolean() ?: true
    }

    suspend fun setAudioGuideEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUDIO_GUIDE_ENABLED_KEY] = enabled.toString()
        }
    }

    val audioGuideEnabled: Flow<Boolean> = context.dataStore.data.map {
        it[AUDIO_GUIDE_ENABLED_KEY]?.toBoolean() ?: true
    }

    suspend fun saveOAuthState(state: String) {
        context.dataStore.edit { preferences ->
            preferences[OAUTH_STATE_KEY] = state
        }
    }

    suspend fun getOAuthState(): String? {
        return context.dataStore.data.map { it[OAUTH_STATE_KEY] }.first()
    }

    suspend fun clearOAuthState() {
        context.dataStore.edit { preferences ->
            preferences.remove(OAUTH_STATE_KEY)
        }
    }

    suspend fun setLastNotificationId(notificationId: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_NOTIFICATION_ID_KEY] = notificationId
        }
    }

    val lastNotificationId: Flow<String?> = context.dataStore.data.map {
        it[LAST_NOTIFICATION_ID_KEY]
    }
}
