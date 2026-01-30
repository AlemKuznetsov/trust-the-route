# Исправление авторизации через YandexID

## Проблема
При повторной авторизации через YandexID система автоматически использует тот же аккаунт, не давая пользователю выбрать другой.

## Решение

Добавлены следующие параметры в URL авторизации Yandex OAuth:

1. **`prompt=select_account`** - стандартный параметр OAuth 2.0, который заставляет показать экран выбора аккаунта
2. **`force_confirm=1`** - дополнительный параметр для принудительного показа экрана выбора
3. **Уникальный timestamp** - добавляется как параметр `_` для предотвращения кэширования URL браузером
4. **`setShareState(NO_SHARE)`** - настройка CustomTabs для неиспользования общих данных браузера

## Изменения в коде

### Файл: `YandexAuthManager.kt`

1. Добавлен timestamp для уникальности URL:
```kotlin
val timestamp = System.currentTimeMillis()
```

2. Добавлены параметры в URL авторизации:
```kotlin
.appendQueryParameter("prompt", "select_account")
.appendQueryParameter("force_confirm", "1")
.appendQueryParameter("_", timestamp.toString())
```

3. Обновлен CustomTabsIntent:
```kotlin
val customTabsIntent = CustomTabsIntent.Builder()
    .setShowTitle(true)
    .setShareState(CustomTabsIntent.SHARE_STATE_NO_SHARE) // Не использовать общие данные браузера
    .build()
```

## Тестирование

После применения изменений:

1. **Войдите через YandexID** первый раз
2. **Выйдите из приложения** (logout)
3. **Попробуйте войти снова** через YandexID
4. **Проверьте**, что появляется экран выбора аккаунта Yandex

## Если проблема сохраняется

Если после этих изменений проблема все еще есть, попробуйте:

### Вариант 1: Использовать `prompt=consent` вместо `select_account`
```kotlin
.appendQueryParameter("prompt", "consent") // Заставляет показать экран согласия
```

### Вариант 2: Использовать `prompt=login`
```kotlin
.appendQueryParameter("prompt", "login") // Заставляет повторно ввести пароль
```

### Вариант 3: Убрать `force_confirm` (если он вызывает проблемы)
```kotlin
// Удалите строку с force_confirm
```

### Вариант 4: Использовать обычный Intent вместо CustomTabs
Если CustomTabs кэширует сессию, можно использовать обычный браузер:
```kotlin
val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
activity.startActivity(intent)
```

## Дополнительные рекомендации

1. **Очистка OAuth state при выходе** - уже реализовано в `AuthRepository.logout()`
2. **Проверка логов** - смотрите логи с тегом `YandexAuthManager` для отладки
3. **Тестирование на разных устройствах** - поведение может отличаться в зависимости от браузера по умолчанию

## Логи для отладки

Проверьте логи при авторизации:
```bash
adb logcat | grep YandexAuthManager
```

Обратите внимание на:
- Параметры в URL авторизации
- Сообщения о открытии CustomTabs
- Ошибки при обработке callback
