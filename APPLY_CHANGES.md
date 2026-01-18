# Как применить изменения в Android Studio

## ✅ Проверка: Файлы изменены правильно

Я проверил файлы - изменения применены:

1. ✅ **gradle.properties** - содержит все `--add-opens` аргументы
2. ✅ **app/build.gradle.kts** - содержит блок `kapt { javacOptions { ... } }`

## 🔄 Если Android Studio не видит изменения:

### Шаг 1: Перезагрузите файлы в Android Studio

1. **Закройте все открытые файлы** `gradle.properties` и `app/build.gradle.kts` в редакторе
2. **Откройте файлы заново:**
   - Нажмите **Ctrl+Shift+N** (или **File → Open**)
   - Найдите и откройте `gradle.properties`
   - Найдите и откройте `app/build.gradle.kts`
3. Проверьте, что видите изменения

### Шаг 2: Сохраните все файлы

1. Нажмите **Ctrl+S** (или **File → Save All**)
2. Убедитесь, что файлы сохранены (нет индикатора "*" рядом с именем файла)

### Шаг 3: Синхронизируйте проект

1. **File → Sync Project with Gradle Files**
2. Или нажмите иконку **слона** в верхней панели
3. Дождитесь завершения синхронизации

### Шаг 4: Остановите Gradle daemon

**ВАЖНО:** Gradle daemon должен быть перезапущен, иначе изменения не применятся!

**Способ 1: Через Android Studio**
1. **File → Settings** (или **Ctrl+Alt+S**)
2. **Build, Execution, Deployment → Build Tools → Gradle**
3. Найдите кнопку **"Stop Gradle daemon"** и нажмите её
4. Или просто **закройте Android Studio полностью**

**Способ 2: Через командную строку**
```powershell
cd "C:\Trust The Route"
gradle --stop
```

**Способ 3: Убить процессы вручную**
1. Нажмите **Ctrl+Shift+Esc** (Диспетчер задач)
2. Найдите процессы `java.exe` или `gradle.exe`
3. Завершите их все

### Шаг 5: Перезапустите Android Studio

1. Закройте Android Studio полностью
2. Подождите 5-10 секунд
3. Откройте Android Studio заново
4. Откройте проект

### Шаг 6: Очистите кэш

1. **File → Invalidate Caches / Restart**
2. Выберите **"Invalidate and Restart"**
3. Дождитесь перезапуска

### Шаг 7: Синхронизируйте проект снова

1. **File → Sync Project with Gradle Files**
2. Дождитесь завершения

### Шаг 8: Очистите и соберите проект

1. **Build → Clean Project**
2. Дождитесь завершения
3. **Build → Assemble Project**
4. Дождитесь завершения сборки

---

## 🔍 Проверка, что изменения применены:

### Проверка 1: Откройте gradle.properties

Файл должен содержать строку (всё в одной строке):
```
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8 --add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED ...
```

### Проверка 2: Откройте app/build.gradle.kts

После строки `jvmTarget = "17"` должен быть блок:
```kotlin
// KAPT configuration for Java 17+ compatibility
kapt {
    javacOptions {
        option("--add-opens", "jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED")
        option("--add-opens", "jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED")
        // ... и так далее
    }
}
```

### Проверка 3: Проверьте логи сборки

1. Откройте панель **Build** внизу Android Studio
2. При сборке не должно быть ошибок `IllegalAccessError`
3. Если ошибка исчезла - всё работает!

---

## 🆘 Если файлы все еще не изменены:

### Вариант 1: Скопируйте изменения вручную

**gradle.properties:**
Откройте файл и замените строку `org.gradle.jvmargs` на:
```
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8 --add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.jvm=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED
```

**app/build.gradle.kts:**
После строки `jvmTarget = "17"` добавьте:
```kotlin
    // KAPT configuration for Java 17+ compatibility
    kapt {
        javacOptions {
            option("--add-opens", "jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED")
            option("--add-opens", "jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED")
            option("--add-opens", "jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED")
            option("--add-opens", "jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED")
            option("--add-opens", "jdk.compiler/com.sun.tools.javac.jvm=ALL-UNNAMED")
            option("--add-opens", "jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED")
            option("--add-opens", "jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED")
            option("--add-opens", "jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED")
            option("--add-opens", "jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED")
            option("--add-opens", "jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED")
        }
    }
```

### Вариант 2: Проверьте, не открыты ли файлы в другом редакторе

Возможно, файлы открыты в другом окне или редакторе. Закройте все и откройте заново в Android Studio.

---

## 📝 Краткая инструкция (если всё еще не работает):

1. ✅ Закройте Android Studio
2. ✅ Остановите Gradle: `gradle --stop` (в командной строке)
3. ✅ Откройте файлы `gradle.properties` и `app/build.gradle.kts` в любом текстовом редакторе
4. ✅ Убедитесь, что изменения есть
5. ✅ Сохраните файлы
6. ✅ Откройте Android Studio
7. ✅ **File → Invalidate Caches / Restart**
8. ✅ **File → Sync Project with Gradle Files**
9. ✅ **Build → Clean Project**
10. ✅ **Build → Assemble Project**

После этого ошибка должна исчезнуть!
