# Инструкции по исправлению ошибки Gradle

## Проблема
```
A problem occurred configuring project ':app'.
> Failed to notify project evaluation listener.
   > 'org.gradle.api.file.FileCollection org.gradle.api.artifacts.Configuration.fileCollection(org.gradle.api.specs.Spec)'
```

## Решение

### 1. Исправления, которые уже применены:

✅ **Создан Gradle Wrapper** (`gradle/wrapper/gradle-wrapper.properties`)
- Версия Gradle: 8.4 (совместима с Android Gradle Plugin 8.2.0)

✅ **Обновлена версия Kotlin**
- С 1.9.20 до 1.9.22 (лучшая совместимость)

✅ **Обновлены настройки**
- Добавлен toolchains resolver в `settings.gradle.kts`
- Обновлены настройки в `gradle.properties`

### 2. Дополнительные шаги для исправления:

#### Шаг 1: Очистка кэша Gradle

В Android Studio:
1. File → Invalidate Caches / Restart
2. Выберите "Invalidate and Restart"

Или через командную строку:
```powershell
cd "C:\Trust The Route"
.\gradlew.bat clean --no-daemon
.\gradlew.bat --stop
```

#### Шаг 2: Удаление папок сборки

Удалите следующие папки (если они существуют):
- `.gradle` (в корне проекта)
- `build` (в корне проекта)
- `app/build` (в папке app)

#### Шаг 3: Синхронизация проекта

В Android Studio:
1. File → Sync Project with Gradle Files
2. Дождитесь завершения синхронизации

#### Шаг 4: Если проблема сохраняется

Попробуйте обновить версию Android Gradle Plugin до более новой:

В `build.gradle.kts` (корневой файл):
```kotlin
plugins {
    id("com.android.application") version "8.3.0" apply false  // вместо 8.2.0
    // ...
}
```

И обновите Gradle Wrapper в `gradle/wrapper/gradle-wrapper.properties`:
```
distributionUrl=https\://services.gradle.org/distributions/gradle-8.4-bin.zip
```

### 3. Альтернативное решение (если проблема сохраняется)

Если ошибка все еще возникает, попробуйте:

1. **Откатить версию AGP:**
   - Измените версию AGP с 8.2.0 на 8.1.4
   - Измените версию Gradle на 8.0

2. **Или обновить до последних версий:**
   - AGP: 8.3.0 или выше
   - Gradle: 8.4 или выше
   - Kotlin: 1.9.22 или выше

### 4. Проверка версий

Убедитесь, что версии совместимы:

| Компонент | Версия | Совместимость |
|-----------|--------|---------------|
| Android Gradle Plugin | 8.2.0 | ✅ |
| Gradle | 8.4 | ✅ (требуется 8.2+) |
| Kotlin | 1.9.22 | ✅ |
| Compose Compiler | 1.5.4 | ✅ |

### 5. Дополнительная диагностика

Если проблема не решается, запустите Gradle с подробным выводом:

```powershell
.\gradlew.bat --stacktrace --info
```

Это поможет увидеть точную причину ошибки.

## После исправления

После успешной синхронизации:
1. Убедитесь, что все зависимости загружены
2. Проверьте, что проект компилируется без ошибок
3. Запустите приложение на эмуляторе или устройстве

## Полезные ссылки

- [Gradle Compatibility Matrix](https://developer.android.com/studio/releases/gradle-plugin#updating-gradle)
- [Kotlin Compatibility](https://kotlinlang.org/docs/compatibility-guide.html)
