package com.trusttheroute.app

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TrustTheRouteApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            // Установка API ключа Yandex MapKit
            // Ключ должен быть установлен ПЕРЕД инициализацией
            val apiKey = "ac4cbd52-7836-461c-8441-2295322b28eb"
            MapKitFactory.setApiKey(apiKey)
            
            // Инициализация Yandex MapKit
            MapKitFactory.initialize(this)
        } catch (e: Exception) {
            // Логируем ошибку инициализации MapKit
            android.util.Log.e("TrustTheRoute", "Ошибка инициализации Yandex MapKit", e)
            e.printStackTrace()
        }
    }
}
