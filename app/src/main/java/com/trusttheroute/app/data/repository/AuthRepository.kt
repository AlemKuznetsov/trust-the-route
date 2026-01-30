package com.trusttheroute.app.data.repository

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.trusttheroute.app.data.api.AuthApi
import com.trusttheroute.app.data.api.AuthResponse
import com.trusttheroute.app.data.api.LoginRequest
import com.trusttheroute.app.data.api.MessageResponse
import com.trusttheroute.app.data.api.RegisterRequest
import com.trusttheroute.app.data.api.ResetPasswordRequest
import com.trusttheroute.app.data.local.PreferencesManager
import com.trusttheroute.app.domain.model.User
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val preferencesManager: PreferencesManager,
    private val gson: Gson
) {
    suspend fun register(email: String, password: String, name: String): Result<User> {
        return try {
            val response = authApi.register(RegisterRequest(email, password, name))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                preferencesManager.saveToken(authResponse.token)
                preferencesManager.saveUser(authResponse.user)
                Result.success(authResponse.user)
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMessage ?: "Registration failed"))
            }
        } catch (e: HttpException) {
            val errorMessage = parseErrorMessage(e.response()?.errorBody()?.string())
            Result.failure(Exception(errorMessage ?: "Network error: ${e.message}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.message}"))
        }
    }

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = authApi.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                preferencesManager.saveToken(authResponse.token)
                preferencesManager.saveUser(authResponse.user)
                Result.success(authResponse.user)
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMessage ?: "Login failed"))
            }
        } catch (e: HttpException) {
            val errorMessage = parseErrorMessage(e.response()?.errorBody()?.string())
            Result.failure(Exception(errorMessage ?: "Network error: ${e.message}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.message}"))
        }
    }

    suspend fun resetPassword(email: String): Result<String> {
        return try {
            val response = authApi.resetPassword(ResetPasswordRequest(email))
            if (response.isSuccessful && response.body() != null) {
                val messageResponse = response.body()!!
                Result.success(messageResponse.message)
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMessage ?: "Reset password failed"))
            }
        } catch (e: HttpException) {
            val errorMessage = parseErrorMessage(e.response()?.errorBody()?.string())
            Result.failure(Exception(errorMessage ?: "Network error: ${e.message}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.message}"))
        }
    }

    /**
     * Парсит сообщение об ошибке из ответа API
     * Бэкенд возвращает либо mapOf("error" to "..."), либо mapOf("message" to "...")
     */
    private fun parseErrorMessage(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) return null
        
        return try {
            val jsonObject = gson.fromJson(errorBody, JsonObject::class.java)
            when {
                jsonObject.has("error") -> jsonObject.get("error").asString
                jsonObject.has("message") -> jsonObject.get("message").asString
                else -> errorBody
            }
        } catch (e: Exception) {
            errorBody
        }
    }

    suspend fun logout() {
        preferencesManager.clearToken()
        preferencesManager.clearUser()
    }

    suspend fun getCurrentUser(): User? {
        return preferencesManager.getUser()
    }

    suspend fun isLoggedIn(): Boolean {
        return preferencesManager.getToken() != null
    }
}
