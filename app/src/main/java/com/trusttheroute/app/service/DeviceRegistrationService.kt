package com.trusttheroute.app.service

import android.util.Log
import com.trusttheroute.app.data.api.DeviceRegistrationRequest
import com.trusttheroute.app.data.api.NotificationApi
import com.trusttheroute.app.data.local.PreferencesManager
import com.trusttheroute.app.util.DeviceIdManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Сервис для регистрации устройства на backend для получения push-уведомлений
 */
@Singleton
class DeviceRegistrationService @Inject constructor(
    private val notificationApi: NotificationApi,
    private val deviceIdManager: DeviceIdManager,
    private val preferencesManager: PreferencesManager
) {
    companion object {
        private const val TAG = "DeviceRegistrationService"
        private const val DEVICE_REGISTERED_KEY = "device_registered"
    }

    /**
     * Зарегистрировать устройство на backend
     * Вызывается при входе/регистрации пользователя
     */
    suspend fun registerDevice(): Boolean {
        return try {
            // Проверяем, не зарегистрировано ли уже устройство
            val isRegistered = preferencesManager.getDeviceRegistered().map { it }.first()
            if (isRegistered) {
                Log.d(TAG, "Device already registered")
                return true
            }

            val deviceId = deviceIdManager.getDeviceId()
            val appVersion = deviceIdManager.getAppVersion()
            val osVersion = deviceIdManager.getOsVersion()

            val request = DeviceRegistrationRequest(
                deviceId = deviceId,
                deviceToken = null, // Для Yandex CNS не требуется
                platform = "android",
                appVersion = appVersion,
                osVersion = osVersion
            )

            val response = notificationApi.registerDevice(request)

            if (response.isSuccessful && response.body() != null) {
                val registrationResponse = response.body()!!
                if (registrationResponse.registered) {
                    preferencesManager.setDeviceRegistered(true)
                    Log.d(TAG, "Device registered successfully: ${registrationResponse.message}")
                    true
                } else {
                    Log.e(TAG, "Device registration failed: ${registrationResponse.message}")
                    false
                }
            } else {
                Log.e(TAG, "Device registration failed: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error registering device", e)
            false
        }
    }

    /**
     * Отменить регистрацию устройства (при выходе)
     */
    suspend fun unregisterDevice() {
        try {
            preferencesManager.setDeviceRegistered(false)
            Log.d(TAG, "Device unregistered")
        } catch (e: Exception) {
            Log.e(TAG, "Error unregistering device", e)
        }
    }
}
