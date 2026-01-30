# Исправление метода deleteUser - PowerShell скрипты

## Быстрый старт

### Вариант 1: Полностью автоматический (рекомендуется)

Просто запустите скрипт - он загрузит файл и выполнит все команды на сервере:

```powershell
cd "C:\Trust The Route"
.\backend\fix_deleteuser_auto.ps1
```

Скрипт автоматически:
1. ✅ Загрузит исправленный файл на сервер
2. ✅ Остановит сервис
3. ✅ Очистит процессы
4. ✅ Пересоберет проект
5. ✅ Запустит сервис
6. ✅ Покажет статус

---

### Вариант 2: Только загрузка файла

Если хотите выполнить команды на сервере вручную:

```powershell
cd "C:\Trust The Route"
.\backend\upload_fixed_userrepository.ps1
```

Затем на сервере:
```bash
ssh -i C:\Users\kuzne\.ssh\ssh-key-1769657037850 ubuntu@158.160.217.181
cd ~/trust-the-route-backend/backend
chmod +x FIX_DELETEUSER_STEP_BY_STEP.sh
./FIX_DELETEUSER_STEP_BY_STEP.sh
```

---

## Параметры подключения

Скрипты используют следующие параметры (уже настроены):
- **SSH ключ:** `C:\Users\kuzne\.ssh\ssh-key-1769657037850`
- **IP сервера:** `158.160.217.181`
- **Пользователь:** `ubuntu`

Если нужно изменить - откройте скрипт и измените переменные в начале файла.

---

## Что было исправлено

Метод `deleteUser` в файле `UserRepository.kt` исправлен:

**Было (с ошибкой):**
```kotlin
val deletedRows = Users.deleteWhere { Users.id eq uuid }
```

**Стало (исправлено):**
```kotlin
val deletedRows = Users.deleteWhere { Users.id eq uuid }
deletedRows > 0
```

Используется правильный синтаксис оператора `eq` (с пробелом), как в других методах файла.

---

## Проверка результата

После выполнения скрипта проверьте:

1. **Статус сервиса:**
   ```powershell
   ssh -i C:\Users\kuzne\.ssh\ssh-key-1769657037850 ubuntu@158.160.217.181 "sudo systemctl status trust-the-route-backend"
   ```
   Должно быть: `active (running)`

2. **Логи (если есть проблемы):**
   ```powershell
   ssh -i C:\Users\kuzne\.ssh\ssh-key-1769657037850 ubuntu@158.160.217.181 "sudo journalctl -u trust-the-route-backend -n 50 --no-pager"
   ```

---

## Если что-то пошло не так

1. **Проверьте, что вы в правильной директории:**
   ```powershell
   Get-Location
   # Должно быть: C:\Trust The Route
   ```

2. **Проверьте существование файла:**
   ```powershell
   Test-Path backend\src\main\kotlin\com\trusttheroute\backend\repositories\UserRepository.kt
   # Должно вернуть: True
   ```

3. **Проверьте SSH ключ:**
   ```powershell
   Test-Path C:\Users\kuzne\.ssh\ssh-key-1769657037850
   # Должно вернуть: True
   ```

4. **Выполните команды вручную:**
   - Сначала загрузите файл через `upload_fixed_userrepository.ps1`
   - Затем подключитесь к серверу и выполните команды из `FIX_DELETEUSER_STEP_BY_STEP.sh`

---

## Файлы

- `fix_deleteuser_auto.ps1` - полностью автоматический скрипт
- `upload_fixed_userrepository.ps1` - только загрузка файла
- `FIX_DELETEUSER_STEP_BY_STEP.sh` - скрипт для выполнения на сервере
- `БЫСТРОЕ_ИСПРАВЛЕНИЕ_DELETEUSER.sh` - быстрый скрипт для сервера
