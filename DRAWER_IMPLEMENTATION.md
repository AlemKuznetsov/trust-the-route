# Реализация кликабельного слоя для закрытия Drawer меню

## Описание задачи

Необходимо было реализовать функциональность закрытия бургер-меню (ModalNavigationDrawer) при клике на область справа от меню (на видимой части карты), начиная с 280.dp от левого края экрана.

## Что было сделано

### 1. Инициализация
- Создан `ModalNavigationDrawer` с шириной меню 280.dp
- Отключены жесты свайпа (`gesturesEnabled = false`) для предотвращения конфликтов с картой
- Отключен стандартный scrim Material3 (`scrimColor = Color.Transparent`)

### 2. Реализация кликабельного слоя
- Создан прозрачный кликабельный слой, размещенный справа от меню
- Слой начинается с 280.dp от левого края экрана (ширина меню)
- Использован `pointerInput` с `detectTapGestures` для обработки кликов
- Слой размещен на уровне Box, оборачивающего `ModalNavigationDrawer`, чтобы быть выше в z-order

### 3. Обработка закрытия меню
- При клике на слой вызывается `drawerState.close()` в корутине
- Добавлена проверка `drawerState.isOpen` перед закрытием
- Добавлено логирование для отладки

## Проблемы и их решения

### Проблема 1: Стандартный scrim Material3 перехватывал клики
**Симптомы:**
- Слой был виден, но клики не доходили до обработчика
- В логах не появлялись сообщения о кликах на слой

**Причина:**
- `ModalNavigationDrawer` создает свой scrim (затемняющий слой), который находится выше контента и перехватывает все события касания

**Решение:**
- Установлен `scrimColor = Color.Transparent` для отключения стандартного scrim
- Создан собственный кликабельный слой вместо стандартного scrim

### Проблема 2: Слой находился внутри content блока ModalNavigationDrawer
**Симптомы:**
- События касания не доходили до обработчика слоя
- В логах появлялись только системные события (TaplEvents), но не наши логи

**Причина:**
- Слой был размещен внутри content блока `ModalNavigationDrawer`, что не позволяло ему перехватывать события выше drawer

**Решение:**
- Перемещен слой на уровень Box, оборачивающего `ModalNavigationDrawer`
- Слой теперь объявлен после `ModalNavigationDrawer`, что обеспечивает правильный z-order (элементы, объявленные позже, отрисовываются выше)

### Проблема 3: InputListener карты перехватывал клики
**Симптомы:**
- Клики на карту обрабатывались `YandexMapView`, но не доходили до нашего слоя

**Причина:**
- В `YandexMapView` был установлен `InputListener` для обработки кликов на карту, который перехватывал все события

**Решение:**
- Убран параметр `onMapClick` из `YandexMapView` (установлен в `null`)
- Теперь клики обрабатываются только нашим кликабельным слоем

### Проблема 4: Unresolved reference 'zIndex'
**Симптомы:**
- Ошибка компиляции: `Unresolved reference 'zIndex'`

**Причина:**
- Попытка использовать `zIndex` для поднятия слоя выше, но импорт был неправильным или функция недоступна в используемой версии

**Решение:**
- Убран `zIndex` - вместо этого используется порядок объявления элементов в Box
- Элементы, объявленные позже в Box, автоматически отрисовываются выше

### Проблема 5: Старый обработчик OnTouchListener конфликтовал
**Симптомы:**
- Старый код с `DisposableEffect` и `OnTouchListener` перехватывал все касания на уровне View

**Решение:**
- Удален старый код с `DisposableEffect` и `OnTouchListener`
- Оставлен только новый подход с `pointerInput` и `detectTapGestures`

## Финальная реализация

```kotlin
Box(modifier = Modifier.fillMaxSize()) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = false,
        scrimColor = Color.Transparent,
        drawerContent = { /* ... */ }
    ) {
        // Основной контент (карта, TopAppBar и т.д.)
    }
    
    // Прозрачный кликабельный слой справа от меню
    if (isDrawerOpen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 280.dp)
                .pointerInput(Unit) {
                    detectTapGestures {
                        scope.launch {
                            if (drawerState.isOpen) {
                                drawerState.close()
                            }
                        }
                    }
                }
        )
    }
}
```

## Ключевые моменты реализации

1. **Порядок элементов важен**: Кликабельный слой должен быть объявлен после `ModalNavigationDrawer` в Box
2. **Прозрачный scrim**: Используется `scrimColor = Color.Transparent` для отключения стандартного scrim
3. **pointerInput вместо clickable**: `pointerInput` с `detectTapGestures` более надежно перехватывает события
4. **Проверка состояния**: Перед закрытием проверяется `drawerState.isOpen`
5. **Корутина для закрытия**: `drawerState.close()` вызывается в корутине через `scope.launch`

