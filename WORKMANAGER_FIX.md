# Исправление ошибок WorkManager и HiltWorkerFactory

## Проблема
Ошибки компиляции:
- `Unresolved reference 'HiltWorkerFactory'`
- `Class 'TrustTheRouteApplication' is not abstract and does not implement abstract member`
- `'workManagerConfiguration' overrides nothing`

## Решение

### Шаг 1: Пересборка проекта
Ошибка `Unresolved reference 'HiltWorkerFactory'` часто возникает из-за того, что KAPT еще не сгенерировал классы. Выполните:

1. **Clean проекта:**
   ```
   Build > Clean Project
   ```

2. **Rebuild проекта:**
   ```
   Build > Rebuild Project
   ```

3. **Invalidate Caches:**
   ```
   File > Invalidate Caches... > Invalidate and Restart
   ```

### Шаг 2: Проверка зависимостей
Убедитесь, что в `app/build.gradle.kts` есть все необходимые зависимости:

```kotlin
// Hilt
implementation("com.google.dagger:hilt-android:2.48")
kapt("com.google.dagger:hilt-android-compiler:2.48")
implementation("androidx.hilt:hilt-work:1.1.0")
kapt("androidx.hilt:hilt-compiler:1.1.0")

// WorkManager
implementation("androidx.work:work-runtime-ktx:2.9.0")
```

### Шаг 3: Проверка манифеста
В `AndroidManifest.xml` должен быть добавлен провайдер для отключения автоматической инициализации WorkManager:

```xml
<provider
    android:name="androidx.startup.InitializationProvider"
    android:authorities="${applicationId}.androidx-startup"
    android:exported="false"
    tools:node="merge">
    <meta-data
        android:name="androidx.work.WorkManagerInitializer"
        android:value="androidx.startup"
        tools:node="remove" />
</provider>
```

### Шаг 4: Проверка аннотаций
Убедитесь, что:
- `TrustTheRouteApplication` имеет аннотацию `@HiltAndroidApp`
- `NotificationWorker` имеет аннотацию `@HiltWorker`
- Используется `@AssistedInject` для конструктора Worker

### Шаг 5: Синхронизация Gradle
После изменений в `build.gradle.kts`:
```
File > Sync Project with Gradle Files
```

## Текущая реализация

Код в `TrustTheRouteApplication.kt` должен выглядеть так:

```kotlin
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
        // ... остальной код
    }
}
```

## Если проблема сохраняется

1. Удалите папки `.gradle` и `build` в корне проекта
2. Выполните `Clean Project` и `Rebuild Project`
3. Проверьте версию Kotlin и убедитесь, что она совместима с Hilt 2.48
4. Убедитесь, что используется KAPT, а не KSP (для Hilt Work нужен KAPT)
