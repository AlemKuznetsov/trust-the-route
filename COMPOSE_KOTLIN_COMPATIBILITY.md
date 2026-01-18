# Совместимость версий Compose Compiler и Kotlin

## Проблема
```
This version (1.5.4) of the Compose Compiler requires Kotlin version 1.9.20 
but you appear to be using Kotlin version 1.9.22 which is not known to be compatible.
```

## Решение

### Исправлено:
- ✅ Версия Kotlin изменена с **1.9.22** на **1.9.20**
- ✅ Compose Compiler версия **1.5.4** (совместима с Kotlin 1.9.20)

### Файлы изменены:
- `build.gradle.kts` (корневой) - версия Kotlin плагина: `1.9.20`
- `app/build.gradle.kts` - версия Compose Compiler: `1.5.4`

## Таблица совместимости

| Compose Compiler | Kotlin | Статус |
|------------------|--------|--------|
| 1.5.4 | 1.9.20 | ✅ Текущая версия |
| 1.5.4 | 1.9.22 | ❌ Несовместимо |
| 1.5.5+ | 1.9.22 | ✅ Совместимо (если доступно) |

## Что делать дальше

1. **Синхронизируйте проект:**
   - В Android Studio: **File → Sync Project with Gradle Files**
   - Или нажмите на иконку слона в верхней панели

2. **Очистите кэш (если проблема сохраняется):**
   - **File → Invalidate Caches / Restart**
   - Выберите **Invalidate and Restart**

3. **Пересоберите проект:**
   - **Build → Clean Project**
   - **Build → Rebuild Project**

## Альтернативные решения

### Вариант 1: Обновить Compose Compiler (если доступно)
Если хотите использовать Kotlin 1.9.22, обновите Compose Compiler до версии 1.5.5 или выше:

```kotlin
composeOptions {
    kotlinCompilerExtensionVersion = "1.5.5" // или выше
}
```

### Вариант 2: Подавить проверку совместимости (не рекомендуется)
```kotlin
composeOptions {
    kotlinCompilerExtensionVersion = "1.5.4"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        suppressKotlinVersionCompatibilityCheck = true
    }
}
```

⚠️ **Внимание:** Использование `suppressKotlinVersionCompatibilityCheck` может привести к неожиданным ошибкам компиляции.

## Полезные ссылки

- [Официальная таблица совместимости Compose-Kotlin](https://developer.android.com/jetpack/androidx/releases/compose-kotlin)
- [Документация Compose Compiler](https://developer.android.com/jetpack/androidx/releases/compose-compiler)
