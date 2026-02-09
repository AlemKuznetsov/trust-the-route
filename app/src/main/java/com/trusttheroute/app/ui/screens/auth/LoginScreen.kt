package com.trusttheroute.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trusttheroute.app.ui.theme.*
import com.trusttheroute.app.ui.viewmodel.AuthViewModel
import com.trusttheroute.app.ui.viewmodel.AuthUiState

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onResetPasswordClick: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsState()
    val isDarkTheme = isDarkTheme()
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Обработка успешной авторизации
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthUiState.Success -> {
                onLoginSuccess()
            }
            is AuthUiState.Error -> {
                errorMessage = state.message
            }
            else -> {}
        }
    }
    
    // Градиентный фон как в главном меню
    val gradientColors = if (isDarkTheme) {
        listOf(
            MainMenuDarkTop,   // #0F172A
            MainMenuDarkBottom // #020617
        )
    } else {
        listOf(
            MainMenuLightTop,     // #FFFFFF
            MainMenuLightBottom   // #F2F5F9
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = gradientColors))
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .windowInsetsPadding(WindowInsets.navigationBars), // Учитываем системную панель навигации
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        
        // Заголовок
        Text(
            text = "Trust The Route",
            style = MaterialTheme.typography.displayMedium,
            color = if (isDarkTheme) Blue400 else BluePrimary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "Вход в приложение",
            style = MaterialTheme.typography.titleLarge,
            color = if (isDarkTheme) DarkOnSurface else LightOnSurface,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Поле email
        OutlinedTextField(
            value = email,
            onValueChange = { 
                email = it
                errorMessage = null
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = if (isDarkTheme) DarkOnSurface else LightOnSurface,
                unfocusedTextColor = if (isDarkTheme) DarkOnSurface else LightOnSurface
            )
        )
        
        // Поле пароля
        OutlinedTextField(
            value = password,
            onValueChange = { 
                password = it
                errorMessage = null
            },
            label = { Text("Пароль") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Скрыть пароль" else "Показать пароль"
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = if (isDarkTheme) DarkOnSurface else LightOnSurface,
                unfocusedTextColor = if (isDarkTheme) DarkOnSurface else LightOnSurface
            )
        )
        
        // Сообщение об ошибке
        errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodySmall
            )
        }
        
        // Кнопка входа
        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    authViewModel.login(email, password)
                } else {
                    errorMessage = "Заполните все поля"
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
                Text("Войти", fontWeight = FontWeight.Bold)
            }
        }
        
        // Разделитель
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(modifier = Modifier.weight(1f))
            Text(
                text = "или",
                modifier = Modifier.padding(horizontal = 16.dp),
                color = if (isDarkTheme) DarkOnSurfacePlaceholder else LightOnSurfaceVariant
            )
            Divider(modifier = Modifier.weight(1f))
        }
        
        // Кнопка входа через Yandex ID
        OutlinedButton(
            onClick = {
                if (context is android.app.Activity) {
                    authViewModel.loginWithYandex(context)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = authState !is AuthUiState.Loading,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (isDarkTheme) Blue400 else BluePrimary
            ),
            border = BorderStroke(
                width = 2.dp,
                color = if (isDarkTheme) Blue400 else BluePrimary
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Войти через Yandex ID",
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Ссылки с дополнительным отступом снизу для системной панели навигации
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp), // Дополнительный отступ снизу
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onResetPasswordClick) {
                Text(
                    "Забыли пароль?",
                    color = if (isDarkTheme) Blue400 else BluePrimary
                )
            }
            TextButton(onClick = onRegisterClick) {
                Text(
                    "Регистрация",
                    color = if (isDarkTheme) Blue400 else BluePrimary
                )
            }
        }
        
        // Дополнительный отступ для системной панели навигации
        Spacer(modifier = Modifier.height(32.dp))
    }
}
