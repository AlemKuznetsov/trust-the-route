# Тестирование API из PowerShell

## Правильный способ (используйте Invoke-RestMethod):

```powershell
# Регистрация
$body = @{
    email = "test@example.com"
    password = "test123"
    name = "Test User"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://158.160.217.181:8080/api/v1/auth/register" `
    -Method Post `
    -ContentType "application/json" `
    -Body $body
```

## Или через curl с правильным экранированием:

```powershell
# Регистрация
curl.exe -X POST http://158.160.217.181:8080/api/v1/auth/register `
  -H "Content-Type: application/json" `
  -d '{\"email\":\"test@example.com\",\"password\":\"test123\",\"name\":\"Test User\"}'
```

Но лучше использовать первый способ с Invoke-RestMethod - он правильнее обрабатывает JSON.

## Тест входа:

```powershell
# Вход
$body = @{
    email = "test@example.com"
    password = "test123"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://158.160.217.181:8080/api/v1/auth/login" `
    -Method Post `
    -ContentType "application/json" `
    -Body $body
```

## Если нужно увидеть полный ответ с заголовками:

```powershell
# Регистрация с полным выводом
$body = @{
    email = "test@example.com"
    password = "test123"
    name = "Test User"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "http://158.160.217.181:8080/api/v1/auth/register" `
        -Method Post `
        -ContentType "application/json" `
        -Body $body
    
    Write-Host "Status Code: $($response.StatusCode)"
    Write-Host "Response:"
    $response.Content | ConvertFrom-Json | ConvertTo-Json -Depth 10
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    Write-Host "Response: $($_.Exception.Response)"
}
```
