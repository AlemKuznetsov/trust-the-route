# Установка APK через ADB с флагом -t (обход проверки 16 KB)

## Проблема
Android Studio блокирует установку APK из-за несовместимости Yandex MapKit с 16 KB страницами. Установка через ADB с флагом `-t` позволяет обойти эту проверку.

## Пошаговая инструкция

### Шаг 1: Соберите APK в Android Studio

1. Откройте Android Studio
2. Выберите в меню: **Build → Generate App Bundles or APKs → Build APK(s)**
   - Или: **Build → Assemble Project**
3. Дождитесь завершения сборки
4. APK будет создан в: `C:\Trust The Route\app\build\outputs\apk\debug\app-debug.apk`

### Шаг 2: Убедитесь, что эмулятор запущен

1. Запустите эмулятор через Android Studio (Device Manager)
2. Убедитесь, что эмулятор полностью загрузился

### Шаг 3: Откройте PowerShell

1. Нажмите **Win + X**
2. Выберите **Windows PowerShell** или **Терминал**
3. Или найдите "PowerShell" в меню Пуск

### Шаг 4: Перейдите в папку platform-tools

Выполните команду:
```powershell
cd "$env:LOCALAPPDATA\Android\Sdk\platform-tools"
```

Или если у вас другой путь к SDK:
```powershell
cd "C:\Users\ВашеИмя\AppData\Local\Android\Sdk\platform-tools"
```

### Шаг 5: Проверьте подключение эмулятора

Выполните команду:
```powershell
.\adb devices
```

Должно показать что-то вроде:
```
List of devices attached
emulator-5554   device
```

Если эмулятор не виден:
- Убедитесь, что эмулятор полностью загрузился
- Попробуйте перезапустить эмулятор
- Проверьте, что ADB работает: `.\adb kill-server` затем `.\adb start-server`

### Шаг 6: Установите APK с флагом -t

Выполните команду:
```powershell
.\adb install -r -t "C:\Trust The Route\app\build\outputs\apk\debug\app-debug.apk"
```

**Объяснение флагов:**
- `-r` = переустановить (replace) если приложение уже установлено
- `-t` = разрешить установку на устройства с более новым targetSdk (обходит проверку 16 KB)

### Шаг 7: Проверьте результат

После выполнения команды должно появиться:
```
Performing Streamed Install
Success
```

Если появилась ошибка, см. раздел "Решение проблем" ниже.

---

## Полная команда (все в одной строке)

Если хотите выполнить все за один раз:

```powershell
cd "$env:LOCALAPPDATA\Android\Sdk\platform-tools"; .\adb install -r -t "C:\Trust The Route\app\build\outputs\apk\debug\app-debug.apk"
```

---

## Альтернативные пути к APK

Если APK находится в другом месте, используйте полный путь:

```powershell
.\adb install -r -t "полный_путь_к_APK"
```

Например:
```powershell
.\adb install -r -t "C:\Users\ИмяПользователя\Downloads\app-debug.apk"
```

---

## Решение проблем

### Ошибка: "device not found" или "no devices/emulators found"
**Решение:**
1. Убедитесь, что эмулятор запущен и полностью загрузился
2. Выполните: `.\adb kill-server` затем `.\adb start-server`
3. Проверьте: `.\adb devices`

### Ошибка: "INSTALL_FAILED_INSUFFICIENT_STORAGE"
**Решение:**
1. Освободите место на эмуляторе
2. Удалите ненужные приложения
3. В настройках эмулятора увеличьте размер хранилища

### Ошибка: "INSTALL_FAILED_UPDATE_INCOMPATIBLE"
**Решение:**
1. Удалите старое приложение с эмулятора
2. Или используйте флаг `-r` для переустановки

### Ошибка: "APK not found"
**Решение:**
1. Проверьте, что APK собран: `Test-Path "C:\Trust The Route\app\build\outputs\apk\debug\app-debug.apk"`
2. Убедитесь, что путь правильный
3. Пересоберите APK в Android Studio

### Ошибка: "Failure [INSTALL_FAILED_INVALID_APK]"
**Решение:**
1. Пересоберите APK в Android Studio
2. Убедитесь, что сборка прошла без ошибок
3. Попробуйте: `Build → Clean Project` затем `Build → Rebuild Project`

---

## Проверка установки

После успешной установки:

1. Откройте эмулятор
2. Найдите приложение "Trust The Route" в списке приложений
3. Запустите приложение

---

## Быстрая команда для копирования

```powershell
cd "$env:LOCALAPPDATA\Android\Sdk\platform-tools"; .\adb install -r -t "C:\Trust The Route\app\build\outputs\apk\debug\app-debug.apk"
```

Скопируйте эту команду и выполните в PowerShell после сборки APK.

---

## Примечания

- Флаг `-t` позволяет обойти проверку совместимости с 16 KB страницами
- Это временное решение для разработки
- Для публикации в Google Play потребуется обновить Yandex MapKit до версии с поддержкой 16 KB
- После каждого изменения кода нужно пересобрать APK и установить заново
