package com.trusttheroute.app.util

import android.content.Context
import android.provider.Settings
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.deviceIdStore: DataStore<Preferences> by preferencesDataStore(name = "device_id")

@Singleton
class DeviceIdManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val DEVICE_ID_KEY = stringPreferencesKey("device_id")
    }

    /**
     * Получить уникальный ID устройства
     * Использует Android ID, если доступен, иначе генерирует UUID и сохраняет его
     */
    suspend fun getDeviceId(): String {
        // Пытаемся получить сохраненный ID
        val savedId = context.deviceIdStore.data.map { it[DEVICE_ID_KEY] }.first()
        if (!savedId.isNullOrBlank()) {
            return savedId
        }

        // Пытаемся получить Android ID
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        
        val deviceId = if (androidId != null && androidId != "9774d56d682e549c") {
            // Используем Android ID, если он валидный (не дефолтный)
            androidId
        } else {
            // Генерируем UUID и сохраняем его
            UUID.randomUUID().toString()
        }

        // Сохраняем ID для будущего использования
        context.deviceIdStore.edit { preferences ->
            preferences[DEVICE_ID_KEY] = deviceId
        }

        return deviceId
    }

    /**
     * Получить версию приложения
     */
    fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            "1.0.0"
        }
    }

    /**
     * Получить версию ОС
     */
    fun getOsVersion(): String {
        return android.os.Build.VERSION.RELEASE
    }
}
