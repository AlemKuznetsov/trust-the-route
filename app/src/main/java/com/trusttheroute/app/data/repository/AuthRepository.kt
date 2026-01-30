package com.trusttheroute.app.data.repository

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.trusttheroute.app.data.api.AuthApi
import com.trusttheroute.app.data.api.AuthResponse
import com.trusttheroute.app.data.api.LoginRequest
import com.trusttheroute.app.data.api.MessageResponse
import com.trusttheroute.app.data.api.RegisterRequest
import com.trusttheroute.app.data.api.ResetPasswordRequest
import com.trusttheroute.app.data.api.UpdateProfileRequest
import com.trusttheroute.app.data.api.ChangePasswordRequest
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
        preferencesManager.clearOAuthState()
    }

    suspend fun getCurrentUser(): User? {
        return preferencesManager.getUser()
    }

    suspend fun isLoggedIn(): Boolean {
        return preferencesManager.getToken() != null
    }

    suspend fun updateProfile(name: String): Result<User> {
        return try {
            val response = authApi.updateProfile(UpdateProfileRequest(name))
            android.util.Log.d("AuthRepository", "UpdateProfile response code: ${response.code()}")
            android.util.Log.d("AuthRepository", "UpdateProfile isSuccessful: ${response.isSuccessful}")
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                preferencesManager.saveToken(authResponse.token)
                preferencesManager.saveUser(authResponse.user)
                Result.success(authResponse.user)
            } else {
                val errorBody = try {
                    response.errorBody()?.string()
                } catch (e: Exception) {
                    null
                }
                android.util.Log.e("AuthRepository", "UpdateProfile error: code=${response.code()}, body=$errorBody")
                val errorMessage = parseErrorMessage(errorBody)
                Result.failure(Exception(errorMessage ?: "Update profile failed (code: ${response.code()})"))
            }
        } catch (e: HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (ex: Exception) {
                null
            }
            android.util.Log.e("AuthRepository", "UpdateProfile HttpException: code=${e.code()}, body=$errorBody", e)
            val errorMessage = parseErrorMessage(errorBody)
            Result.failure(Exception(errorMessage ?: "Network error: ${e.message}"))
        } catch (e: IOException) {
            android.util.Log.e("AuthRepository", "UpdateProfile IOException", e)
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "UpdateProfile Exception", e)
            Result.failure(Exception("Unexpected error: ${e.message}"))
        }
    }

    suspend fun changePassword(oldPassword: String, newPassword: String): Result<String> {
        return try {
            val response = authApi.changePassword(ChangePasswordRequest(oldPassword, newPassword))
            android.util.Log.d("AuthRepository", "ChangePassword response code: ${response.code()}")
            android.util.Log.d("AuthRepository", "ChangePassword isSuccessful: ${response.isSuccessful}")
            if (response.isSuccessful && response.body() != null) {
                val messageResponse = response.body()!!
                Result.success(messageResponse.message)
            } else {
                val errorBody = try {
                    response.errorBody()?.string()
                } catch (e: Exception) {
                    null
                }
                android.util.Log.e("AuthRepository", "ChangePassword error: code=${response.code()}, body=$errorBody")
                val errorMessage = parseErrorMessage(errorBody)
                Result.failure(Exception(errorMessage ?: "Change password failed (code: ${response.code()})"))
            }
        } catch (e: HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (ex: Exception) {
                null
            }
            android.util.Log.e("AuthRepository", "ChangePassword HttpException: code=${e.code()}, body=$errorBody", e)
            val errorMessage = parseErrorMessage(errorBody)
            Result.failure(Exception(errorMessage ?: "Network error: ${e.message}"))
        } catch (e: IOException) {
            android.util.Log.e("AuthRepository", "ChangePassword IOException", e)
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "ChangePassword Exception", e)
            Result.failure(Exception("Unexpected error: ${e.message}"))
        }
    }

    suspend fun deleteAccount(confirmationText: String): Result<Unit> {
        return try {
            val response = authApi.deleteAccount(confirmationText)
            android.util.Log.d("AuthRepository", "DeleteAccount response code: ${response.code()}")
            android.util.Log.d("AuthRepository", "DeleteAccount isSuccessful: ${response.isSuccessful}")
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = try {
                    response.errorBody()?.string()
                } catch (e: Exception) {
                    null
                }
                android.util.Log.e("AuthRepository", "DeleteAccount error: code=${response.code()}, body=$errorBody")
                val errorMessage = parseErrorMessage(errorBody)
                Result.failure(Exception(errorMessage ?: "Delete account failed (code: ${response.code()})"))
            }
        } catch (e: HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (ex: Exception) {
                null
            }
            android.util.Log.e("AuthRepository", "DeleteAccount HttpException: code=${e.code()}, body=$errorBody", e)
            val errorMessage = parseErrorMessage(errorBody)
            Result.failure(Exception(errorMessage ?: "Network error: ${e.message}"))
        } catch (e: IOException) {
            android.util.Log.e("AuthRepository", "DeleteAccount IOException", e)
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "DeleteAccount Exception", e)
            Result.failure(Exception("Unexpected error: ${e.message}"))
        }
    }
}
