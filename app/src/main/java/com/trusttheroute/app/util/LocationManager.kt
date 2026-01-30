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

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            intervalMillis
        )
            .setMinUpdateIntervalMillis(intervalMillis)
            .setMaxUpdateDelayMillis(intervalMillis * 2)
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
            if (lastLocation != null && lastLocation.time > System.currentTimeMillis() - 60000) {
                // Если местоположение свежее (менее минуты назад), используем его
                android.util.Log.d("LocationManager", "Используется свежее последнее местоположение")
                return lastLocation
            }
            
            // Если последнее местоположение устарело или отсутствует, запрашиваем новое
            android.util.Log.d("LocationManager", "Запрос нового местоположения...")
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                1000L // 1 секунда
            )
                .setMaxUpdateDelayMillis(5000L) // Максимальная задержка 5 секунд
                .build()
            
            // Используем await для получения одного обновления местоположения
            kotlinx.coroutines.withTimeout(10000L) { // Таймаут 10 секунд
                kotlinx.coroutines.suspendCancellableCoroutine<Location?> { continuation ->
                    val callback = object : LocationCallback() {
                        override fun onLocationResult(result: LocationResult) {
                            result.lastLocation?.let { location ->
                                fusedLocationClient.removeLocationUpdates(this)
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
            android.util.Log.w("LocationManager", "Таймаут при получении местоположения")
            // В случае таймаута возвращаем последнее известное местоположение
            try {
                fusedLocationClient.lastLocation.result
            } catch (ex: Exception) {
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("LocationManager", "Ошибка при получении местоположения", e)
            null
        }
    }

    fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
            locationCallback = null
        }
    }
}
