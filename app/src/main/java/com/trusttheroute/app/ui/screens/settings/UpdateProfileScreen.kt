package com.trusttheroute.app.ui.screens.settings

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trusttheroute.app.ui.theme.*
import com.trusttheroute.app.ui.viewmodel.AuthUiState
import com.trusttheroute.app.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfileScreen(
    onBackClick: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val isDarkTheme = isDarkTheme()

    var name by remember { mutableStateOf(currentUser?.name ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentUser) {
        currentUser?.name?.let { name = it }
    }

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthUiState.Success -> {
                onBackClick()
            }
            is AuthUiState.Error -> {
                errorMessage = state.message
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
                    "Редактирование профиля",
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

            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        authViewModel.updateProfile(name)
                    } else {
                        errorMessage = "Введите имя"
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
                    Text("Сохранить", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
