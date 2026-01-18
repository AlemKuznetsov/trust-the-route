# Как установить APK без проверки совместимости с 16 KB

## Проблема
Android Studio блокирует установку APK из-за несовместимости Yandex MapKit с 16 KB страницами, даже если эмулятор использует 4 KB.

## Решение: Установка через ADB с игнорированием проверки

### Шаг 1: Соберите APK
1. В Android Studio: **Build → Build Bundle(s) / APK(s) → Build APK(s)**
2. Дождитесь завершения сборки
3. APK будет создан в: `app/build/outputs/apk/debug/app-debug.apk`

### Шаг 2: Установите через ADB с флагом
1. Откройте PowerShell или Command Prompt
2. Перейдите в папку с Android SDK:
   ```powershell
   cd "$env:LOCALAPPDATA\Android\Sdk\platform-tools"
   ```
3. Убедитесь, что эмулятор запущен:
   ```powershell
   .\adb devices
   ```
4. Установите APK с флагом для игнорирования проверки:
   ```powershell
   .\adb install -r --bypass-low-target-sdk-block "C:\Trust The Route\app\build\outputs\apk\debug\app-debug.apk"
   ```
   
   Или попробуйте:
   ```powershell
   .\adb install -r -t "C:\Trust The Route\app\build\outputs\apk\debug\app-debug.apk"
   ```

### Шаг 3: Альтернативный способ - через Android Studio
1. Соберите APK: **Build → Build Bundle(s) / APK(s) → Build APK(s)**
2. После сборки появится уведомление "APK(s) generated successfully"
3. Нажмите **locate** в уведомлении
4. Скопируйте путь к APK
5. В терминале выполните:
   ```powershell
   cd "$env:LOCALAPPDATA\Android\Sdk\platform-tools"
   .\adb install -r -t "путь_к_APK"
   ```

## Альтернативное решение: Обновить Yandex MapKit

Попробуйте обновить Yandex MapKit до последней версии (если доступна):

В `app/build.gradle.kts`:
```kotlin
// Попробуйте последнюю версию
implementation("com.yandex.android:maps.mobile:4.6.0-full")
// или
implementation("com.yandex.android:maps.mobile:4.7.0-full")
```

Затем:
1. **File → Sync Project with Gradle Files**
2. **Build → Clean Project**
3. **Build → Rebuild Project**

## Примечание

Это временное решение для разработки. Для публикации в Google Play после ноября 2025 года потребуется обновить Yandex MapKit до версии с поддержкой 16 KB страниц.
