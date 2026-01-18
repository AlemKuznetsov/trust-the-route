# Как остановить Gradle без глобальной установки

## ✅ Решение: Используйте эти команды вместо `gradle --stop`

### Способ 1: Убить процессы Java/Gradle напрямую (рекомендуется)

```powershell
# Остановить все процессы Java (включая Gradle daemon)
Get-Process | Where-Object {$_.ProcessName -like "*java*"} | Stop-Process -Force

# Остановить процессы Gradle (если есть)
Get-Process | Where-Object {$_.ProcessName -like "*gradle*"} | Stop-Process -Force
```

### Способ 2: Удалить кэш Gradle daemon

```powershell
# Удалить кэш Gradle daemon (это заставит его перезапуститься)
Remove-Item -Path "$env:USERPROFILE\.gradle\daemon" -Recurse -Force -ErrorAction SilentlyContinue
```

### Способ 3: Через Диспетчер задач

1. Нажмите **Ctrl+Shift+Esc** (Диспетчер задач)
2. Найдите процессы `java.exe` или `OpenJDK Platform binary`
3. Завершите их все

---

## 🔧 Полный скрипт для очистки (скопируйте и выполните):

```powershell
# Переход в директорию проекта
cd "C:\Trust The Route"

# Остановка всех процессов Java/Gradle
Write-Host "Остановка процессов Java/Gradle..."
Get-Process | Where-Object {$_.ProcessName -like "*java*" -or $_.ProcessName -like "*gradle*"} | Stop-Process -Force -ErrorAction SilentlyContinue

# Удаление кэша Gradle daemon
Write-Host "Удаление кэша Gradle daemon..."
Remove-Item -Path "$env:USERPROFILE\.gradle\daemon" -Recurse -Force -ErrorAction SilentlyContinue

# Удаление папок build
Write-Host "Удаление папок build..."
Remove-Item -Path "build" -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item -Path "app\build" -Recurse -Force -ErrorAction SilentlyContinue

Write-Host "Готово! Теперь закройте Android Studio и откройте заново."
```

---

## 📋 Что делать дальше:

1. **Выполните скрипт выше** (скопируйте весь блок и вставьте в PowerShell)

2. **Закройте Android Studio полностью**
   - File → Exit
   - Убедитесь, что процесс завершен

3. **Откройте Android Studio заново**

4. **File → Invalidate Caches / Restart**
   - Выберите "Invalidate and Restart"

5. **File → Sync Project with Gradle Files**

6. **Build → Clean Project**

7. **Build → Assemble Project**

---

## 🆘 Если проблема все еще сохраняется:

Попробуйте более радикальное решение - см. файл `MIGRATE_TO_KSP.md` или используйте Java 11 временно.
