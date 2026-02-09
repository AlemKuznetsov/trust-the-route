package com.trusttheroute.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trusttheroute.app.ui.theme.*
import com.trusttheroute.app.ui.viewmodel.AuthViewModel
import com.trusttheroute.app.ui.viewmodel.AuthUiState

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit = {},
    onTermsOfServiceClick: () -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val isDarkTheme = isDarkTheme()
    val borderColor = if (isDarkTheme) DarkBorder else BorderLight
    
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var termsAccepted by remember { mutableStateOf(false) }
    var privacyAccepted by remember { mutableStateOf(false) }
    
    // Обработка успешной регистрации
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthUiState.Success -> {
                onRegisterSuccess()
            }
            is AuthUiState.Error -> {
                errorMessage = state.message
            }
            else -> {}
        }
    }
    
    val gradientColors = if (isDarkTheme) {
        listOf(MainMenuDarkTop, MainMenuDarkBottom)
    } else {
        listOf(MainMenuLightTop, MainMenuLightBottom)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = gradientColors))
    ) {
        TopAppBar(
            title = { Text("Регистрация") },
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .windowInsetsPadding(WindowInsets.navigationBars), // Учитываем системную панель навигации
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Создать аккаунт",
                style = MaterialTheme.typography.headlineMedium,
                color = if (isDarkTheme) Blue400 else BluePrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Поле имени
            OutlinedTextField(
                value = name,
                onValueChange = { 
                    name = it
                    errorMessage = null
                },
                label = { Text("Имя") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = if (isDarkTheme) DarkOnSurface else LightOnSurface,
                    unfocusedTextColor = if (isDarkTheme) DarkOnSurface else LightOnSurface
                )
            )
            
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
            
            // Поле подтверждения пароля
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { 
                    confirmPassword = it
                    errorMessage = null
                },
                label = { Text("Подтвердите пароль") },
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
            
            // Согласие на обработку персональных данных
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp), // Добавляем вертикальный отступ для лучшей видимости
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Checkbox(
                    checked = privacyAccepted,
                    onCheckedChange = { privacyAccepted = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = if (isDarkTheme) Blue400 else BluePrimary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                val privacyText = buildAnnotatedString {
                    val defaultColor = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                    val linkColor = if (isDarkTheme) Blue400 else BluePrimary
                    
                    withStyle(style = SpanStyle(color = defaultColor)) {
                        append("Я согласен на ")
                    }
                    pushStringAnnotation(
                        tag = "privacy",
                        annotation = "privacy_policy"
                    )
                    withStyle(
                        style = SpanStyle(
                            color = linkColor,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("обработку персональных данных")
                    }
                    pop()
                }
                ClickableText(
                    text = privacyText,
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                        lineHeight = MaterialTheme.typography.bodySmall.lineHeight,
                        letterSpacing = MaterialTheme.typography.bodySmall.letterSpacing
                    ),
                    modifier = Modifier.weight(1f),
                    onClick = { offset ->
                        privacyText.getStringAnnotations(
                            tag = "privacy",
                            start = offset,
                            end = offset
                        ).firstOrNull()?.let {
                            onPrivacyPolicyClick()
                        }
                    }
                )
            }
            
            // Принятие пользовательского соглашения
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp), // Добавляем вертикальный отступ для лучшей видимости
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = if (isDarkTheme) Blue400 else BluePrimary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                val termsText = buildAnnotatedString {
                    val defaultColor = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                    val linkColor = if (isDarkTheme) Blue400 else BluePrimary
                    
                    withStyle(style = SpanStyle(color = defaultColor)) {
                        append("Я принимаю ")
                    }
                    pushStringAnnotation(
                        tag = "terms",
                        annotation = "terms_of_service"
                    )
                    withStyle(
                        style = SpanStyle(
                            color = linkColor,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("пользовательское соглашение")
                    }
                    pop()
                }
                ClickableText(
                    text = termsText,
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                        lineHeight = MaterialTheme.typography.bodySmall.lineHeight,
                        letterSpacing = MaterialTheme.typography.bodySmall.letterSpacing
                    ),
                    modifier = Modifier.weight(1f),
                    onClick = { offset ->
                        termsText.getStringAnnotations(
                            tag = "terms",
                            start = offset,
                            end = offset
                        ).firstOrNull()?.let {
                            onTermsOfServiceClick()
                        }
                    }
                )
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
            
            // Кнопка регистрации с дополнительным отступом снизу
            Button(
                onClick = {
                    when {
                        name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                            errorMessage = "Заполните все поля"
                        }
                        password != confirmPassword -> {
                            errorMessage = "Пароли не совпадают"
                        }
                        password.length < 6 -> {
                            errorMessage = "Пароль должен содержать минимум 6 символов"
                        }
                        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                            errorMessage = "Введите корректный email"
                        }
                        !privacyAccepted -> {
                            errorMessage = "Необходимо согласие на обработку персональных данных"
                        }
                        !termsAccepted -> {
                            errorMessage = "Необходимо принять пользовательское соглашение"
                        }
                        else -> {
                            authViewModel.register(email, password, name)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 16.dp), // Дополнительный отступ снизу
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
                    Text("Зарегистрироваться", fontWeight = FontWeight.Bold)
                }
            }
            
            // Дополнительный отступ для системной панели навигации
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
