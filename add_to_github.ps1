# Скрипт для добавления файлов в GitHub репозиторий
# Запустите этот скрипт после настройки Git и создания репозитория на GitHub

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Добавление файлов в GitHub репозиторий" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Переход в директорию проекта
$projectPath = "C:\Trust The Route"
Set-Location $projectPath

# Проверка, что мы в правильной директории
if (-not (Test-Path "build.gradle.kts")) {
    Write-Host "ОШИБКА: Не найдены файлы проекта. Убедитесь, что вы находитесь в правильной директории." -ForegroundColor Red
    exit 1
}

Write-Host "✓ Найдена директория проекта" -ForegroundColor Green

# Проверка наличия Git
try {
    $gitVersion = git --version
    Write-Host "✓ Git установлен: $gitVersion" -ForegroundColor Green
} catch {
    Write-Host "ОШИБКА: Git не установлен или не найден в PATH." -ForegroundColor Red
    Write-Host "Установите Git с https://git-scm.com/download/win" -ForegroundColor Yellow
    exit 1
}

# Проверка, инициализирован ли репозиторий
if (-not (Test-Path ".git")) {
    Write-Host "Инициализация Git репозитория..." -ForegroundColor Yellow
    git init
    Write-Host "✓ Репозиторий инициализирован" -ForegroundColor Green
} else {
    Write-Host "✓ Git репозиторий уже инициализирован" -ForegroundColor Green
}

Write-Host ""
Write-Host "Проверка статуса файлов..." -ForegroundColor Yellow
git status

Write-Host ""
Write-Host "Добавление всех файлов (кроме тех, что в .gitignore)..." -ForegroundColor Yellow
git add .

Write-Host ""
Write-Host "Проверка, что будет добавлено в коммит..." -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
git status
Write-Host "========================================" -ForegroundColor Cyan

Write-Host ""
Write-Host "ВАЖНО: Проверьте список файлов выше!" -ForegroundColor Yellow
Write-Host "Убедитесь, что НЕТ следующих файлов:" -ForegroundColor Yellow
Write-Host "  - local.properties" -ForegroundColor Red
Write-Host "  - google-services.json" -ForegroundColor Red
Write-Host "  - build/" -ForegroundColor Red
Write-Host "  - app/build/" -ForegroundColor Red
Write-Host ""

$confirm = Read-Host "Продолжить создание коммита? (y/n)"
if ($confirm -ne "y" -and $confirm -ne "Y") {
    Write-Host "Отменено пользователем." -ForegroundColor Yellow
    exit 0
}

Write-Host ""
Write-Host "Создание коммита..." -ForegroundColor Yellow
$commitMessage = "Initial commit: Trust The Route mobile app

- Added source code (Kotlin, Compose)
- Added project configuration files
- Added documentation (implementation guides, test cases)
- Added assets (routes, images, audio)
- Configured .gitignore for Android project"

git commit -m $commitMessage

if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Коммит создан успешно!" -ForegroundColor Green
} else {
    Write-Host "ОШИБКА: Не удалось создать коммит." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Следующие шаги:" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "1. Если вы еще не создали репозиторий на GitHub:" -ForegroundColor Yellow
Write-Host "   - Зайдите на https://github.com" -ForegroundColor White
Write-Host "   - Нажмите 'New repository'" -ForegroundColor White
Write-Host "   - Укажите название (например: trust-the-route)" -ForegroundColor White
Write-Host "   - НЕ добавляйте README, .gitignore или лицензию" -ForegroundColor White
Write-Host ""
Write-Host "2. Подключите локальный репозиторий к GitHub:" -ForegroundColor Yellow
Write-Host "   git remote add origin https://github.com/YOUR_USERNAME/trust-the-route.git" -ForegroundColor White
Write-Host "   (Замените YOUR_USERNAME на ваш GitHub username)" -ForegroundColor Gray
Write-Host ""
Write-Host "3. Переименуйте ветку в main (если нужно):" -ForegroundColor Yellow
Write-Host "   git branch -M main" -ForegroundColor White
Write-Host ""
Write-Host "4. Отправьте код в GitHub:" -ForegroundColor Yellow
Write-Host "   git push -u origin main" -ForegroundColor White
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Готово! Файлы подготовлены к отправке." -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
