package com.trusttheroute.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TrustTheRouteApplication : Application(), Configuration.Provider {
    
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    
    private val config by lazy {
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
    
    override val workManagerConfiguration: Configuration
        get() = config
    
    override fun onCreate() {
        super.onCreate()
        
        // Инициализация WorkManager с кастомной конфигурацией
        // WorkManager автоматически использует Configuration.Provider если он реализован
        // Явная инициализация не требуется, если провайдер удален из манифеста
        
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
