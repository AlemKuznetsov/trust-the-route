# Интеграция бэкенда в Android приложение

## ✅ Выполнено

### 1. Обновлен BASE_URL
- **Файл:** `app/src/main/java/com/trusttheroute/app/di/NetworkModule.kt`
- **URL:** `http://158.160.180.232:8080/api/v1/`
- **Примечание:** Для продакшена замените на домен с HTTPS

### 2. Обновлен AuthApi
- **Файл:** `app/src/main/java/com/trusttheroute/app/data/api/AuthApi.kt`
- Добавлен `MessageResponse` для ответов с сообщениями
- Endpoints соответствуют бэкенду:
  - `POST /api/v1/auth/register`
  - `POST /api/v1/auth/login`
  - `POST /api/v1/auth/reset-password`

### 3. Обновлен AuthRepository
- **Файл:** `app/src/main/java/com/trusttheroute/app/data/repository/AuthRepository.kt`
- Добавлена обработка ошибок из API (парсинг `mapOf("error" to "...")`)
- `resetPassword` теперь возвращает `Result<String>` с сообщением
- Улучшена обработка сетевых ошибок

### 4. Обновлен AuthViewModel
- **Файл:** `app/src/main/java/com/trusttheroute/app/ui/viewmodel/AuthViewModel.kt`
- Добавлены функции:
  - `register(email, password, name)`
  - `resetPassword(email)`
- Добавлено состояние `AuthUiState.Message` для сообщений

### 5. Реализованы экраны
- **RegisterScreen.kt** - полная реализация с валидацией
- **ResetPasswordScreen.kt** - полная реализация с обработкой сообщений

## 📋 API Endpoints

### Регистрация
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "name": "User Name"
}
```

**Успешный ответ (201):**
```json
{
  "user": {
    "id": "uuid",
    "email": "user@example.com",
    "name": "User Name",
    "token": "jwt-token"
  },
  "token": "jwt-token"
}
```

**Ошибка (400/409):**
```json
{
  "error": "Email, password, and name are required"
}
```

### Вход
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**Успешный ответ (200):**
```json
{
  "user": {
    "id": "uuid",
    "email": "user@example.com",
    "name": "User Name",
    "token": "jwt-token"
  },
  "token": "jwt-token"
}
```

**Ошибка (401):**
```json
{
  "error": "Invalid email or password"
}
```

### Сброс пароля
```http
POST /api/v1/auth/reset-password
Content-Type: application/json

{
  "email": "user@example.com"
}
```

**Успешный ответ (200):**
```json
{
  "message": "If the email exists, a password reset link has been sent"
}
```

## 🔧 Настройка для продакшена

### 1. Замените BASE_URL на домен

Откройте `app/src/main/java/com/trusttheroute/app/di/NetworkModule.kt`:

```kotlin
// Замените на ваш домен с HTTPS
private const val BASE_URL = "https://api.trusttheroute.com/api/v1/"
```

### 2. Настройте SSL сертификат (если используете самоподписанный)

Добавьте в `NetworkModule.kt`:

```kotlin
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import okhttp3.OkHttpClient

// Только для разработки! В продакшене используйте правильный сертификат
fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
    // ... существующий код ...
    
    // Для разработки с самоподписанным сертификатом (НЕ использовать в продакшене!)
    // val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
    //     override fun checkClientTrusted(chain: Array<out java.security.cert.X509Certificate>?, authType: String?) {}
    //     override fun checkServerTrusted(chain: Array<out java.security.cert.X509Certificate>?, authType: String?) {}
    //     override fun getAcceptedIssuers() = arrayOf<java.security.cert.X509Certificate>()
    // })
    // val sslContext = SSLContext.getInstance("SSL")
    // sslContext.init(null, trustAllCerts, java.security.SecureRandom())
    
    return OkHttpClient.Builder()
        // .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
        // .hostnameVerifier { _, _ -> true } // Только для разработки!
        .cache(cache)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
}
```

## 🧪 Тестирование

### Проверка подключения к API

1. Запустите приложение
2. Перейдите на экран регистрации
3. Заполните форму и зарегистрируйтесь
4. Проверьте логи в Logcat:
   ```
   D/OkHttp: --> POST http://158.160.180.232:8080/api/v1/auth/register
   D/OkHttp: <-- 201 Created
   ```

### Проверка ошибок

1. Попробуйте зарегистрироваться с существующим email
2. Должна появиться ошибка: "User with this email already exists"

## 📝 Следующие шаги

1. **Настроить домен и SSL** для продакшена
2. **Добавить обработку JWT токена** в заголовках запросов (если нужно)
3. **Реализовать refresh token** (если планируется)
4. **Добавить логирование** сетевых запросов в продакшене (убрать BODY уровень)

## ⚠️ Важные замечания

1. **HTTP vs HTTPS:** Сейчас используется HTTP. Для продакшена обязательно используйте HTTPS
2. **IP адрес:** IP адрес может измениться. Настройте домен
3. **Безопасность:** Не логируйте пароли и токены в продакшене
4. **Обработка ошибок:** Все ошибки API теперь правильно обрабатываются и показываются пользователю
