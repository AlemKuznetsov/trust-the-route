package com.trusttheroute.app.ui.screens.settings

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.trusttheroute.app.ui.theme.*
import com.trusttheroute.app.ui.viewmodel.NotificationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val isDarkTheme = isDarkTheme()
    
    // Проверка системного разрешения на уведомления
    val hasNotificationPermission = remember {
        derivedStateOf {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        }
    }
    
    // Launcher для запроса разрешения на уведомления
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Если разрешение получено и пользователь хотел включить уведомления, включаем их
        if (isGranted && !notificationsEnabled) {
            viewModel.setNotificationsEnabled(true)
        }
    }
    
    // Градиентный фон
    val gradientBrush = if (isDarkTheme) {
        Brush.linearGradient(
            colors = listOf(
                DarkBackground,  // slate-900
                DarkSurface,      // slate-800
                DarkBackground    // slate-900
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFEFF6FF), // blue-50
                Color(0xFFFFFFFF), // white
                Color(0xFFECFEFF)  // cyan-50
            ),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
        )
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = { 
                    Text(
                        "Уведомления",
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (isDarkTheme) Blue400 else BluePrimary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            "Назад",
                            tint = if (isDarkTheme) Blue400 else BluePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDarkTheme) DarkSurface else White,
                    titleContentColor = if (isDarkTheme) Blue400 else BluePrimary,
                    navigationIconContentColor = if (isDarkTheme) Blue400 else BluePrimary
                ),
                modifier = Modifier.drawBehind {
                    // Рисуем только нижнюю границу
                    val borderColor = if (isDarkTheme) DarkBorder else BorderLight
                    drawLine(
                        color = borderColor,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            )
            
            AnimatedVisibility(
                visible = true,
                enter = slideInHorizontally(
                    initialOffsetX = { 20 },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(300))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Описание
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDarkTheme) DarkSurface else White
                        ),
                        elevation = if (!isDarkTheme) CardDefaults.cardElevation(defaultElevation = 2.dp) else CardDefaults.cardElevation(defaultElevation = 0.dp),
                        border = if (isDarkTheme) androidx.compose.foundation.BorderStroke(1.dp, DarkBorder) else null
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Push-уведомления",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isDarkTheme) Blue400 else BluePrimary
                            )
                            Text(
                                text = "Получайте уведомления о новых маршрутах и обновлениях приложения",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isDarkTheme) DarkOnSurfaceSecondary else LightOnSurfaceVariant
                            )
                        }
                    }
                    
                    // Информация о системном разрешении (для Android 13+)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission.value) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDarkTheme) DarkSurface else Color(0xFFFFF7ED)
                            ),
                            border = if (isDarkTheme) androidx.compose.foundation.BorderStroke(1.dp, DarkBorder) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = if (isDarkTheme) Blue400 else Color(0xFFF59E0B),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Системное разрешение не предоставлено",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (isDarkTheme) Blue400 else Color(0xFFF59E0B),
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "Для работы уведомлений необходимо разрешение системы",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isDarkTheme) DarkOnSurfaceVariant else Color(0xFF92400E),
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                                TextButton(
                                    onClick = {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                ) {
                                    Text(
                                        "Разрешить",
                                        color = if (isDarkTheme) Blue400 else BluePrimary
                                    )
                                }
                            }
                        }
                    }
                    
                    // Переключатель уведомлений
                    ThemeOptionCard(
                        title = "Включить уведомления",
                        isEnabled = notificationsEnabled && hasNotificationPermission.value,
                        onToggle = { 
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission.value) {
                                // Если нет системного разрешения, запрашиваем его
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                // Иначе переключаем настройку приложения
                                viewModel.setNotificationsEnabled(!notificationsEnabled)
                            }
                        }
                    )
                    
                    // Информация о статусе
                    if (notificationsEnabled && hasNotificationPermission.value) {
                        Text(
                            text = "✓ Уведомления включены и готовы к работе",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDarkTheme) Color(0xFF4ADE80) else Color(0xFF16A34A),
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                        )
                    } else if (notificationsEnabled && !hasNotificationPermission.value) {
                        Text(
                            text = "⚠ Уведомления включены в приложении, но требуется системное разрешение",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDarkTheme) Color(0xFFFBBF24) else Color(0xFFF59E0B),
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
