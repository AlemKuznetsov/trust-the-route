package com.trusttheroute.app.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var locationCallback: LocationCallback? = null

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun getLocationUpdates(intervalMillis: Long = Constants.LOCATION_UPDATE_INTERVAL): Flow<Location> = callbackFlow {
        if (!hasLocationPermission()) {
            close(Exception("Location permission not granted"))
            return@callbackFlow
        }

        // Используем BALANCED_POWER_ACCURACY для баланса между точностью и скоростью
        // Это обеспечивает хорошую точность без излишнего расхода батареи
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY, // Оптимизированный приоритет
            intervalMillis
        )
            .setMinUpdateIntervalMillis(intervalMillis)
            .setMaxUpdateDelayMillis(intervalMillis * 2)
            .setWaitForAccurateLocation(false) // Не ждать высокой точности для быстрого старта
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    trySend(location)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            context.mainLooper
        )

        awaitClose {
            locationCallback?.let {
                fusedLocationClient.removeLocationUpdates(it)
            }
            locationCallback = null
        }
    }

    suspend fun getCurrentLocation(): Location? {
        if (!hasLocationPermission()) {
            return null
        }
        return try {
            // Сначала пытаемся получить последнее известное местоположение
            val lastLocation = fusedLocationClient.lastLocation.result
            val currentTime = System.currentTimeMillis()
            val fiveMinutesAgo = currentTime - 300000L // 5 минут
            
            if (lastLocation != null && lastLocation.time > fiveMinutesAgo) {
                // Если местоположение свежее (менее 5 минут назад), используем его сразу
                android.util.Log.d("LocationManager", "Используется свежее последнее местоположение (${(currentTime - lastLocation.time) / 1000} сек назад)")
                return lastLocation
            }
            
            // Если последнее местоположение устарело или отсутствует, запрашиваем новое
            android.util.Log.d("LocationManager", "Запрос нового местоположения...")
            
            // Используем BALANCED_POWER_ACCURACY для быстрого первого определения
            // Это использует GPS + WiFi + сотовые сети, что быстрее чем только GPS
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY, // Быстрее чем HIGH_ACCURACY
                500L // 0.5 секунды для быстрого ответа
            )
                .setMaxUpdateDelayMillis(3000L) // Максимальная задержка 3 секунды
                .setWaitForAccurateLocation(false) // Не ждать высокой точности для первого определения
                .build()
            
            // Уменьшенный таймаут для быстрого ответа
            kotlinx.coroutines.withTimeout(5000L) { // Таймаут 5 секунд вместо 10
                kotlinx.coroutines.suspendCancellableCoroutine<Location?> { continuation ->
                    val callback = object : LocationCallback() {
                        override fun onLocationResult(result: LocationResult) {
                            result.lastLocation?.let { location ->
                                fusedLocationClient.removeLocationUpdates(this)
                                android.util.Log.d("LocationManager", "Получено новое местоположение: lat=${location.latitude}, lng=${location.longitude}, accuracy=${location.accuracy}m")
                                continuation.resume(location) {}
                            }
                        }
                    }
                    
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        callback,
                        context.mainLooper
                    )
                    
                    continuation.invokeOnCancellation {
                        fusedLocationClient.removeLocationUpdates(callback)
                    }
                }
            }
        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            android.util.Log.w("LocationManager", "Таймаут при получении местоположения, используем последнее известное")
            // В случае таймаута возвращаем последнее известное местоположение (даже если устарело)
            try {
                val fallbackLocation = fusedLocationClient.lastLocation.result
                if (fallbackLocation != null) {
                    android.util.Log.d("LocationManager", "Используется fallback местоположение: lat=${fallbackLocation.latitude}, lng=${fallbackLocation.longitude}")
                }
                fallbackLocation
            } catch (ex: Exception) {
                android.util.Log.e("LocationManager", "Ошибка при получении fallback местоположения", ex)
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("LocationManager", "Ошибка при получении местоположения", e)
            // В случае ошибки тоже пробуем вернуть последнее известное местоположение
            try {
                fusedLocationClient.lastLocation.result
            } catch (ex: Exception) {
                null
            }
        }
    }

    fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
            locationCallback = null
        }
    }
}
