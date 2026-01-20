package com.trusttheroute.app.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.trusttheroute.app.data.auth.YandexAuthManager
import com.trusttheroute.app.ui.theme.TrustTheRouteTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Activity для обработки OAuth redirect от Yandex ID
 * 
 * Эта Activity должна быть зарегистрирована в манифесте с intent-filter
 * для обработки custom scheme: trusttheroute://oauth/yandex
 */
@AndroidEntryPoint
class YandexOAuthActivity : ComponentActivity() {
    
    @Inject
    lateinit var yandexAuthManager: YandexAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    private fun handleIntent(intent: Intent) {
        val uri = intent.data
        if (uri != null && uri.scheme == "trusttheroute" && uri.host == "oauth" && uri.pathSegments.contains("yandex")) {
            setContent {
                TrustTheRouteTheme {
                    OAuthProcessingScreen(uri, yandexAuthManager) { success ->
                        if (success) {
                            // Возвращаемся к предыдущей Activity с результатом
                            setResult(RESULT_OK)
                            finish()
                        } else {
                            // Показываем ошибку
                            setContent {
                                TrustTheRouteTheme {
                                    OAuthErrorScreen {
                                        setResult(RESULT_CANCELED)
                                        finish()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            finish()
        }
    }
}

@Composable
fun OAuthProcessingScreen(
    uri: Uri,
    yandexAuthManager: YandexAuthManager,
    onResult: (Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uri) {
        scope.launch {
            try {
                val result = yandexAuthManager.handleAuthCallback(uri)
                result.fold(
                    onSuccess = {
                        isLoading = false
                        onResult(true)
                    },
                    onFailure = { error ->
                        isLoading = false
                        errorMessage = error.message ?: "Неизвестная ошибка"
                    }
                )
            } catch (e: Exception) {
                isLoading = false
                errorMessage = e.message ?: "Ошибка при обработке авторизации"
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator()
                Text("Обработка авторизации...")
            } else {
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = { onResult(false) }) {
                        Text("Закрыть")
                    }
                }
            }
        }
    }
}

@Composable
fun OAuthErrorScreen(
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Ошибка авторизации",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
            Text("Не удалось выполнить вход через Yandex ID")
            Button(onClick = onClose) {
                Text("Закрыть")
            }
        }
    }
}
