# Скрипт для проверки логов Android приложения Trust The Route
# Использование: .\check_logs.ps1

Write-Host "Проверка подключенных устройств..." -ForegroundColor Cyan
adb devices

Write-Host "`nФильтрация логов по MapScreen и YandexMapView..." -ForegroundColor Cyan
Write-Host "Нажмите Ctrl+C для остановки`n" -ForegroundColor Yellow

# Фильтруем логи по тегам MapScreen и YandexMapView
adb logcat -s MapScreen YandexMapView
