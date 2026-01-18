# Инструкции по настройке проекта

## Предварительные требования

1. **Android Studio** - версия Hedgehog (2023.1.1) или новее
2. **JDK** - версия 17 или выше
3. **Android SDK** - API Level 26 (Android 8.0) и выше

## Шаги настройки

### 1. Клонирование и открытие проекта

```bash
# Если проект в Git репозитории
git clone <repository-url>
cd Trust-The-Route

# Откройте проект в Android Studio
# File -> Open -> выберите папку проекта
```

### 2. Настройка API ключей

#### Yandex MapKit SDK

1. Зарегистрируйтесь на [Yandex Cloud](https://cloud.yandex.ru/)
2. Создайте проект и получите API ключ для MapKit
3. Откройте файл `app/src/main/AndroidManifest.xml`
4. Замените `YOUR_YANDEX_MAPKIT_API_KEY` на ваш реальный API ключ:

```xml
<meta-data
    android:name="com.yandex.mapkit.api_key"
    android:value="ВАШ_API_КЛЮЧ" />
```

#### Firebase Cloud Messaging

1. Создайте проект в [Firebase Console](https://console.firebase.google.com/)
2. Добавьте Android приложение с package name: `com.trusttheroute.app`
3. Скачайте `google-services.json`
4. Замените файл `app/google-services.json` на скачанный файл

#### Yandex IAM (для аутентификации)

1. В Yandex Cloud создайте OAuth приложение
2. Получите Client ID и Client Secret
3. Эти данные будут использоваться при реализации модуля аутентификации

### 3. Синхронизация Gradle

1. В Android Studio нажмите "Sync Project with Gradle Files" (иконка слона)
2. Дождитесь загрузки всех зависимостей
3. Убедитесь, что нет ошибок синхронизации

### 4. Настройка эмулятора или устройства

#### Эмулятор:
- Минимальные требования: Android 8.0 (API 26)
- Рекомендуется: Android 11+ (API 30+)
- RAM: минимум 2GB (для тестирования производительности)

#### Физическое устройство:
- Включите режим разработчика
- Включите USB отладку
- Подключите устройство к компьютеру

### 5. Первый запуск

1. Выберите устройство/эмулятор
2. Нажмите "Run" (Shift+F10)
3. Дождитесь сборки и установки приложения

## Проверка работоспособности

После настройки проверьте:

- [ ] Проект синхронизируется без ошибок
- [ ] Приложение собирается успешно
- [ ] Приложение запускается на устройстве/эмуляторе
- [ ] Отображается базовый экран (MainActivity)

## Возможные проблемы

### Ошибка синхронизации Gradle
- Проверьте подключение к интернету
- Очистите кэш: File -> Invalidate Caches / Restart
- Удалите папку `.gradle` и синхронизируйте снова

### Ошибка компиляции
- Убедитесь, что используется JDK 17
- Проверьте версии зависимостей в `build.gradle.kts`
- Очистите проект: Build -> Clean Project

### Ошибка с Yandex MapKit
- Проверьте правильность API ключа
- Убедитесь, что ключ активирован в Yandex Cloud
- Проверьте, что в манифесте указан правильный package name

### Ошибка с Firebase
- Проверьте, что `google-services.json` находится в папке `app/`
- Убедитесь, что package name в Firebase совпадает с `com.trusttheroute.app`
- Проверьте, что плагин `com.google.gms.google-services` добавлен в `build.gradle.kts`

## Следующие шаги

После успешной настройки проекта следуйте плану разработки в файле [todo.md](todo.md)
