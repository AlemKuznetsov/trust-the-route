# Миграция с KAPT на KSP (рекомендуемое решение)

## 🎯 Почему KSP?

KSP (Kotlin Symbol Processing) - это современная замена KAPT, которая:
- ✅ Не имеет проблем с модульной системой Java 17+
- ✅ Работает быстрее KAPT
- ✅ Не требует дополнительных настроек для Java 17

## 📋 Шаги миграции:

### Шаг 1: Обновите app/build.gradle.kts

**Замените:**
```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")  // ← УДАЛИТЬ ЭТУ СТРОКУ
    id("com.google.gms.google-services")
}
```

**На:**
```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp") version "1.9.20-1.0.14"  // ← ДОБАВИТЬ ЭТУ СТРОКУ
    id("com.google.gms.google-services")
}
```

### Шаг 2: Удалите блок kapt { ... }

Найдите и **УДАЛИТЕ** весь блок:
```kotlin
    // KAPT configuration for Java 17+ compatibility
    kapt {
        javacOptions {
            option("--add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED")
            // ... остальные строки
        }
    }
```

### Шаг 3: Замените все kapt на ksp в зависимостях

**Найдите все строки с `kapt(` и замените на `ksp(`:**

Было:
```kotlin
    kapt("com.google.dagger:hilt-android-compiler:2.48")
    kapt("androidx.room:room-compiler:2.6.1")
    kaptTest("com.google.dagger:hilt-android-compiler:2.48")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.48")
```

Стало:
```kotlin
    ksp("com.google.dagger:hilt-android-compiler:2.48")
    ksp("androidx.room:room-compiler:2.6.1")
    kspTest("com.google.dagger:hilt-android-compiler:2.48")
    kspAndroidTest("com.google.dagger:hilt-android-compiler:2.48")
```

### Шаг 4: Обновите gradle.properties (опционально)

Можно удалить строку с `--add-opens` из `org.gradle.jvmargs`, так как она больше не нужна:

```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
```

(Остальные настройки оставьте как есть)

### Шаг 5: Синхронизируйте и пересоберите

1. **File → Sync Project with Gradle Files**
2. **Build → Clean Project**
3. **Build → Assemble Project**

---

## ✅ Преимущества KSP:

- ✅ Нет проблем с Java 17+
- ✅ Быстрее компиляция
- ✅ Меньше памяти
- ✅ Лучшая поддержка Kotlin

---

## 📝 Полный пример изменений:

См. файл `app/build.gradle.kts` после миграции - все `kapt` будут заменены на `ksp`.

---

**После миграции проблема с KAPT полностью исчезнет!**
