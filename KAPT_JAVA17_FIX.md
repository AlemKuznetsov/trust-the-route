# Исправление ошибки KAPT с Java 17+

## Проблема
```
java.lang.IllegalAccessError: superclass access check failed: 
class org.jetbrains.kotlin.kapt3.base.javac.KaptJavaCompiler 
cannot access class com.sun.tools.javac.main.JavaCompiler 
because module jdk.compiler does not export com.sun.tools.javac.main to unnamed module
```

## Причина
Начиная с Java 9, была введена модульная система Java (Java Platform Module System), которая ограничивает доступ к внутренним пакетам компилятора. KAPT (Kotlin Annotation Processing Tool) пытается получить доступ к внутренним классам Java компилятора, но модуль `jdk.compiler` не экспортирует эти пакеты для неназванных модулей.

## Решение

### Исправлено:
✅ Добавлены JVM аргументы в `gradle.properties` для открытия необходимых модулей компилятора

### Изменения в `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8 \
  --add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED \
  --add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED \
  --add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED \
  --add-opens=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED \
  --add-opens=jdk.compiler/com.sun.tools.javac.jvm=ALL-UNNAMED \
  --add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED \
  --add-opens=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED \
  --add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED \
  --add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED \
  --add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED
```

Эти аргументы открывают необходимые пакеты модуля `jdk.compiler` для неназванных модулей, что позволяет KAPT работать корректно.

## Что делать дальше

1. **Остановите Gradle daemon через Android Studio:**
   - В Android Studio: **File → Settings → Build, Execution, Deployment → Build Tools → Gradle**
   - Нажмите **Stop Gradle daemon** или закройте Android Studio

2. **Или через командную строку (если gradlew.bat существует):**
   ```powershell
   cd "C:\Trust The Route"
   .\gradlew.bat --stop
   ```
   
   **Если gradlew.bat отсутствует**, используйте глобальный Gradle:
   ```powershell
   gradle --stop
   ```

3. **Синхронизируйте проект:**
   - В Android Studio: **File → Sync Project with Gradle Files**
   - Или нажмите на иконку слона в верхней панели

4. **Очистите кэш:**
   - **File → Invalidate Caches / Restart**
   - Выберите **Invalidate and Restart**

4. **Пересоберите проект:**
   - **Build → Clean Project**
   - **Build → Rebuild Project**

## Альтернативные решения

### Вариант 1: Использовать KSP вместо KAPT (рекомендуется для новых проектов)
KSP (Kotlin Symbol Processing) - более современная альтернатива KAPT, которая не имеет проблем с модульной системой Java.

**Миграция на KSP:**
1. Замените `kotlin-kapt` на `com.google.devtools.ksp` в `build.gradle.kts`
2. Замените `kapt` на `ksp` в зависимостях
3. Обновите версию KSP до последней

**Пример:**
```kotlin
plugins {
    id("com.google.devtools.ksp") version "1.9.20-1.0.14"
}

dependencies {
    ksp("com.google.dagger:hilt-android-compiler:2.48")
    ksp("androidx.room:room-compiler:2.6.1")
}
```

### Вариант 2: Использовать Java 11 (не рекомендуется)
Java 11 не имеет таких строгих ограничений модульной системы, но это устаревшее решение.

## Проверка версии Java

Убедитесь, что используется Java 17 или выше:
```powershell
java -version
```

В Android Studio:
- **File → Settings → Build, Execution, Deployment → Build Tools → Gradle**
- Проверьте поле **Gradle JDK** - должно быть Java 17 или выше

## Полезные ссылки

- [KAPT документация](https://kotlinlang.org/docs/kapt.html)
- [KSP документация](https://kotlinlang.org/docs/ksp-overview.html)
- [Java Module System](https://www.oracle.com/corporate/features/understanding-java-9-modules.html)

## Примечание

Это известная проблема при использовании KAPT с Java 17+. Решение с добавлением `--add-opens` аргументов является стандартным и безопасным способом обхода ограничений модульной системы для инструментов обработки аннотаций.
