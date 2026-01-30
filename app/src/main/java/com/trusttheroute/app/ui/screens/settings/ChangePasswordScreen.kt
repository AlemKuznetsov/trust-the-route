package com.trusttheroute.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trusttheroute.app.ui.theme.*
import com.trusttheroute.app.ui.viewmodel.AuthUiState
import com.trusttheroute.app.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onBackClick: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val isDarkTheme = isDarkTheme()

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

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
            else -> {}
        }
    }

    val gradientBrush = if (isDarkTheme) {
        Brush.linearGradient(
            colors = listOf(DarkBackground, DarkSurface, DarkBackground),
            start = Offset(0f, 0f),
            end = Offset(1000f, 1000f)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color(0xFFEFF6FF), Color(0xFFFFFFFF), Color(0xFFECFEFF)),
            start = Offset(0f, 0f),
            end = Offset(1000f, 1000f)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush)
    ) {
        TopAppBar(
            title = {
                Text(
                    "Смена пароля",
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (isDarkTheme) Blue400 else BluePrimary
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.Default.ArrowBack,
                        "Назад",
                        tint = if (isDarkTheme) Blue400 else BluePrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = if (isDarkTheme) DarkSurface else White,
                titleContentColor = if (isDarkTheme) Blue400 else BluePrimary,
                navigationIconContentColor = if (isDarkTheme) Blue400 else BluePrimary
            ),
            modifier = Modifier.drawBehind {
                drawLine(
                    color = if (isDarkTheme) DarkBorder else BorderLight,
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

            OutlinedTextField(
                value = oldPassword,
                onValueChange = {
                    oldPassword = it
                    errorMessage = null
                },
                label = { Text("Текущий пароль") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (oldPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { oldPasswordVisible = !oldPasswordVisible }) {
                        Icon(
                            imageVector = if (oldPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (oldPasswordVisible) "Скрыть пароль" else "Показать пароль"
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = if (isDarkTheme) DarkOnSurface else LightOnSurface,
                    unfocusedTextColor = if (isDarkTheme) DarkOnSurface else LightOnSurface
                )
            )

            OutlinedTextField(
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    errorMessage = null
                },
                label = { Text("Новый пароль") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                        Icon(
                            imageVector = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (newPasswordVisible) "Скрыть пароль" else "Показать пароль"
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = if (isDarkTheme) DarkOnSurface else LightOnSurface,
                    unfocusedTextColor = if (isDarkTheme) DarkOnSurface else LightOnSurface
                )
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = null
                },
                label = { Text("Подтвердите новый пароль") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) "Скрыть пароль" else "Показать пароль"
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = if (isDarkTheme) DarkOnSurface else LightOnSurface,
                    unfocusedTextColor = if (isDarkTheme) DarkOnSurface else LightOnSurface
                )
            )

            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodySmall
                )
            }

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

            Button(
                onClick = {
                    when {
                        oldPassword.isBlank() -> errorMessage = "Введите текущий пароль"
                        newPassword.isBlank() -> errorMessage = "Введите новый пароль"
                        newPassword.length < 6 -> errorMessage = "Пароль должен содержать минимум 6 символов"
                        newPassword != confirmPassword -> errorMessage = "Пароли не совпадают"
                        else -> authViewModel.changePassword(oldPassword, newPassword)
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
                    Text("Изменить пароль", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
