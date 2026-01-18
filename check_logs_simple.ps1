# Простой скрипт для проверки всех логов приложения
# Использование: .\check_logs_simple.ps1

Write-Host "Проверка подключенных устройств..." -ForegroundColor Cyan
adb devices

Write-Host "`nПоказываем все логи приложения Trust The Route..." -ForegroundColor Cyan
Write-Host "Нажмите Ctrl+C для остановки`n" -ForegroundColor Yellow

# Показываем логи с фильтром по пакету приложения
adb logcat | Select-String -Pattern "trusttheroute|MapScreen|YandexMapView"
