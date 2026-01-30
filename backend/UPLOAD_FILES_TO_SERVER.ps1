# PowerShell скрипт для загрузки файлов на сервер

$SSH_KEY = "C:\Users\kuzne\.ssh\ssh-key-1769657037850"
$SERVER = "ubuntu@158.160.217.181"
$PROJECT_DIR = "C:\Trust The Route"

Write-Host "=== Загрузка файлов на сервер ===" -ForegroundColor Green
Write-Host ""

# Проверка существования SSH ключа
if (-not (Test-Path $SSH_KEY)) {
    Write-Host "❌ SSH ключ не найден: $SSH_KEY" -ForegroundColor Red
    Write-Host "Найдите ваш SSH ключ:" -ForegroundColor Yellow
    Get-ChildItem C:\Users\kuzne\.ssh\*.pem, C:\Users\kuzne\.ssh\ssh-key* -ErrorAction SilentlyContinue | Select-Object FullName
    exit 1
}

Write-Host "✅ SSH ключ найден: $SSH_KEY" -ForegroundColor Green
Write-Host ""

# Файлы для загрузки
$files = @(
    @{
        Local = "$PROJECT_DIR\backend\src\main\kotlin\com\trusttheroute\backend\routes\auth\AuthRoutes.kt"
        Remote = "~/trust-the-route-backend/backend/src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt"
    },
    @{
        Local = "$PROJECT_DIR\backend\src\main\kotlin\com\trusttheroute\backend\models\User.kt"
        Remote = "~/trust-the-route-backend/backend/src/main/kotlin/com/trusttheroute/backend/models/User.kt"
    },
    @{
        Local = "$PROJECT_DIR\backend\src\main\kotlin\com\trusttheroute\backend\repositories\UserRepository.kt"
        Remote = "~/trust-the-route-backend/backend/src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt"
    }
)

foreach ($file in $files) {
    if (Test-Path $file.Local) {
        Write-Host "Загрузка: $($file.Local)" -ForegroundColor Cyan
        scp -i $SSH_KEY $file.Local "$SERVER`:$($file.Remote)"
        if ($LASTEXITCODE -eq 0) {
            Write-Host "  ✅ Успешно загружено" -ForegroundColor Green
        } else {
            Write-Host "  ❌ Ошибка загрузки" -ForegroundColor Red
        }
    } else {
        Write-Host "❌ Файл не найден: $($file.Local)" -ForegroundColor Red
    }
    Write-Host ""
}

Write-Host "=== Готово! ===" -ForegroundColor Green
Write-Host ""
Write-Host "Теперь подключитесь к серверу и выполните:" -ForegroundColor Yellow
Write-Host "  cd ~/trust-the-route-backend/backend" -ForegroundColor White
Write-Host "  ./VERIFY_AND_FIX_ON_SERVER.sh" -ForegroundColor White