## Файлы, которые были изменены

- `app/src/main/java/com/trusttheroute/app/ui/screens/map/MapScreen.kt`
  - Добавлен кликабельный слой для закрытия drawer
  - Отключен стандартный scrim
  - Убран конфликтующий обработчик кликов на карте

- `app/src/main/java/com/trusttheroute/app/ui/components/YandexMapView.kt`
  - Убран параметр `onMapClick` (установлен в `null`) для предотвращения конфликтов

## Дата реализации

18 января 2026

---

## Другие проблемы и решения в проекте

В процессе разработки приложения были решены и другие проблемы, не связанные напрямую с drawer меню. Эта информация может быть полезна для понимания общего контекста разработки.

### Проблема: KAPT с Java 17
**Статус:** ✅ **РЕШЕНО**

**Проблема:**
- Ошибки компиляции при использовании KAPT (Kotlin Annotation Processing Tool) с Java 17
- Gradle daemon не применял настройки из `gradle.properties`

**Решение:**
- Добавлены настройки `--add-opens` в `gradle.properties` и `app/build.gradle.kts`
- Временное решение: использование Java 11 для компиляции
- Полное решение: очистка кэшей Gradle и перезапуск daemon

**Файлы:**
- `app/build.gradle.kts` - блок `kapt { javacOptions { ... } }`
- `gradle.properties` - аргументы `--add-opens`

**Документация:**
- `KAPT_URGENT_FIX.md` - детальное описание проблемы и решений

---

### Проблема: Yandex MapKit и 16 KB страницы
**Статус:** ✅ **РЕШЕНО (временное решение)**

**Проблема:**
- Yandex MapKit версии 4.5.1 не поддерживает 16 KB страницы памяти
- Современные Android устройства требуют поддержку 16 KB страниц

**Решение:**
- Использован legacy packaging для обхода проблемы
- В `app/build.gradle.kts` добавлено: `jniLibs { useLegacyPackaging = true }`
- TODO: Обновить до версии Yandex MapKit с поддержкой 16 KB когда будет доступна

**Файлы:**
- `app/build.gradle.kts` - блок `packaging { jniLibs { useLegacyPackaging = true } }`

**Комментарий в коде:**
```kotlin
// Временное решение для поддержки 16 KB страниц
// Yandex MapKit 4.5.1 не поддерживает 16 KB страницы
// Используем legacy packaging для обхода проблемы
```

---

### Проблема: Метод hasRoutes() в RouteDataLoader
**Статус:** ✅ **ИСПРАВЛЕНО**

**Проблема:**
- Метод `hasRoutes()` использовал `first()`, который мог выбросить `NoSuchElementException`, если Flow пустой
- Это приводило к крашам при первом запуске приложения

**Решение:**
- Добавлена обработка исключений `NoSuchElementException` и общих `Exception`
- Метод теперь возвращает `false` при пустом Flow или ошибке

**Код до исправления:**
```kotlin
suspend fun hasRoutes(): Boolean {
    return routeDao.getAllRoutes().first().isNotEmpty()
}
```

**Код после исправления:**
```kotlin
suspend fun hasRoutes(): Boolean {
    return try {
        routeDao.getAllRoutes().first().isNotEmpty()
    } catch (e: NoSuchElementException) {
        false
    } catch (e: Exception) {
        android.util.Log.e("RouteDataLoader", "Ошибка проверки наличия маршрутов", e)
        false
    }
}
```

**Файлы:**
- `app/src/main/java/com/trusttheroute/app/data/local/RouteDataLoader.kt`

**Документация:**
- `TESTING_REPORT.md` - описание проблемы и решения

---

### Проблема: Обработка ошибок парсинга JSON
**Статус:** ✅ **ОБРАБОТАНО**

**Проблема:**
- При парсинге JSON файлов маршрутов могли возникать ошибки
- Ошибки не обрабатывались должным образом

**Решение:**
- Добавлена обработка ошибок в методах `parseRouteData()` и `saveRouteData()`
- Ошибки логируются, но не приводят к крашу приложения

**Файлы:**
- `app/src/main/java/com/trusttheroute/app/data/local/RouteDataLoader.kt`

---

### Проблема: Обработка ошибок в YandexMapView
**Статус:** ✅ **ОБРАБОТАНО**

**Проблема:**
- Ошибки при добавлении/удалении listeners карты не обрабатывались
- Ошибки при добавлении маркеров на карту могли привести к крашу

**Решение:**
- Добавлены try-catch блоки для всех операций с картой:
  - Добавление/удаление InputListener
  - Добавление/удаление MapObjectTapListener
  - Добавление маркеров (placemarks) на карту
- Ошибки логируются, но не прерывают работу приложения

