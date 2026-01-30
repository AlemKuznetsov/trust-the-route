# Инструкция по исправлению метода deleteUser

## Проблема
Ошибка компиляции в методе `deleteUser` на строке 198:
- `Type mismatch: inferred type is Unit but Op<Boolean> was expected`
- `Unresolved reference: eq`

## Решение
Метод `deleteUser` уже исправлен в локальном файле. Нужно применить изменения на сервере.

---

## Вариант 1: Автоматическое исправление (рекомендуется)

### Шаг 1: Подключитесь к серверу
```bash
ssh ubuntu@ваш_сервер
```

### Шаг 2: Перейдите в директорию проекта
```bash
cd ~/trust-the-route-backend/backend
```

### Шаг 3: Скопируйте исправленный файл на сервер
**На вашем локальном компьютере** (в PowerShell):
```powershell
# Убедитесь, что вы в директории проекта
cd "C:\Trust The Route"

# Скопируйте файл на сервер (замените YOUR_SERVER на адрес вашего сервера)
scp backend/src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt ubuntu@YOUR_SERVER:~/trust-the-route-backend/backend/src/main/kotlin/com/trusttheroute/backend/repositories/
```

### Шаг 4: На сервере запустите скрипт исправления
```bash
cd ~/trust-the-route-backend/backend
chmod +x FIX_DELETEUSER_STEP_BY_STEP.sh
./FIX_DELETEUSER_STEP_BY_STEP.sh
```

---

## Вариант 2: Ручное исправление

### Шаг 1: Подключитесь к серверу
```bash
ssh ubuntu@ваш_сервер
cd ~/trust-the-route-backend/backend
```

### Шаг 2: Создайте резервную копию
```bash
cp src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt \
   src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt.backup
```

### Шаг 3: Откройте файл для редактирования
```bash
nano src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt
```

### Шаг 4: Найдите метод deleteUser (около строки 194)
Замените метод на следующий:

```kotlin
fun deleteUser(userId: String): Boolean {
    return transaction {
        try {
            val uuid = UUID.fromString(userId)
            val deletedRows = Users.deleteWhere { Users.id eq uuid }
            deletedRows > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
```

**Важно:** Убедитесь, что используется оператор `eq` (с пробелом), а не метод `.eq()`

### Шаг 5: Сохраните файл
- В nano: `Ctrl+O`, затем `Enter`, затем `Ctrl+X`

### Шаг 6: Остановите сервис
```bash
sudo systemctl stop trust-the-route-backend
```

### Шаг 7: Очистите процессы
```bash
sudo lsof -ti:8080 | xargs -r sudo kill -9 2>/dev/null
ps aux | grep -E "gradlew|java.*backend" | grep -v grep | awk '{print $2}' | xargs -r sudo kill -9 2>/dev/null
```

### Шаг 8: Очистите и пересоберите проект
```bash
./gradlew clean build --no-daemon
```

### Шаг 9: Проверьте, что сборка успешна
Если видите `BUILD SUCCESSFUL` - продолжайте. Если есть ошибки - проверьте их.

### Шаг 10: Запустите сервис
```bash
sudo systemctl start trust-the-route-backend
```

### Шаг 11: Проверьте статус
```bash
sudo systemctl status trust-the-route-backend
```

### Шаг 12: Проверьте логи (если есть проблемы)
```bash
sudo journalctl -u trust-the-route-backend -n 50 --no-pager
```

---

## Проверка исправления

После применения исправления проверьте:

1. **Статус сервиса:**
   ```bash
   sudo systemctl status trust-the-route-backend
   ```
   Должно быть: `active (running)`

2. **Логи без ошибок:**
   ```bash
   sudo journalctl -u trust-the-route-backend -n 50 --no-pager | grep -i error
   ```
   Не должно быть ошибок компиляции

3. **Тест API (опционально):**
   ```bash
   curl -X DELETE "http://localhost:8080/api/auth/account?confirmation=УДАЛИТЬ" \
        -H "Authorization: Bearer YOUR_TOKEN"
   ```

---

## Если ошибка сохраняется

Если после всех шагов ошибка все еще есть:

1. **Проверьте версию Exposed:**
   ```bash
   grep "exposed" build.gradle.kts
   ```
   Должно быть: `exposed-core:0.44.1`

2. **Проверьте импорты в UserRepository.kt:**
   ```bash
   head -10 src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt
   ```
   Должны быть:
   ```kotlin
   import org.jetbrains.exposed.sql.*
   import org.jetbrains.exposed.sql.transactions.transaction
   ```

3. **Попробуйте полную очистку:**
   ```bash
   ./gradlew clean
   rm -rf .gradle
   ./gradlew build --no-daemon
   ```

---

## Контакты для помощи

Если проблема не решается, предоставьте:
- Вывод команды `./gradlew compileKotlin --no-daemon 2>&1 | grep error`
- Содержимое метода `deleteUser` (строки 194-205)
- Версию Exposed из `build.gradle.kts`
