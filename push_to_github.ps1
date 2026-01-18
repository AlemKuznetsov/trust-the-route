# Скрипт для отправки кода в GitHub репозиторий
# Запустите этот скрипт после выполнения add_to_github.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Отправка кода в GitHub репозиторий" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Переход в директорию проекта
$projectPath = "C:\Trust The Route"
Set-Location $projectPath

# Проверка наличия Git
try {
    git --version | Out-Null
} catch {
    Write-Host "ОШИБКА: Git не установлен или не найден в PATH." -ForegroundColor Red
    exit 1
}

# Проверка, что репозиторий инициализирован
if (-not (Test-Path ".git")) {
    Write-Host "ОШИБКА: Git репозиторий не инициализирован." -ForegroundColor Red
    Write-Host "Сначала запустите add_to_github.ps1" -ForegroundColor Yellow
    exit 1
}

# Проверка наличия коммитов
$commits = git log --oneline 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "ОШИБКА: Нет коммитов в репозитории." -ForegroundColor Red
    Write-Host "Сначала запустите add_to_github.ps1" -ForegroundColor Yellow
    exit 1
}

Write-Host "Проверка подключения к удаленному репозиторию..." -ForegroundColor Yellow
$remote = git remote -v

if ([string]::IsNullOrEmpty($remote)) {
    Write-Host ""
    Write-Host "Удаленный репозиторий не настроен." -ForegroundColor Yellow
    Write-Host ""
    $githubUsername = Read-Host "Введите ваш GitHub username"
    $repoName = Read-Host "Введите название репозитория (например: trust-the-route)"
    
    Write-Host ""
    Write-Host "Добавление удаленного репозитория..." -ForegroundColor Yellow
    git remote add origin "https://github.com/$githubUsername/$repoName.git"
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ОШИБКА: Не удалось добавить удаленный репозиторий." -ForegroundColor Red
        Write-Host "Проверьте, что репозиторий создан на GitHub." -ForegroundColor Yellow
        exit 1
    }
    
    Write-Host "✓ Удаленный репозиторий добавлен" -ForegroundColor Green
} else {
    Write-Host "✓ Удаленный репозиторий уже настроен:" -ForegroundColor Green
    Write-Host $remote -ForegroundColor Gray
}

Write-Host ""
Write-Host "Переименование ветки в main (если нужно)..." -ForegroundColor Yellow
git branch -M main 2>&1 | Out-Null

Write-Host ""
Write-Host "Отправка кода в GitHub..." -ForegroundColor Yellow
Write-Host "Вам может потребоваться ввести учетные данные GitHub." -ForegroundColor Yellow
Write-Host ""

git push -u origin main

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "✓ Код успешно отправлен в GitHub!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Ваш репозиторий доступен по адресу:" -ForegroundColor Yellow
    $remoteUrl = git remote get-url origin
    Write-Host $remoteUrl -ForegroundColor White
} else {
    Write-Host ""
    Write-Host "ОШИБКА: Не удалось отправить код в GitHub." -ForegroundColor Red
    Write-Host ""
    Write-Host "Возможные причины:" -ForegroundColor Yellow
    Write-Host "1. Неверные учетные данные GitHub" -ForegroundColor White
    Write-Host "2. Репозиторий не существует на GitHub" -ForegroundColor White
    Write-Host "3. Нет прав доступа к репозиторию" -ForegroundColor White
    Write-Host ""
    Write-Host "Решения:" -ForegroundColor Yellow
    Write-Host "1. Проверьте URL репозитория: git remote -v" -ForegroundColor White
    Write-Host "2. Используйте Personal Access Token вместо пароля" -ForegroundColor White
    Write-Host "3. Убедитесь, что репозиторий создан на GitHub" -ForegroundColor White
}
