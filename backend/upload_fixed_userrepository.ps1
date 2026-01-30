# PowerShell скрипт для загрузки исправленного UserRepository.kt на сервер

# Параметры подключения
$SSH_KEY = "C:\Users\kuzne\.ssh\ssh-key-1769657037850"
$SERVER_IP = "158.160.217.181"
$SERVER_USER = "ubuntu"
$LOCAL_FILE = "backend\src\main\kotlin\com\trusttheroute\backend\repositories\UserRepository.kt"
$REMOTE_PATH = "~/trust-the-route-backend/backend/src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt"

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Загрузка исправленного UserRepository.kt" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# Проверка существования локального файла
if (-not (Test-Path $LOCAL_FILE)) {
    Write-Host "❌ Ошибка: Файл не найден!" -ForegroundColor Red
    Write-Host "   Путь: $LOCAL_FILE" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Убедитесь, что вы находитесь в корневой директории проекта" -ForegroundColor Yellow
    Write-Host "Текущая директория: $(Get-Location)" -ForegroundColor Yellow
    exit 1
}

# Проверка существования SSH ключа
if (-not (Test-Path $SSH_KEY)) {
    Write-Host "❌ Ошибка: SSH ключ не найден!" -ForegroundColor Red
    Write-Host "   Путь: $SSH_KEY" -ForegroundColor Yellow
    exit 1
}

Write-Host "📁 Локальный файл: $LOCAL_FILE" -ForegroundColor Green
Write-Host "🔑 SSH ключ: $SSH_KEY" -ForegroundColor Green
Write-Host "🌐 Сервер: $SERVER_USER@$SERVER_IP" -ForegroundColor Green
Write-Host "📤 Удаленный путь: $REMOTE_PATH" -ForegroundColor Green
Write-Host ""

# Команда scp
$scpCommand = "scp -i `"$SSH_KEY`" `"$LOCAL_FILE`" ${SERVER_USER}@${SERVER_IP}:`"$REMOTE_PATH`""

Write-Host "Выполняется команда:" -ForegroundColor Cyan
Write-Host "  $scpCommand" -ForegroundColor Gray
Write-Host ""

try {
    Invoke-Expression $scpCommand
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "✅ Файл успешно загружен на сервер!" -ForegroundColor Green
        Write-Host ""
        Write-Host "==========================================" -ForegroundColor Cyan
        Write-Host "Следующие шаги:" -ForegroundColor Cyan
        Write-Host "==========================================" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "1. Подключитесь к серверу:" -ForegroundColor Yellow
        Write-Host "   ssh -i `"$SSH_KEY`" $SERVER_USER@$SERVER_IP" -ForegroundColor White
        Write-Host ""
        Write-Host "2. Перейдите в директорию проекта:" -ForegroundColor Yellow
        Write-Host "   cd ~/trust-the-route-backend/backend" -ForegroundColor White
        Write-Host ""
        Write-Host "3. Запустите скрипт исправления:" -ForegroundColor Yellow
        Write-Host "   chmod +x FIX_DELETEUSER_STEP_BY_STEP.sh" -ForegroundColor White
        Write-Host "   ./FIX_DELETEUSER_STEP_BY_STEP.sh" -ForegroundColor White
        Write-Host ""
    } else {
        Write-Host ""
        Write-Host "❌ Ошибка при загрузке файла!" -ForegroundColor Red
        Write-Host "   Код ошибки: $LASTEXITCODE" -ForegroundColor Yellow
        exit 1
    }
} catch {
    Write-Host ""
    Write-Host "❌ Ошибка: $_" -ForegroundColor Red
    exit 1
}
