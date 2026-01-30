# Yandex Push Notifications - Интеграция

## 📋 Обзор

Приложение использует **Yandex Cloud Notification Service (CNS)** для отправки push-уведомлений через собственный бэкенд. На клиенте реализована периодическая проверка новых уведомлений через **WorkManager**.

## 🔄 Архитектура

```
Бэкенд (Yandex CNS) → API → Android App (WorkManager) → YandexPushService → NotificationManager
```

1. **Бэкенд** использует Yandex CNS для отправки push-уведомлений
2. **API** предоставляет endpoints для получения уведомлений
3. **WorkManager** периодически проверяет новые уведомления
4. **YandexPushService** отображает уведомления пользователю
5. **NotificationManager** управляет периодической проверкой

## 📁 Структура файлов

- `app/src/main/java/com/trusttheroute/app/data/api/NotificationApi.kt` - API интерфейс
- `app/src/main/java/com/trusttheroute/app/service/YandexPushService.kt` - Сервис для отображения уведомлений
- `app/src/main/java/com/trusttheroute/app/worker/NotificationWorker.kt` - Worker для периодической проверки
- `app/src/main/java/com/trusttheroute/app/util/NotificationManager.kt` - Утилита для управления проверкой

## 🚀 Использование

### Запуск периодической проверки уведомлений

```kotlin
@HiltViewModel
class SomeViewModel @Inject constructor(
    private val notificationManager: NotificationManager
) : ViewModel() {
    
    fun enableNotifications() {
        notificationManager.startPeriodicNotificationCheck()
    }
    
    fun disableNotifications() {
        notificationManager.stopPeriodicNotificationCheck()
    }
}
```

### Отображение уведомления вручную

```kotlin
@Inject
lateinit var pushService: YandexPushService

fun showNotification(notification: NotificationResponse) {
    pushService.showNotification(notification)
}
```

## ⚙️ Настройка

### 1. Настройка API endpoints

Откройте `app/src/main/java/com/trusttheroute/app/di/NetworkModule.kt` и убедитесь, что `BASE_URL` указывает на ваш бэкенд:

```kotlin
private const val BASE_URL = "https://your-backend.com/api/v1/"
```

### 2. Настройка периодичности проверки

В `app/src/main/java/com/trusttheroute/app/util/NotificationManager.kt` измените интервал:

```kotlin
private const val REPEAT_INTERVAL_MINUTES = 15L // Проверка каждые 15 минут
```

### 3. Настройка бэкенда

Бэкенд должен реализовать следующие endpoints:

- `POST /notifications/register` - Регистрация устройства
- `GET /notifications?lastNotificationId={id}` - Получение уведомлений
- `PUT /notifications/{id}/read` - Отметить как прочитанное
- `PUT /notifications/read-all` - Отметить все как прочитанные

## 🔧 Требования бэкенда

Бэкенд должен использовать **Yandex Cloud Notification Service** для отправки push-уведомлений.

Документация Yandex CNS: https://cloud.yandex.ru/docs/notifications/

## 📝 Типы уведомлений

- `NEW_ROUTE` - Новый маршрут
- `ROUTE_UPDATE` - Обновление маршрута
- `ATTRACTION_UPDATE` - Обновление достопримечательности
- `SYSTEM` - Системное уведомление
- `PROMOTION` - Рекламное/промо уведомление

## ✅ Преимущества решения

1. ✅ Интеграция с экосистемой Яндекса
2. ✅ Соответствие ФЗ-152 (хранение данных в России)
3. ✅ Независимость от Google-сервисов
4. ✅ Гибкость платформ (Android, iOS, Web, RuStore)
5. ✅ Надежная работа через WorkManager

## 🔄 Миграция с Firebase Cloud Messaging

Firebase Cloud Messaging был заменен на Yandex Cloud Notification Service:

- ❌ Удалено: `firebase-messaging-ktx`, `google-services` plugin
- ✅ Добавлено: `work-runtime-ktx`, `hilt-work`
- ✅ Создан: `YandexPushService`, `NotificationWorker`, `NotificationApi`

## 📚 Дополнительная информация

- [Yandex Cloud Notification Service](https://cloud.yandex.ru/docs/notifications/)
- [WorkManager Documentation](https://developer.android.com/topic/libraries/architecture/workmanager)
- [Hilt WorkManager Integration](https://developer.android.com/training/dependency-injection/hilt-android#workmanager)
