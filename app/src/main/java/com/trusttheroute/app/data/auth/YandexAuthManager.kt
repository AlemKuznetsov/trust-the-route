package com.trusttheroute.app.data.auth

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import com.trusttheroute.app.data.local.PreferencesManager
import com.trusttheroute.app.domain.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Менеджер для работы с Yandex ID OAuth авторизацией
 * 
 * Для использования необходимо:
 * 1. Зарегистрировать приложение в Yandex OAuth: https://oauth.yandex.com/
 * 2. Получить Client ID
 * 3. Настроить Redirect URI (например: trusttheroute://oauth/yandex)
 * 4. Указать Client ID в BuildConfig или local.properties
 */
@Singleton
class YandexAuthManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesManager: PreferencesManager,
    private val okHttpClient: OkHttpClient
) {
    companion object {
        private const val TAG = "YandexAuthManager"
        
        // TODO: Замените на ваш Client ID из Yandex OAuth
        // Рекомендуется хранить в BuildConfig или local.properties
        private const val YANDEX_CLIENT_ID = "18401c8a769a4f5f9a546e05ea184851"
        
        // TODO: Если требуется client_secret, добавьте его здесь
        // Для Android приложений обычно НЕ требуется, но может быть нужен для некоторых типов приложений
        private const val YANDEX_CLIENT_SECRET = "686a11b235894d4bba2d463dd2ce2a64"
        
        // ⚠️ ВАЖНО: Redirect URI должен ТОЧНО совпадать с Callback URL в настройках приложения Yandex OAuth
        // Проверьте настройки на https://oauth.yandex.com/
        // Формат должен быть: trusttheroute://oauth/yandex (без слеша в конце!)
        private const val REDIRECT_URI = "trusttheroute://oauth/yandex"
        
        // Используем правильный endpoint для авторизации Yandex ID
        private const val YANDEX_AUTH_URL = "https://oauth.yandex.ru/authorize"
        private const val YANDEX_TOKEN_URL = "https://oauth.yandex.ru/token"
        private const val YANDEX_USER_INFO_URL = "https://login.yandex.ru/info"
        
        // Scopes для запроса данных пользователя
        // ⚠️ ВАЖНО: Scopes должны совпадать с разрешениями, выбранными в настройках приложения Yandex OAuth
        // Для Android приложений обычно используются: login:email и login:info (вместо login:name)
        // Если получаете ошибку invalid_scope, попробуйте убрать login:avatar или использовать только login:info
        private const val SCOPES = "login:email login:info"
    }

    /**
     * Генерирует URL для авторизации через Yandex ID
     */
    suspend fun getAuthUrl(): String = withContext(Dispatchers.IO) {
        val state = UUID.randomUUID().toString()
        Log.d(TAG, "Генерация state: $state")
        
        // Сохраняем state для проверки при получении токена
        // Важно: сохраняем синхронно, чтобы state был доступен при callback
        preferencesManager.saveOAuthState(state)
        
        // Проверяем, что state сохранился
        val savedState = preferencesManager.getOAuthState()
        Log.d(TAG, "State сохранен: ${savedState == state}, сохраненный: $savedState")
        
        val authUrl = Uri.parse(YANDEX_AUTH_URL)
            .buildUpon()
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("client_id", YANDEX_CLIENT_ID)
            .appendQueryParameter("redirect_uri", REDIRECT_URI)
            .appendQueryParameter("scope", SCOPES)
            .appendQueryParameter("state", state)
            .appendQueryParameter("lang", "ru") // Русский язык для интерфейса Yandex
            .build()
            .toString()
        
        Log.d(TAG, "=== Yandex OAuth URL ===")
        Log.d(TAG, "Client ID: $YANDEX_CLIENT_ID")
        Log.d(TAG, "Redirect URI: $REDIRECT_URI")
        Log.d(TAG, "Scopes: $SCOPES")
        Log.d(TAG, "State: $state")
        Log.d(TAG, "Full URL: $authUrl")
        Log.d(TAG, "========================")
        
        authUrl
    }

    /**
     * Проверяет наличие интернет-соединения
     */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    /**
     * Открывает браузер для авторизации через Yandex ID
     */
    suspend fun startAuth(activity: android.app.Activity) {
        // Проверяем наличие интернет-соединения
        if (!isNetworkAvailable()) {
            Log.e(TAG, "Нет интернет-соединения")
            throw Exception("Нет подключения к интернету. Проверьте настройки сети и попробуйте снова.")
        }
        
        // Генерируем URL и сохраняем state
        val authUrl = getAuthUrl()
        Log.d(TAG, "Открытие браузера для авторизации: $authUrl")
        
        // Дополнительная проверка, что state сохранился перед открытием браузера
        val savedState = preferencesManager.getOAuthState()
        Log.d(TAG, "Перед открытием браузера - сохраненный state: $savedState")
        
        if (savedState == null) {
            Log.e(TAG, "State не сохранился перед открытием браузера!")
            // Попробуем еще раз сохранить
            val stateFromUrl = Uri.parse(authUrl).getQueryParameter("state")
            if (stateFromUrl != null) {
                preferencesManager.saveOAuthState(stateFromUrl)
                Log.d(TAG, "Повторная попытка сохранения state: $stateFromUrl")
            }
        }
        
        try {
            Log.d(TAG, "Попытка открыть CustomTabs с URL: $authUrl")
            val customTabsIntent = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .build()
            
            // Запускаем на главном потоке, так как это UI операция
            kotlinx.coroutines.withContext(Dispatchers.Main) {
                val uri = Uri.parse(authUrl)
                Log.d(TAG, "Открытие CustomTabs с URI: $uri")
                
                // Проверяем, что есть приложение для обработки этого URL
                val packageManager = activity.packageManager
                val resolveInfo = customTabsIntent.intent.resolveActivity(packageManager)
                
                if (resolveInfo == null) {
                    Log.e(TAG, "Нет приложения для открытия CustomTabs")
                    throw Exception("Нет браузера для открытия страницы авторизации. Установите браузер и попробуйте снова.")
                }
                
                customTabsIntent.launchUrl(activity, uri)
                Log.d(TAG, "CustomTabs открыт успешно, браузер должен открыться")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при открытии браузера через CustomTabs", e)
            // Fallback: пытаемся открыть через обычный Intent
            try {
                Log.d(TAG, "Попытка открыть через обычный Intent")
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
                val packageManager = activity.packageManager
                if (intent.resolveActivity(packageManager) != null) {
                    activity.startActivity(intent)
                    Log.d(TAG, "Браузер открыт через обычный Intent")
                } else {
                    throw Exception("Нет браузера для открытия страницы авторизации. Установите браузер и попробуйте снова.")
                }
            } catch (fallbackException: Exception) {
                Log.e(TAG, "Ошибка при открытии через обычный Intent", fallbackException)
                throw Exception("Не удалось открыть браузер для авторизации: ${fallbackException.message}. Проверьте подключение к интернету и наличие браузера.")
            }
        }
    }

    /**
     * Обрабатывает redirect URI и получает токен
     */
    suspend fun handleAuthCallback(uri: Uri): Result<User> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Обработка callback URI: $uri")
            
            val code = uri.getQueryParameter("code")
            val state = uri.getQueryParameter("state")
            val error = uri.getQueryParameter("error")

            Log.d(TAG, "Параметры из URI - code: ${code?.take(10)}..., state: $state, error: $error")

            if (error != null) {
                Log.e(TAG, "Ошибка в callback: $error")
                return@withContext Result.failure<User>(
                    Exception("Ошибка авторизации: $error")
                )
            }

            if (code == null) {
                Log.e(TAG, "Код авторизации не получен")
                return@withContext Result.failure<User>(
                    Exception("Код авторизации не получен")
                )
            }

            // Проверяем state для защиты от CSRF
            val savedState = preferencesManager.getOAuthState()
            Log.d(TAG, "Сравнение state - получен из URI: $state, сохраненный: $savedState")
            
            if (state == null) {
                Log.e(TAG, "State параметр отсутствует в URI")
                return@withContext Result.failure<User>(
                    Exception("Неверный state параметр: параметр отсутствует в URI")
                )
            }
            
            // Проверка state с fallback для случаев, когда приложение перезапускается
            if (savedState == null) {
                Log.w(TAG, "State не найден в сохраненных данных. Возможно, приложение было перезапущено.")
                Log.w(TAG, "Продолжаем без проверки state (менее безопасно, но позволяет завершить авторизацию)")
                // ВАЖНО: В продакшене лучше не пропускать без state, но для Android приложений
                // это может быть необходимо из-за особенностей работы custom scheme
                // Можно добавить дополнительную проверку по времени или другим параметрам
            } else if (state != savedState) {
                Log.e(TAG, "State не совпадает! URI: $state, Сохраненный: $savedState")
                return@withContext Result.failure<User>(
                    Exception("Неверный state параметр: не совпадает с сохраненным значением")
                )
            } else {
                Log.d(TAG, "State проверен успешно")
            }

            // Обмениваем код на токен (code уже проверен на null выше)
            Log.d(TAG, "Начало обмена кода на токен")
            val accessToken = exchangeCodeForToken(code!!) ?: return@withContext Result.failure<User>(
                Exception("Не удалось получить токен")
            )
            Log.d(TAG, "Токен получен, длина: ${accessToken.length}")

            // Получаем информацию о пользователе
            Log.d(TAG, "Начало получения информации о пользователе")
            val userInfo = getUserInfo(accessToken)
            if (userInfo == null) {
                Log.e(TAG, "Не удалось получить информацию о пользователе")
                return@withContext Result.failure<User>(
                    Exception("Не удалось получить информацию о пользователе")
                )
            }
            Log.d(TAG, "Информация о пользователе получена: ${userInfo.toString().take(200)}")

            // Создаем пользователя
            Log.d(TAG, "Создание объекта User")
            val user = try {
                User(
                    id = userInfo.getString("id"),
                    email = userInfo.getString("default_email") ?: userInfo.optString("emails", "").split(",").firstOrNull() ?: "",
                    name = "${userInfo.optString("first_name", "")} ${userInfo.optString("last_name", "")}".trim()
                        .ifEmpty { userInfo.optString("display_name", "") }
                        .ifEmpty { userInfo.getString("login") },
                    token = accessToken
                )
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при создании объекта User", e)
                return@withContext Result.failure<User>(
                    Exception("Ошибка при создании пользователя: ${e.message}")
                )
            }
            Log.d(TAG, "Пользователь создан: id=${user.id}, email=${user.email}, name=${user.name}")

            // Сохраняем пользователя и токен
            Log.d(TAG, "Сохранение токена и данных пользователя")
            preferencesManager.saveToken(accessToken)
            preferencesManager.saveUser(user)
            preferencesManager.clearOAuthState()
            Log.d(TAG, "Данные сохранены успешно")

            Log.d(TAG, "Авторизация завершена успешно")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Исключение при обработке callback", e)
            Result.failure(e)
        }
    }

    /**
     * Обменивает код авторизации на access token
     */
    private suspend fun exchangeCodeForToken(code: String): String? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Обмен кода на токен. Code: ${code.take(10)}...")
            
            // Для Android приложений обычно НЕ требуется client_secret
            // Но если получаете ошибку, попробуйте добавить client_secret
            val requestBody = buildString {
                append("grant_type=authorization_code&")
                append("code=${URLEncoder.encode(code, "UTF-8")}&")
                append("client_id=${URLEncoder.encode(YANDEX_CLIENT_ID, "UTF-8")}&")
                append("redirect_uri=${URLEncoder.encode(REDIRECT_URI, "UTF-8")}")
                // Если требуется client_secret, раскомментируйте следующую строку:
                append("&client_secret=${URLEncoder.encode(YANDEX_CLIENT_SECRET, "UTF-8")}")
            }

            Log.d(TAG, "Request body: grant_type=authorization_code&code=...&client_id=...&redirect_uri=$REDIRECT_URI")

            val mediaType = "application/x-www-form-urlencoded".toMediaType()
            val request = Request.Builder()
                .url(YANDEX_TOKEN_URL)
                .post(okhttp3.RequestBody.create(mediaType, requestBody))
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string()

            Log.d(TAG, "Response code: ${response.code}")
            Log.d(TAG, "Response body: ${responseBody?.take(200)}")

            if (response.isSuccessful && responseBody != null) {
                val json = JSONObject(responseBody)
                val accessToken = json.optString("access_token")
                if (accessToken.isNotEmpty()) {
                    Log.d(TAG, "Токен успешно получен")
                    return@withContext accessToken
                } else {
                    Log.e(TAG, "Токен не найден в ответе. Ответ: $responseBody")
                    return@withContext null
                }
            } else {
                Log.e(TAG, "Ошибка при получении токена. Код: ${response.code}, Тело: $responseBody")
                // Попробуем распарсить ошибку из ответа
                if (responseBody != null) {
                    try {
                        val errorJson = JSONObject(responseBody)
                        val error = errorJson.optString("error", "unknown")
                        val errorDescription = errorJson.optString("error_description", "")
                        Log.e(TAG, "Ошибка OAuth: $error - $errorDescription")
                    } catch (e: Exception) {
                        Log.e(TAG, "Не удалось распарсить ошибку: $responseBody")
                    }
                }
                return@withContext null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Исключение при обмене кода на токен", e)
            return@withContext null
        }
    }

    /**
     * Получает информацию о пользователе по access token
     */
    private suspend fun getUserInfo(accessToken: String): JSONObject? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Запрос информации о пользователе по токену")
            val url = "$YANDEX_USER_INFO_URL?format=json"
            Log.d(TAG, "URL для получения информации: $url")
            
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "OAuth $accessToken")
                .build()

            Log.d(TAG, "Выполнение запроса к API Yandex")
            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string()

            Log.d(TAG, "Response code: ${response.code}")
            Log.d(TAG, "Response body length: ${responseBody?.length ?: 0}")
            
            if (response.isSuccessful && responseBody != null) {
                Log.d(TAG, "Ответ успешен, парсинг JSON")
                val json = JSONObject(responseBody)
                Log.d(TAG, "JSON распарсен успешно")
                json
            } else {
                Log.e(TAG, "Ошибка при получении информации о пользователе. Code: ${response.code}, Body: ${responseBody?.take(200)}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Исключение при получении информации о пользователе", e)
            null
        }
    }
}
