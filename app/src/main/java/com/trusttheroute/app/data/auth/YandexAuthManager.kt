package com.trusttheroute.app.data.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
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
        // TODO: Замените на ваш Client ID из Yandex OAuth
        // Рекомендуется хранить в BuildConfig или local.properties
        private const val YANDEX_CLIENT_ID = "18401c8a769a4f5f9a546e05ea184851"
        
        // Redirect URI должен совпадать с указанным в настройках приложения Yandex OAuth
        private const val REDIRECT_URI = "trusttheroute://oauth/yandex"
        
        private const val YANDEX_AUTH_URL = "https://oauth.yandex.ru/authorize"
        private const val YANDEX_TOKEN_URL = "https://oauth.yandex.ru/token"
        private const val YANDEX_USER_INFO_URL = "https://login.yandex.ru/info"
        
        // Scopes для запроса данных пользователя
        private const val SCOPES = "login:email login:avatar login:name"
    }

    /**
     * Генерирует URL для авторизации через Yandex ID
     */
    suspend fun getAuthUrl(): String = withContext(Dispatchers.IO) {
        val state = UUID.randomUUID().toString()
        // Сохраняем state для проверки при получении токена
        preferencesManager.saveOAuthState(state)
        
        Uri.parse(YANDEX_AUTH_URL)
            .buildUpon()
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("client_id", YANDEX_CLIENT_ID)
            .appendQueryParameter("redirect_uri", REDIRECT_URI)
            .appendQueryParameter("scope", SCOPES)
            .appendQueryParameter("state", state)
            .build()
            .toString()
    }

    /**
     * Открывает браузер для авторизации через Yandex ID
     */
    suspend fun startAuth(activity: android.app.Activity) {
        val authUrl = getAuthUrl()
        val customTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
        
        // Запускаем на главном потоке, так как это UI операция
        kotlinx.coroutines.withContext(Dispatchers.Main) {
            customTabsIntent.launchUrl(activity, Uri.parse(authUrl))
        }
    }

    /**
     * Обрабатывает redirect URI и получает токен
     */
    suspend fun handleAuthCallback(uri: Uri): Result<User> = withContext(Dispatchers.IO) {
        try {
            val code = uri.getQueryParameter("code")
            val state = uri.getQueryParameter("state")
            val error = uri.getQueryParameter("error")

            if (error != null) {
                return@withContext Result.failure<User>(
                    Exception("Ошибка авторизации: $error")
                )
            }

            if (code == null) {
                return@withContext Result.failure<User>(
                    Exception("Код авторизации не получен")
                )
            }

            // Проверяем state для защиты от CSRF
            val savedState = preferencesManager.getOAuthState()
            if (state == null || savedState == null || state != savedState) {
                return@withContext Result.failure<User>(
                    Exception("Неверный state параметр")
                )
            }

            // Обмениваем код на токен (code уже проверен на null выше)
            val accessToken = exchangeCodeForToken(code!!) ?: return@withContext Result.failure<User>(
                Exception("Не удалось получить токен")
            )

            // Получаем информацию о пользователе
            val userInfo = getUserInfo(accessToken) ?: return@withContext Result.failure<User>(
                Exception("Не удалось получить информацию о пользователе")
            )

            // Создаем пользователя
            val user = User(
                id = userInfo.getString("id"),
                email = userInfo.getString("default_email") ?: userInfo.optString("emails", "").split(",").firstOrNull() ?: "",
                name = "${userInfo.optString("first_name", "")} ${userInfo.optString("last_name", "")}".trim()
                    .ifEmpty { userInfo.optString("display_name", "") }
                    .ifEmpty { userInfo.getString("login") },
                token = accessToken
            )

            // Сохраняем пользователя и токен
            preferencesManager.saveToken(accessToken)
            preferencesManager.saveUser(user)
            preferencesManager.clearOAuthState()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Обменивает код авторизации на access token
     */
    private suspend fun exchangeCodeForToken(code: String): String? = withContext(Dispatchers.IO) {
        try {
            val requestBody = "grant_type=authorization_code&" +
                    "code=${URLEncoder.encode(code, "UTF-8")}&" +
                    "client_id=${URLEncoder.encode(YANDEX_CLIENT_ID, "UTF-8")}&" +
                    "redirect_uri=${URLEncoder.encode(REDIRECT_URI, "UTF-8")}"

            val mediaType = "application/x-www-form-urlencoded".toMediaType()
            val request = Request.Builder()
                .url(YANDEX_TOKEN_URL)
                .post(okhttp3.RequestBody.create(mediaType, requestBody))
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                val json = JSONObject(responseBody)
                json.optString("access_token")
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Получает информацию о пользователе по access token
     */
    private suspend fun getUserInfo(accessToken: String): JSONObject? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$YANDEX_USER_INFO_URL?format=json")
                .addHeader("Authorization", "OAuth $accessToken")
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                JSONObject(responseBody)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
