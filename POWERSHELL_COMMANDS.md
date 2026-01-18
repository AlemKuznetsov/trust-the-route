# Правильные команды PowerShell для проекта

## ⚠️ Важно: Синтаксис PowerShell

В PowerShell команды разделяются точкой с запятой (`;`), а не пробелом.

### ❌ Неправильно:
```powershell
cd "C:\Trust The Route" .\gradlew.bat --stop
```

### ✅ Правильно:
```powershell
cd "C:\Trust The Route"; .\gradlew.bat --stop
```

Или выполните команды отдельно:
```powershell
cd "C:\Trust The Route"
.\gradlew.bat --stop
```

---

## 📋 Полезные команды для проекта

### Остановка Gradle daemon

**Если gradlew.bat существует:**
```powershell
cd "C:\Trust The Route"
.\gradlew.bat --stop
```

**Если gradlew.bat отсутствует (используйте глобальный Gradle):**
```powershell
gradle --stop
```

### Очистка проекта

**Если gradlew.bat существует:**
```powershell
cd "C:\Trust The Route"
.\gradlew.bat clean
```

**Если gradlew.bat отсутствует:**
```powershell
cd "C:\Trust The Route"
gradle clean
```

### Сборка проекта

**Если gradlew.bat существует:**
```powershell
cd "C:\Trust The Route"
.\gradlew.bat build
```

**Если gradlew.bat отсутствует:**
```powershell
cd "C:\Trust The Route"
gradle build
```

### Проверка версии Java

```powershell
java -version
```

### Проверка версии Gradle

**Если gradlew.bat существует:**
```powershell
cd "C:\Trust The Route"
.\gradlew.bat --version
```

**Если gradlew.bat отсутствует:**
```powershell
gradle --version
```

---

## 🔧 Создание Gradle Wrapper (если нужно)

Если файл `gradlew.bat` отсутствует, вы можете создать его:

```powershell
cd "C:\Trust The Route"
gradle wrapper --gradle-version 8.4
```

После этого будут созданы файлы:
- `gradlew.bat` (Windows)
- `gradlew` (Linux/Mac)
- `gradle/wrapper/gradle-wrapper.jar`
- `gradle/wrapper/gradle-wrapper.properties` (уже существует)

---

## 💡 Рекомендации

**Для работы с проектом Android рекомендуется:**

1. **Использовать Android Studio** для всех операций с Gradle:
   - Синхронизация: **File → Sync Project with Gradle Files**
   - Очистка: **Build → Clean Project**
   - Сборка: **Build → Rebuild Project**
   - Остановка daemon: **File → Settings → Build Tools → Gradle → Stop Gradle daemon**

2. **Командная строка используется только для:**
   - Проверки версий
   - Создания wrapper (если нужно)
   - Автоматизации (скрипты)

---

## 🆘 Решение проблем

### Проблема: "gradlew.bat не найден"

**Решение 1:** Используйте Android Studio для всех операций (рекомендуется)

**Решение 2:** Создайте wrapper:
```powershell
cd "C:\Trust The Route"
gradle wrapper --gradle-version 8.4
```

**Решение 3:** Используйте глобальный Gradle:
```powershell
gradle --stop
gradle clean
```

### Проблема: "gradle: команда не найдена"

Установите Gradle или используйте Android Studio (встроенный Gradle).

---

## 📝 Примеры правильного использования

### Пример 1: Остановка daemon и очистка
```powershell
# Переход в директорию проекта
cd "C:\Trust The Route"

# Остановка daemon (если gradlew.bat существует)
.\gradlew.bat --stop

# Или через глобальный Gradle
gradle --stop

# Очистка проекта
.\gradlew.bat clean
# Или
gradle clean
```

### Пример 2: Проверка и сборка
```powershell
cd "C:\Trust The Route"

# Проверка версии
.\gradlew.bat --version

# Сборка проекта
.\gradlew.bat build
```

---

**Примечание:** В большинстве случаев для работы с Android проектом достаточно использовать Android Studio, которая имеет встроенную поддержку Gradle и автоматически управляет daemon процессами.
