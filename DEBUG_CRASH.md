# Отладка вылета приложения

## Способ 1: Просмотр логов через ADB (рекомендуется)

### Шаг 1: Очистите логи
```powershell
cd "$env:LOCALAPPDATA\Android\Sdk\platform-tools"
.\adb logcat -c
```

### Шаг 2: Запустите мониторинг логов
```powershell
.\adb logcat | Select-String -Pattern "AndroidRuntime|FATAL|TrustTheRoute|com.trusttheroute|Exception"
```

### Шаг 3: Запустите приложение на эмуляторе
Откройте приложение "Trust The Route" на эмуляторе

### Шаг 4: Скопируйте ошибку
Когда приложение вылетит, в терминале появится ошибка. Скопируйте её полностью.

---

## Способ 2: Просмотр логов через Android Studio

1. Откройте Android Studio
2. Внизу найдите вкладку **Logcat**
3. Если вкладки нет: **View → Tool Windows → Logcat**
4. В фильтре Logcat выберите: **Show only selected application** или введите фильтр: `package:mine`
5. Запустите приложение на эмуляторе
6. Когда приложение вылетит, в Logcat появится красная ошибка
7. Скопируйте полный текст ошибки (особенно строки с "FATAL EXCEPTION" или "AndroidRuntime")

---

## Способ 3: Быстрая команда для получения последних ошибок

```powershell
cd "$env:LOCALAPPDATA\Android\Sdk\platform-tools"
.\adb logcat -d *:E | Select-Object -Last 100
```

Эта команда покажет последние 100 строк с уровнем ERROR.

---

## Что искать в логах

Ищите строки с:
- `FATAL EXCEPTION`
- `AndroidRuntime`
- `java.lang.RuntimeException`
- `java.lang.NullPointerException`
- `com.trusttheroute.app`
- `Yandex` или `MapKit`
- `IllegalStateException`

---

## Частые причины вылетов

1. **Отсутствие разрешений** - проверьте AndroidManifest.xml
2. **Ошибка инициализации Yandex MapKit** - проверьте API ключ
3. **Ошибка в Application классе** - проверьте TrustTheRouteApplication.kt
4. **Проблемы с зависимостями** - проверьте, что все зависимости загружены
5. **Ошибка в MainActivity** - проверьте onCreate метод

---

## После получения ошибки

Скопируйте полный текст ошибки и отправьте мне - я помогу исправить проблему.
