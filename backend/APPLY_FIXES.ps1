# PowerShell скрипт для применения исправлений UserRepository.kt

$SSH_KEY = "C:\Users\kuzne\.ssh\ssh-key-1769657037850"
$SERVER = "ubuntu@158.160.217.181"
$PROJECT_DIR = "C:\Trust The Route"

Write-Host "=== Применение исправлений UserRepository.kt ===" -ForegroundColor Green
Write-Host ""

# Проверка существования SSH ключа
if (-not (Test-Path $SSH_KEY)) {
    Write-Host "❌ SSH ключ не найден: $SSH_KEY" -ForegroundColor Red
    exit 1
}

Write-Host "✅ SSH ключ найден" -ForegroundColor Green
Write-Host ""

# Загружаем исправленный файл
$localFile = "$PROJECT_DIR\backend\src\main\kotlin\com\trusttheroute\backend\repositories\UserRepository.kt"
$remoteFile = "~/trust-the-route-backend/backend/src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt"

if (Test-Path $localFile) {
    Write-Host "Загрузка исправленного UserRepository.kt..." -ForegroundColor Cyan
    scp -i $SSH_KEY $localFile "$SERVER`:$remoteFile"
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Файл успешно загружен" -ForegroundColor Green
        Write-Host ""
        
        Write-Host "Выполняем сборку и перезапуск на сервере..." -ForegroundColor Cyan
        ssh -i $SSH_KEY $SERVER "cd ~/trust-the-route-backend/backend && ./gradlew clean build && sudo systemctl restart trust-the-route-backend && sleep 3 && sudo systemctl status trust-the-route-backend --no-pager | head -20"
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host ""
            Write-Host "✅ Исправления применены успешно!" -ForegroundColor Green
        } else {
            Write-Host ""
            Write-Host "⚠️ Проверьте вывод выше на наличие ошибок" -ForegroundColor Yellow
        }
    } else {
        Write-Host "❌ Ошибка загрузки файла" -ForegroundColor Red
    }
} else {
    Write-Host "❌ Файл не найден: $localFile" -ForegroundColor Red
}
