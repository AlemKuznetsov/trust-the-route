package com.trusttheroute.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trusttheroute.app.ui.theme.*
import com.trusttheroute.app.ui.viewmodel.AuthViewModel
import com.trusttheroute.app.ui.viewmodel.AuthUiState

@Composable
fun ResetPasswordScreen(
    onBackClick: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val isDarkTheme = isDarkTheme()
    val borderColor = if (isDarkTheme) DarkBorder else BorderLight
    
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    
    // Обработка состояния
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthUiState.Message -> {
                successMessage = state.message
                errorMessage = null
            }
            is AuthUiState.Error -> {
                errorMessage = state.message
                successMessage = null
            }
            is AuthUiState.Loading -> {
                errorMessage = null
                successMessage = null
            }
            else -> {}
        }
    }
    
    val gradientColors = if (isDarkTheme) {
        listOf(DarkBackground, DarkSurface, DarkBackground)
    } else {
        listOf(CyanAccent.copy(alpha = 0.1f), LightBackground)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = gradientColors))
    ) {
        TopAppBar(
            title = { Text("Восстановление пароля") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, "Назад")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = if (isDarkTheme) DarkSurface else White,
                titleContentColor = if (isDarkTheme) Blue400 else BluePrimary,
                navigationIconContentColor = if (isDarkTheme) Blue400 else BluePrimary
            ),
            modifier = Modifier.drawBehind {
                drawLine(
                    color = borderColor,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Восстановление пароля",
                style = MaterialTheme.typography.headlineMedium,
                color = if (isDarkTheme) Blue400 else BluePrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Введите ваш email, и мы отправим вам ссылку для восстановления пароля",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDarkTheme) DarkOnSurfacePlaceholder else LightOnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Поле email
            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it
                    errorMessage = null
                    successMessage = null
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = authState !is AuthUiState.Loading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = if (isDarkTheme) DarkOnSurface else LightOnSurface,
                    unfocusedTextColor = if (isDarkTheme) DarkOnSurface else LightOnSurface
                )
            )
            
            // Сообщение об успехе
            successMessage?.let { message ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) Blue700.copy(alpha = 0.2f) else BorderLight.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = message,
                        color = if (isDarkTheme) Blue400 else Blue700,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            // Сообщение об ошибке
            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            // Кнопка отправки
            Button(
                onClick = {
                    when {
                        email.isBlank() -> {
                            errorMessage = "Введите email"
                        }
                        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                            errorMessage = "Введите корректный email"
                        }
                        else -> {
                            authViewModel.resetPassword(email)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = authState !is AuthUiState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary
                )
            ) {
                if (authState is AuthUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = White
                    )
                } else {
                    Text("Отправить", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
