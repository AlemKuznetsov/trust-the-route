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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trusttheroute.app.ui.theme.*
import com.trusttheroute.app.ui.viewmodel.AuthUiState
import com.trusttheroute.app.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAccountScreen(
    onBackClick: () -> Unit,
    onAccountDeleted: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val isDarkTheme = isDarkTheme()

    var confirmationText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val requiredText = "УДАЛИТЬ"

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthUiState.Message -> {
                onAccountDeleted()
            }
            is AuthUiState.Error -> {
                errorMessage = state.message
                showConfirmDialog = false
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
                    "Удаление учетной записи",
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

            // Предупреждение
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Внимание!",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Удаление учетной записи является необратимым действием. Все ваши данные будут безвозвратно удалены.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDarkTheme) DarkOnSurface else LightOnSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Для подтверждения удаления введите:",
                style = MaterialTheme.typography.bodyLarge,
                color = if (isDarkTheme) DarkOnSurface else LightOnSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "\"$requiredText\"",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = confirmationText,
                onValueChange = {
                    confirmationText = it
                    errorMessage = null
                },
                label = { Text("Подтверждение") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = if (isDarkTheme) DarkOnSurface else LightOnSurface,
                    unfocusedTextColor = if (isDarkTheme) DarkOnSurface else LightOnSurface,
                    focusedContainerColor = if (isDarkTheme) DarkSurface else White,
                    unfocusedContainerColor = if (isDarkTheme) DarkSurface else White
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
                    if (confirmationText == requiredText) {
                        showConfirmDialog = true
                    } else {
                        errorMessage = "Текст подтверждения не совпадает"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = authState !is AuthUiState.Loading && confirmationText == requiredText,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                if (authState is AuthUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = White
                    )
                } else {
                    Text("Удалить учетную запись", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Диалог подтверждения
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    "Подтвердите удаление",
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Text(
                    "Вы уверены, что хотите удалить свою учетную запись? Это действие нельзя отменить.",
                    color = if (isDarkTheme) DarkOnSurface else LightOnSurface
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        authViewModel.deleteAccount(confirmationText)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Удалить", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmDialog = false }
                ) {
                    Text("Отмена")
                }
            },
            containerColor = if (isDarkTheme) DarkSurface else White
        )
    }
}
