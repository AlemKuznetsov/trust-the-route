package com.trusttheroute.app.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.trusttheroute.app.ui.theme.TrustTheRouteTheme
import com.trusttheroute.app.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class YandexOAuthActivity : ComponentActivity() {
    companion object {
        private const val TAG = "YandexOAuthActivity"
    }
    
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d(TAG, "YandexOAuthActivity создана")
        Log.d(TAG, "Intent: $intent")
        Log.d(TAG, "Intent data: ${intent.data}")
        Log.d(TAG, "Intent action: ${intent.action}")
        
        // Обрабатываем URI через ViewModel
        // ViewModel обработает callback и сохранит данные
        // После этого MainActivity проверит статус авторизации при возврате
        intent.data?.let { uri ->
            Log.d(TAG, "Обработка callback URI: $uri")
            lifecycleScope.launch {
                try {
                    Log.d(TAG, "Вызов authViewModel.handleYandexOAuthCallback")
                    authViewModel.handleYandexOAuthCallback(uri)
                    Log.d(TAG, "handleYandexOAuthCallback вызван успешно")
                    // Не ждем результата здесь - OAuthCallbackScreen обработает состояние
                } catch (e: Exception) {
                    Log.e(TAG, "Ошибка при обработке callback", e)
                    e.printStackTrace()
                    // Переходим в MainActivity с ошибкой
                    val mainIntent = Intent(this@YandexOAuthActivity, com.trusttheroute.app.MainActivity::class.java)
                    mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(mainIntent)
                    finish()
                }
            }
        } ?: run {
            Log.e(TAG, "URI не получен в Intent!")
            finish()
        }
        
        setContent {
            TrustTheRouteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    OAuthCallbackScreen(
                        uri = intent.data,
                        authViewModel = authViewModel,
                        onSuccess = {
                            Log.d(TAG, "Авторизация успешна, возврат в MainActivity")
                            // Возвращаемся в MainActivity
                            // MainActivity проверит статус авторизации и обновит состояние
                            val mainIntent = Intent(this@YandexOAuthActivity, com.trusttheroute.app.MainActivity::class.java)
                            mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(mainIntent)
                            finish()
                        },
                        onError = { error ->
                            Log.e(TAG, "Ошибка авторизации: $error")
                            // Возвращаемся в MainActivity с ошибкой
                            val mainIntent = Intent(this@YandexOAuthActivity, com.trusttheroute.app.MainActivity::class.java)
                            mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(mainIntent)
                            finish()
                        }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent вызван с data: ${intent?.data}")
        setIntent(intent)
    }
}

@Composable
fun OAuthCallbackScreen(
    uri: Uri?,
    authViewModel: AuthViewModel,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // URI уже обработан в onCreate Activity, здесь только отслеживаем состояние

    // Слушаем изменения состояния авторизации
    val authState by authViewModel.authState.collectAsState()
    
    LaunchedEffect(authState) {
        when (val state = authState) {
            is com.trusttheroute.app.ui.viewmodel.AuthUiState.Success -> {
                Log.d("OAuthCallbackScreen", "Авторизация успешна")
                isLoading = false
                onSuccess()
            }
            is com.trusttheroute.app.ui.viewmodel.AuthUiState.Error -> {
                Log.e("OAuthCallbackScreen", "Ошибка авторизации: ${state.message}")
                errorMessage = state.message
                isLoading = false
                onError(state.message)
            }
            else -> {
                // Loading или Idle - продолжаем ждать
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Обработка авторизации...")
        } else if (errorMessage != null) {
            Text(
                text = "Ошибка: $errorMessage",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
