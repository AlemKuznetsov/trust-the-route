# Команды для прямого выполнения в PowerShell
# Скопируйте и вставьте эти команды в PowerShell

Write-Host "Загрузка файла на сервер..." -ForegroundColor Yellow

scp -i C:\Users\kuzne\.ssh\ssh-key-1769657037850 `
    backend\src\main\kotlin\com\trusttheroute\backend\repositories\UserRepository.kt `
    ubuntu@158.160.217.181:~/trust-the-route-backend/backend/src/main/kotlin/com/trusttheroute/backend/repositories/UserRepository.kt

Write-Host ""
Write-Host "Выполнение команд на сервере..." -ForegroundColor Yellow
Write-Host ""

ssh -i C:\Users\kuzne\.ssh\ssh-key-1769657037850 ubuntu@158.160.217.181 @"
cd ~/trust-the-route-backend/backend && \
sudo systemctl stop trust-the-route-backend && \
sleep 2 && \
sudo lsof -ti:8080 | xargs -r sudo kill -9 2>/dev/null && \
ps aux | grep -E 'gradlew|java.*backend' | grep -v grep | awk '{print \$2}' | xargs -r sudo kill -9 2>/dev/null && \
sleep 2 && \
./gradlew clean --no-daemon && \
./gradlew compileKotlin --no-daemon 2>&1 | tee /tmp/compile.log && \
if grep -q 'error:' /tmp/compile.log; then
    echo '❌ Ошибки компиляции:'
    grep 'error:' /tmp/compile.log | head -5
    exit 1
fi && \
./gradlew build --no-daemon 2>&1 | tail -10 && \
sudo systemctl start trust-the-route-backend && \
sleep 3 && \
sudo systemctl status trust-the-route-backend --no-pager -l | head -15
"@

Write-Host ""
Write-Host "Готово!" -ForegroundColor Green
