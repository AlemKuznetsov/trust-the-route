# Перезапуск Backend

## Быстрый перезапуск

```bash
sudo systemctl restart trust-the-route-backend
```

## Проверка статуса

```bash
sudo systemctl status trust-the-route-backend
```

## Другие полезные команды

### Остановить backend
```bash
sudo systemctl stop trust-the-route-backend
```

### Запустить backend
```bash
sudo systemctl start trust-the-route-backend
```

### Просмотр логов в реальном времени
```bash
sudo journalctl -u trust-the-route-backend -f
```

### Просмотр последних 50 строк логов
```bash
sudo journalctl -u trust-the-route-backend -n 50
```

### Просмотр логов из файла app.log
```bash
tail -f ~/trust-the-route-backend/backend/app.log
```

## После изменений в коде

Если вы внесли изменения в код backend, нужно:

1. **Перезапустить service:**
   ```bash
   sudo systemctl restart trust-the-route-backend
   ```

2. **Проверить статус:**
   ```bash
   sudo systemctl status trust-the-route-backend
   ```

3. **Проверить логи на ошибки:**
   ```bash
   sudo journalctl -u trust-the-route-backend -n 100
   ```

## Если service не запускается

1. **Проверить логи:**
   ```bash
   sudo journalctl -u trust-the-route-backend -n 100 --no-pager
   ```

2. **Проверить, что порт 8080 свободен:**
   ```bash
   sudo ss -tlnp | grep 8080
   ```

3. **Остановить процессы на порту 8080 (если нужно):**
   ```bash
   sudo lsof -ti:8080 | xargs sudo kill -9
   ```

4. **Перезапустить service:**
   ```bash
   sudo systemctl restart trust-the-route-backend
   ```
