# Скрипт PowerShell для открытия проекта в Android Studio

$projectPath = $PSScriptRoot
$studioFound = $false
$studioPath = $null

# Проверка стандартных расположений Android Studio
$possiblePaths = @(
    "$env:LOCALAPPDATA\Programs\Android\Android Studio\bin\studio64.exe",
    "$env:ProgramFiles\Android\Android Studio\bin\studio64.exe",
    "${env:ProgramFiles(x86)}\Android\Android Studio\bin\studio64.exe"
)

foreach ($path in $possiblePaths) {
    if (Test-Path $path) {
        $studioPath = $path
        $studioFound = $true
        break
    }
}

if ($studioFound) {
    Write-Host "Opening project in Android Studio..." -ForegroundColor Green
    Write-Host "Project path: $projectPath" -ForegroundColor Cyan
    Write-Host "Studio path: $studioPath" -ForegroundColor Cyan
    Start-Process -FilePath $studioPath -ArgumentList $projectPath
} else {
    Write-Host "Android Studio not found in standard locations." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Please open Android Studio manually and select this folder:" -ForegroundColor Yellow
    Write-Host $projectPath -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Or install Android Studio from: https://developer.android.com/studio" -ForegroundColor Yellow
    
    # Попытка открыть папку проекта в проводнике
    Start-Process explorer.exe -ArgumentList $projectPath
}
