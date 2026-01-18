# Исправление ошибки KSP

## Проблема
```
Unable to load class 'com.google.devtools.ksp.gradle.KspTaskJvm'
Gradle's dependency cache may be corrupt
```

## Решение

### Шаг 1: Очистите кэш Gradle

**Выполните в PowerShell:**

```powershell
cd "C:\Trust The Route"

# Остановить все процессы Java/Gradle
Get-Process | Where-Object {$_.ProcessName -like "*java*" -or $_.ProcessName -like "*gradle*"} | Stop-Process -Force -ErrorAction SilentlyContinue

# Удалить кэш Gradle
Remove-Item -Path "$env:USERPROFILE\.gradle\caches" -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item -Path "$env:USERPROFILE\.gradle\daemon" -Recurse -Force -ErrorAction SilentlyContinue

# Удалить папки build
Remove-Item -Path "build" -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item -Path "app\build" -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item -Path ".gradle" -Recurse -Force -ErrorAction SilentlyContinue
```

### Шаг 2: Обновлена версия KSP

✅ Версия KSP обновлена с `1.9.20-1.0.14` на `1.9.20-1.0.15`

### Шаг 3: В Android Studio

1. **Закройте Android Studio полностью**

2. **Откройте Android Studio заново**

3. **File → Invalidate Caches / Restart**
   - Выберите **"Invalidate and Restart"**

4. **File → Sync Project with Gradle Files**
   - Gradle загрузит все зависимости заново
   - Это может занять несколько минут

5. **Build → Clean Project**

6. **Build → Assemble Project**

---

## 🔄 Альтернативное решение: Если проблема сохраняется

Если ошибка все еще появляется, попробуйте использовать более стабильную версию KSP:

В `app/build.gradle.kts` замените:
```kotlin
id("com.google.devtools.ksp") version "1.9.20-1.0.15"
```

На:
```kotlin
id("com.google.devtools.ksp") version "1.9.20-1.0.13"
```

Или вернитесь к KAPT с правильными настройками (см. файл `KAPT_FINAL_FIX.md`).

---

## 📝 Проверка версий

Убедитесь, что версии совместимы:
- Kotlin: 1.9.20
- KSP: 1.9.20-1.0.15 (или 1.9.20-1.0.13)
- Gradle: 8.5
- Android Gradle Plugin: 8.2.0

---

**После очистки кэша и перезапуска проблема должна быть решена!**
