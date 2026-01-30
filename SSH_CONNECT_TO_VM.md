# Подключение к VM через SSH

## Способ 1: Через PowerShell (рекомендуется)

1. **Откройте PowerShell** на вашем компьютере

2. **Подключитесь к VM:**
   ```powershell
   ssh -i C:\Users\kuzne\.ssh\ssh-key-1769657037850 admin_t_t_r@158.160.180.232
   ```

   Если файл ключа имеет другое имя, найдите его:
   ```powershell
   Get-ChildItem C:\Users\kuzne\.ssh\*.pem
   Get-ChildItem C:\Users\kuzne\.ssh\ssh-key*
   ```

3. **После подключения** вы увидите приглашение:
   ```
   admin_t_t_r@compute-vm-2-4-25-ssd-1769660811014:~$
   ```

4. **Теперь выполняйте команды на VM:**
   ```bash
   cd ~/trust-the-route-backend/backend
   ps aux | grep java
   sudo ss -tlnp | grep 8080
   tail -100 app.log
   ```

## Способ 2: Через Windows Terminal или CMD

Если PowerShell не работает, используйте:

```cmd
ssh -i C:\Users\kuzne\.ssh\ssh-key-1769657037850 admin_t_t_r@158.160.180.232
```

## Способ 3: Если ключ не найден

1. **Найдите ваш приватный ключ:**
   ```powershell
   # В PowerShell
   Get-ChildItem C:\Users\kuzne\.ssh\ -Recurse | Where-Object {$_.Name -like "*key*" -and $_.Extension -ne ".pub"}
   ```

2. **Или проверьте, какие ключи есть:**
   ```powershell
   ls C:\Users\kuzne\.ssh\
   ```

## Важно:

- **Cloud Shell** - это веб-терминал в браузере Yandex Cloud
- **SSH подключение** - это прямое подключение к VM с вашего компьютера
- Команды в Cloud Shell выполняются на другом сервере, не на вашей VM!

## После подключения через SSH:

Вы будете работать напрямую на VM, где запущено приложение. Все команды будут выполняться там.
