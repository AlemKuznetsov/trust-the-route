package com.trusttheroute.app.util

import com.trusttheroute.app.data.local.PreferencesManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val preferencesManager: PreferencesManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Получаем токен синхронно (для interceptor это допустимо)
        val token = runBlocking {
            preferencesManager.getToken()
        }

        // Если токен есть, добавляем его в заголовок
        val newRequest = if (token != null) {
            android.util.Log.d("AuthInterceptor", "Adding token to request: ${originalRequest.url}")
            android.util.Log.d("AuthInterceptor", "Token length: ${token.length}, starts with: ${token.take(20)}...")
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            android.util.Log.w("AuthInterceptor", "No token found for request: ${originalRequest.url}")
            originalRequest
        }

        val response = chain.proceed(newRequest)
        
        // Логируем ответ для отладки
        if (!response.isSuccessful) {
            android.util.Log.e("AuthInterceptor", "Request failed: ${originalRequest.url}, code: ${response.code}")
        }
        
        return response
    }
}
