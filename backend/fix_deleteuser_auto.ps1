# Автоматическое исправление метода deleteUser
# Загружает файл на сервер и выполняет все необходимые команды

# Параметры подключения
$SSH_KEY = "C:\Users\kuzne\.ssh\ssh-key-1769657037850"
$SERVER_IP = "158.160.217.181"
$SERVER_USER = "ubuntu"
$LOCAL_FILE = "backend\src\main\kotlin\com\trusttheroute\backend\repositories\UserRepository.kt"
$REMOTE_PATH = "~/trust-the-route-backend/backend/src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt"

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Автоматическое исправление deleteUser" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# Проверка существования локального файла
if (-not (Test-Path $LOCAL_FILE)) {
    Write-Host "Ошибка: Файл не найден!" -ForegroundColor Red
    Write-Host "   Путь: $LOCAL_FILE" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Убедитесь, что вы находитесь в корневой директории проекта" -ForegroundColor Yellow
    Write-Host "Текущая директория: $(Get-Location)" -ForegroundColor Yellow
    exit 1
}

# Проверка существования SSH ключа
if (-not (Test-Path $SSH_KEY)) {
    Write-Host "Ошибка: SSH ключ не найден!" -ForegroundColor Red
    Write-Host "   Путь: $SSH_KEY" -ForegroundColor Yellow
    exit 1
}

Write-Host "ШАГ 1: Загрузка файла на сервер..." -ForegroundColor Yellow
Write-Host ""

$scpCommand = "scp -i `"$SSH_KEY`" `"$LOCAL_FILE`" ${SERVER_USER}@${SERVER_IP}:`"$REMOTE_PATH`""

try {
    Invoke-Expression $scpCommand
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Ошибка при загрузке файла!" -ForegroundColor Red
        exit 1
    }
    
    Write-Host "Файл успешно загружен" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "Ошибка: $_" -ForegroundColor Red
    exit 1
}

Write-Host "ШАГ 2: Выполнение команд на сервере..." -ForegroundColor Yellow
Write-Host ""

# Команды для выполнения на сервере (упрощенная версия)
$remoteCommands = "cd ~/trust-the-route-backend/backend && sudo systemctl stop trust-the-route-backend && sleep 2 && sudo lsof -ti:8080 | xargs -r sudo kill -9 2>/dev/null && ps aux | grep -E 'gradlew|java.*backend' | grep -v grep | awk '{print `$2}' | xargs -r sudo kill -9 2>/dev/null && sleep 2 && ./gradlew clean --no-daemon && ./gradlew compileKotlin --no-daemon 2>&1 | tee /tmp/compile.log && if grep -q 'error:' /tmp/compile.log; then echo 'Ошибки компиляции:' && grep 'error:' /tmp/compile.log | head -5 && exit 1; fi && ./gradlew build --no-daemon 2>&1 | tail -10 && sudo systemctl start trust-the-route-backend && sleep 3 && sudo systemctl status trust-the-route-backend --no-pager -l | head -15"

$sshCommand = "ssh -i `"$SSH_KEY`" ${SERVER_USER}@${SERVER_IP} `"$remoteCommands`""

Write-Host "Выполняется команда на сервере..." -ForegroundColor Cyan
Write-Host ""

try {
    Invoke-Expression $sshCommand
    
    Write-Host ""
    Write-Host "==========================================" -ForegroundColor Cyan
    Write-Host "ГОТОВО!" -ForegroundColor Green
    Write-Host "==========================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Проверьте статус сервиса выше." -ForegroundColor Yellow
    Write-Host ""
    
} catch {
    Write-Host ""
    Write-Host "Ошибка при выполнении команд на сервере: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "Попробуйте выполнить команды вручную:" -ForegroundColor Yellow
    Write-Host "  ssh -i `"$SSH_KEY`" $SERVER_USER@$SERVER_IP" -ForegroundColor White
    Write-Host "  cd ~/trust-the-route-backend/backend" -ForegroundColor White
    Write-Host "  ./FIX_DELETEUSER_STEP_BY_STEP.sh" -ForegroundColor White
    exit 1
}
