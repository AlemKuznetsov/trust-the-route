# PowerShell скрипт для загрузки исправленных файлов на сервер

$SSH_KEY = "C:\Users\kuzne\.ssh\ssh-key-1769657037850"
$SERVER = "ubuntu@158.160.217.181"
$PROJECT_DIR = "C:\Trust The Route"

Write-Host "=== Загрузка исправленных файлов на сервер ===" -ForegroundColor Green
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
        Local = "$PROJECT_DIR\backend\src\main\kotlin\com\trusttheroute\backend\models\ApiResponses.kt"
        Remote = "~/trust-the-route-backend/backend/src/main/kotlin/com/trusttheroute/backend/models/ApiResponses.kt"
        Description = "ApiResponses.kt (новый файл с ErrorResponse и MessageResponse)"
    },
    @{
        Local = "$PROJECT_DIR\backend\src\main\kotlin\com\trusttheroute\backend\Application.kt"
        Remote = "~/trust-the-route-backend/backend/src/main/kotlin/com/trusttheroute/backend/Application.kt"
        Description = "Application.kt (добавлен импорт ErrorResponse)"
    },
    @{
        Local = "$PROJECT_DIR\backend\src\main\kotlin\com\trusttheroute\backend\routes\auth\AuthRoutes.kt"
        Remote = "~/trust-the-route-backend/backend/src/main/kotlin/com/trusttheroute/backend/routes/auth/AuthRoutes.kt"
        Description = "AuthRoutes.kt (добавлены импорты, заменен mapOf на MessageResponse)"
    }
)

foreach ($file in $files) {
    if (Test-Path $file.Local) {
        Write-Host "Загрузка: $($file.Description)" -ForegroundColor Cyan
        Write-Host "  Путь: $($file.Local)" -ForegroundColor Gray
        
        # Создаем директорию на сервере, если её нет
        $remoteDir = $file.Remote.Substring(0, $file.Remote.LastIndexOf('/'))
        ssh -i $SSH_KEY $SERVER "mkdir -p $remoteDir" | Out-Null
        
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

Write-Host "=== Загрузка завершена! ===" -ForegroundColor Green
Write-Host ""
Write-Host "Теперь подключитесь к серверу и пересоберите проект:" -ForegroundColor Yellow
Write-Host ""
Write-Host "  ssh -i $SSH_KEY $SERVER" -ForegroundColor White
Write-Host ""
Write-Host "Затем выполните на сервере:" -ForegroundColor Yellow
Write-Host "  cd ~/trust-the-route-backend/backend" -ForegroundColor White
Write-Host "  ./gradlew clean build" -ForegroundColor White
Write-Host "  sudo systemctl restart trust-the-route-backend" -ForegroundColor White
Write-Host ""