**Пример обработки:**
```kotlin
try {
    mapView.map.addInputListener(mapTapListener)
    android.util.Log.d("YandexMapView", "Map tap listener added for drawer close")
} catch (e: Exception) {
    android.util.Log.e("YandexMapView", "Error adding map tap listener", e)
}
```

**Файлы:**
- `app/src/main/java/com/trusttheroute/app/ui/components/YandexMapView.kt`

---

### Проблема: Обработка ошибок в AudioPlayer
**Статус:** ✅ **ОБРАБОТАНО**

**Проблема:**
- Ошибки воспроизведения аудио не обрабатывались должным образом
- Ошибки могли привести к крашу приложения

**Решение:**
- Добавлена обработка ошибок в методе `play()`
- Добавлен listener для обработки ошибок воспроизведения через `onPlayerError`
- Ошибки логируются с детальной информацией (сообщение и код ошибки)

**Пример обработки:**
```kotlin
override fun onPlayerError(error: PlaybackException) {
    android.util.Log.e("AudioPlayer", "Playback error: ${error.message}", error)
    android.util.Log.e("AudioPlayer", "Error type: ${error.errorCode}", error)
}
```

**Файлы:**
- `app/src/main/java/com/trusttheroute/app/util/AudioPlayer.kt`

---

### Проблема: Обработка ошибок геолокации
**Статус:** ⚠️ **ЧАСТИЧНО ОБРАБОТАНО**

**Проблема:**
- Ошибки получения геолокации могут возникать при отсутствии разрешений или отключенном GPS
- Ошибки не всегда обрабатываются корректно

**Решение:**
- Добавлена базовая обработка ошибок в `MapViewModel` и `LocationManager`
- Рекомендуется добавить runtime permissions для запроса разрешений

**Файлы:**
- `app/src/main/java/com/trusttheroute/app/ui/viewmodel/MapViewModel.kt`
- `app/src/main/java/com/trusttheroute/app/util/LocationManager.kt`

**Документация:**
- `DEBUG_LOGCAT_ERRORS.md` - инструкции по отладке ошибок геолокации

---

### Известные ограничения проекта

#### API endpoints не подключены
**Статус:** ⚠️ **ИЗВЕСТНОЕ ОГРАНИЧЕНИЕ**

- Интерфейсы API созданы, но реальные URL endpoints не указаны
- Приложение работает только с данными из assets
- Реализован fallback механизм в `RouteRepository` - проверка доступности API и использование локальных данных

**Файлы:**
- `app/src/main/java/com/trusttheroute/app/data/repository/RouteRepository.kt`
- `app/src/main/java/com/trusttheroute/app/data/api/RouteApi.kt`

#### Аутентификация не реализована
**Статус:** ⚠️ **ИЗВЕСТНОЕ ОГРАНИЧЕНИЕ**

- Экраны аутентификации (Login, Register, ResetPassword) являются только заглушками
- Yandex IAM не интегрирован
- Созданы базовые UI экраны, реализация интеграции отложена на будущее

**Файлы:**
- `app/src/main/java/com/trusttheroute/app/ui/screens/auth/LoginScreen.kt`
- `app/src/main/java/com/trusttheroute/app/ui/screens/auth/RegisterScreen.kt`
- `app/src/main/java/com/trusttheroute/app/ui/screens/auth/ResetPasswordScreen.kt`

---

## Уроки, извлеченные из разработки

### 1. Важность обработки ошибок
Все критические операции должны быть обернуты в try-catch блоки. Это предотвращает краши приложения и улучшает пользовательский опыт.

### 2. Логирование для отладки
Добавление подробного логирования помогает быстро находить и исправлять проблемы. Особенно важно логировать:
- Состояния компонентов
- Ошибки с полным контекстом
- Критические операции (открытие/закрытие меню, загрузка данных и т.д.)

### 3. Порядок элементов в Compose
В Jetpack Compose порядок объявления элементов в контейнере определяет их z-order. Элементы, объявленные позже, отрисовываются выше.

### 4. Совместимость библиотек
При использовании сторонних библиотек важно проверять их совместимость с версией Android и требованиями платформы (например, 16 KB страницы).

### 5. Fallback механизмы
Важно предусматривать fallback механизмы для критических функций (например, использование локальных данных при недоступности API).

---

## Связанные документы

- `DRAWER_TEST_CASES.md` - тесткейсы для функциональности drawer меню
- `TESTING_REPORT.md` - отчет о тестировании приложения
- `АНАЛИЗ_ПРОЕКТА.md` - общий анализ проекта и известные проблемы
- `DEBUG_LOGCAT_ERRORS.md` - инструкции по отладке ошибок в Logcat
- `KAPT_URGENT_FIX.md` - решение проблемы с KAPT и Java 17
- `TEST_REPORT.md` - отчет о тестировании и найденных проблемах
