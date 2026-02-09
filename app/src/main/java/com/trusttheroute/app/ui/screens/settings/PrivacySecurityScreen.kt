package com.trusttheroute.app.ui.screens.settings

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.trusttheroute.app.ui.theme.*
import com.trusttheroute.app.ui.viewmodel.AuthViewModel
import com.trusttheroute.app.ui.viewmodel.NotificationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySecurityScreen(
    onBackClick: () -> Unit,
    onTermsOfServiceClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    notificationsViewModel: NotificationsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isDarkTheme = isDarkTheme()
    val authState by authViewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val notificationsEnabled by notificationsViewModel.notificationsEnabled.collectAsState()
    
    // Обработка сообщений от ViewModel
    LaunchedEffect(authState) {
        when (val state = authState) {
            is com.trusttheroute.app.ui.viewmodel.AuthUiState.Message -> {
                snackbarHostState.showSnackbar(state.message)
            }
            is com.trusttheroute.app.ui.viewmodel.AuthUiState.Error -> {
                snackbarHostState.showSnackbar("Ошибка: ${state.message}")
            }
            else -> {}
        }
    }
    
    // Проверка разрешений
    val hasLocationPermission = remember {
        derivedStateOf {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    val hasStoragePermission = remember {
        derivedStateOf {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13+ - разрешения на хранилище не требуются для большинства случаев
                true
            } else {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            }
        }
    }
    
    val hasNotificationPermission = remember {
        derivedStateOf {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        }
    }
    
    val gradientBrush = if (isDarkTheme) {
        Brush.linearGradient(
            colors = listOf(DarkBackground, DarkSurface, DarkBackground),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(LightBackground, LightSurface, LightBackground),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Конфиденциальность и безопасность",
                        color = if (isDarkTheme) Blue400 else LightOnSurface,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = if (isDarkTheme) Blue400 else LightOnSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDarkTheme) DarkSurface else LightSurface
                )
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        containerColor = if (isDarkTheme) DarkBackground else LightBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Раздел: Разрешения приложения
                Text(
                    text = "Разрешения приложения",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isDarkTheme) Blue400 else BluePrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                PermissionCard(
                    title = "Геолокация",
                    description = "Используется для определения вашего местоположения на карте и показа ближайших достопримечательностей",
                    icon = Icons.Default.LocationOn,
                    isGranted = hasLocationPermission.value,
                    onOpenSettings = {
                        openAppSettings(context)
                    },
                    isDarkTheme = isDarkTheme
                )
                
                PermissionCard(
                    title = "Уведомления",
                    description = if (hasNotificationPermission.value && notificationsEnabled) {
                        "Включено в системе и приложении"
                    } else if (hasNotificationPermission.value && !notificationsEnabled) {
                        "Разрешено в системе, но выключено в приложении"
                    } else if (!hasNotificationPermission.value && notificationsEnabled) {
                        "Включено в приложении, но требуется системное разрешение"
                    } else {
                        "Используется для отправки уведомлений о важных событиях"
                    },
                    icon = Icons.Default.Notifications,
                    isGranted = hasNotificationPermission.value && notificationsEnabled,
                    onOpenSettings = {
                        openAppSettings(context)
                    },
                    isDarkTheme = isDarkTheme
                )
                
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    PermissionCard(
                        title = "Доступ к хранилищу",
                        description = "Используется для сохранения медиафайлов",
                        icon = Icons.Default.Folder,
                        isGranted = hasStoragePermission.value,
                        onOpenSettings = {
                            openAppSettings(context)
                        },
                        isDarkTheme = isDarkTheme
                    )
                }
                
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = if (isDarkTheme) DarkSurfaceVariant else LightSurfaceVariant
                )
                
                // Раздел: Документы
                Text(
                    text = "Документы",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isDarkTheme) Blue400 else BluePrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                DocumentCard(
                    title = "Политика конфиденциальности",
                    description = "Узнайте, как мы собираем, используем и защищаем ваши данные",
                    icon = Icons.Default.PrivacyTip,
                    onClick = onPrivacyPolicyClick,
                    isDarkTheme = isDarkTheme
                )
                
                DocumentCard(
                    title = "Пользовательское соглашение",
                    description = "Условия использования приложения Trust The Route",
                    icon = Icons.Default.Description,
                    onClick = onTermsOfServiceClick,
                    isDarkTheme = isDarkTheme
                )
                
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = if (isDarkTheme) DarkSurfaceVariant else LightSurfaceVariant
                )
                
                // Раздел: Безопасность
                Text(
                    text = "Безопасность",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isDarkTheme) Blue400 else BluePrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                SecurityInfoCard(
                    title = "Управление сессиями",
                    description = "Просмотр и управление активными сессиями",
                    icon = Icons.Default.Devices,
                    onClick = {
                        // TODO: Navigate to sessions management
                    },
                    isDarkTheme = isDarkTheme
                )
                
                SecurityInfoCard(
                    title = "Выход со всех устройств",
                    description = "Завершить все активные сессии на всех устройствах",
                    icon = Icons.Default.Logout,
                    onClick = {
                        authViewModel.logoutFromAllDevices()
                    },
                    isDarkTheme = isDarkTheme
                )
                
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = if (isDarkTheme) DarkSurfaceVariant else LightSurfaceVariant
                )
                
                // Раздел: Информация о безопасности
                Text(
                    text = "Информация",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isDarkTheme) Blue400 else BluePrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                InfoCard(
                    text = "Данные пользователя хранятся и передаются в зашифрованном виде",
                    icon = Icons.Default.Lock,
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }
}

@Composable
fun PermissionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isGranted: Boolean,
    onOpenSettings: () -> Unit,
    isDarkTheme: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenSettings),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) DarkSurface else LightSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isDarkTheme) Blue400 else BluePrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = if (isDarkTheme) Blue400 else LightOnSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isGranted) "Включено" else "Выключено",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isGranted) {
                    if (isDarkTheme) Color(0xFF4ADE80) else Color(0xFF16A34A)
                } else {
                    if (isDarkTheme) Color(0xFFF87171) else Color(0xFFDC2626)
                },
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun DocumentCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    isDarkTheme: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) DarkSurface else LightSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDarkTheme) Blue400 else BluePrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkTheme) Blue400 else LightOnSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
            )
        }
    }
}

@Composable
fun SecurityInfoCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    isDarkTheme: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) DarkSurface else LightSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDarkTheme) Blue400 else BluePrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkTheme) Blue400 else LightOnSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
            )
        }
    }
}

@Composable
fun InfoCard(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isDarkTheme: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) DarkSurface else LightSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDarkTheme) Blue400 else BluePrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
            )
        }
    }
}

private fun openAppSettings(context: Context) {
    val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}
