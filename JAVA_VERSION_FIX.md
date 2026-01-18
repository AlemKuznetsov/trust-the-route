# Исправление ошибки версии Java для Gradle

## Проблема
```
Gradle JVM version incompatible.
This project is configured to use an older Gradle JVM that supports up to version 11 
but the current AGP requires a Gradle JVM that supports version 17.
```

## Причина
Android Gradle Plugin 8.2.0 **требует Java 17**, но в Android Studio был выбран Java 11.

## Решение

### ✅ НЕОБХОДИМО использовать Java 17!

**В Android Studio:**

1. **File → Settings → Build, Execution, Deployment → Build Tools → Gradle**
2. В поле **Gradle JDK** выберите **Java 17** (или выше)
3. Если Java 17 нет в списке:
   - Нажмите **Download JDK...**
   - Выберите версию **17** (например, **Eclipse Temurin 17**)
   - Нажмите **Download**
   - После загрузки выберите её в списке
4. Нажмите **OK**

### Затем:

1. **File → Sync Project with Gradle Files**
2. **Build → Clean Project**
3. **Build → Assemble Project**

---

## ⚠️ Важно:

- **AGP 8.2.0 требует Java 17+** - это обязательное требование
- **Java 11 не подходит** для этой версии AGP
- **KAPT с Java 17** требует специальных настроек (которые уже применены)

---

## 📝 Текущие настройки (уже применены):

✅ **gradle.properties** - содержит `--add-opens` аргументы для KAPT
✅ **app/build.gradle.kts** - содержит блок `kapt { javacOptions { ... } }`

После выбора Java 17 в настройках Gradle, проект должен работать!

---

## 🔧 Если ошибка KAPT все еще появляется:

Выполните полную очистку (см. файл `FINAL_KAPT_SOLUTION.md`):

```powershell
cd "C:\Trust The Route"

# Остановить все процессы
Get-Process | Where-Object {$_.ProcessName -like "*java*" -or $_.ProcessName -like "*gradle*"} | Stop-Process -Force -ErrorAction SilentlyContinue

# Удалить кэши Gradle
Remove-Item -Path "$env:USERPROFILE\.gradle" -Recurse -Force -ErrorAction SilentlyContinue

# Удалить папки build
Remove-Item -Path "build" -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item -Path "app\build" -Recurse -Force -ErrorAction SilentlyContinue
```

Затем:
1. Закройте Android Studio
2. Откройте заново
3. **File → Invalidate Caches / Restart** → все галочки → **Invalidate and Restart**
4. Выберите **Java 17** в настройках Gradle
5. **File → Sync Project with Gradle Files**
6. **Build → Clean Project**
7. **Build → Assemble Project**

---

**После выбора Java 17 ошибка должна исчезнуть!**
