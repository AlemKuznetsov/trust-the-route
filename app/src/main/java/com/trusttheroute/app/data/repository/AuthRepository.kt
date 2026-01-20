package com.trusttheroute.app.data.repository

import com.trusttheroute.app.data.api.AuthApi
import com.trusttheroute.app.data.api.AuthResponse
import com.trusttheroute.app.data.api.LoginRequest
import com.trusttheroute.app.data.api.RegisterRequest
import com.trusttheroute.app.data.api.ResetPasswordRequest
import com.trusttheroute.app.data.local.PreferencesManager
import com.trusttheroute.app.domain.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val preferencesManager: PreferencesManager
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
                Result.failure(Exception("Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
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
                Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            val response = authApi.resetPassword(ResetPasswordRequest(email))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Reset password failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
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
