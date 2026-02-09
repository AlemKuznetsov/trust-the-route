package com.trusttheroute.app.ui.screens.main

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import com.trusttheroute.app.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.trusttheroute.app.ui.theme.*
import com.trusttheroute.app.ui.viewmodel.MainMenuViewModel
import kotlinx.coroutines.launch

@Composable
fun MainMenuScreen(
    onRoutesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit,
    viewModel: MainMenuViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isDarkTheme = isDarkTheme()
    
    // Градиент фона
    val gradientColors = if (isDarkTheme) {
        listOf(
            MainMenuDarkTop,      // #0F172A
            MainMenuDarkBottom    // #020617
        )
    } else {
        listOf(
            MainMenuLightTop,     // #FFFFFF
            MainMenuLightBottom   // #F2F5F9
        )
    }
    
    // Проверка разрешения на уведомления
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
    
    val permissionRequested by viewModel.notificationPermissionRequested.collectAsState()
    
    // Флаг для показа диалога
    var showNotificationDialog by remember { mutableStateOf(false) }
    
    // Launcher для запроса разрешения на уведомления
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        scope.launch {
            viewModel.setNotificationPermissionRequested(true)
        }
        showNotificationDialog = false
    }
    
    // Проверяем, был ли уже запрошен разрешение при первом входе
    LaunchedEffect(permissionRequested, hasNotificationPermission.value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!permissionRequested && !hasNotificationPermission.value) {
                // Первый вход и разрешение не запрошено
                showNotificationDialog = true
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(colors = gradientColors)
            )
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Заголовок (верх)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 64.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Trust The Route",
                fontSize = 26.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDarkTheme) MainMenuDarkTitle else MainMenuLightTitle,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Твой гид по городу на общественном транспорте",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = if (isDarkTheme) MainMenuDarkSubtitle else MainMenuLightSubtitle
            )
        }
        
        // Центральная часть (изображение)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            // Изображение увеличенного размера
            Image(
                painter = painterResource(id = R.drawable.main_menu_image),
                contentDescription = "Trust The Route",
                modifier = Modifier
                    .fillMaxWidth() // Такая же ширина как у кнопки "Маршруты"
                    .heightIn(max = 450.dp) // Увеличили максимальную высоту до 450dp
                    .offset(y = (-32).dp), // Используем offset для поднятия изображения
                contentScale = ContentScale.Fit // Сохраняем пропорции изображения
                // Временно без colorFilter для проверки
            )
        }
        
        // Блок кнопок (низ)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Кнопка "Маршруты" (Primary)
            RoutesButton(
                onClick = onRoutesClick,
                isDarkTheme = isDarkTheme
            )
            
            // Кнопка "Настройки" (Secondary)
            SettingsButton(
                onClick = onSettingsClick,
                isDarkTheme = isDarkTheme
            )
            
            // Кнопка "О приложении" (Tertiary)
            AboutButton(
                onClick = onAboutClick,
                isDarkTheme = isDarkTheme,
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    }
    
    // Диалог запроса разрешения на уведомления
    if (showNotificationDialog) {
        AlertDialog(
            onDismissRequest = {
                scope.launch {
                    viewModel.setNotificationPermissionRequested(true)
                }
                showNotificationDialog = false
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = if (isDarkTheme) Blue400 else BluePrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Разрешить уведомления?",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (isDarkTheme) Blue400 else BluePrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Text(
                    text = "Мы хотели бы отправлять вам уведомления о новых маршрутах и важных обновлениях приложения.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDarkTheme) DarkOnSurfaceSecondary else LightOnSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            scope.launch {
                                viewModel.setNotificationPermissionRequested(true)
                            }
                            showNotificationDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDarkTheme) Blue500 else BluePrimary
                    )
                ) {
                    Text("Разрешить", color = White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            viewModel.setNotificationPermissionRequested(true)
                        }
                        showNotificationDialog = false
                    }
                ) {
                    Text(
                        "Не сейчас",
                        color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                    )
                }
            },
            containerColor = if (isDarkTheme) DarkSurface else White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun RoutesButton(
    onClick: () -> Unit,
    isDarkTheme: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1.01f,
        animationSpec = tween(200),
        label = "scale"
    )
    
    var iconOffset by remember { mutableStateOf(0.dp) }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .then(
                if (isDarkTheme) {
                    Modifier.shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(24.dp)
                    )
                } else {
                    Modifier.shadow(
                        elevation = 1.dp,
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            )
            .clip(RoundedCornerShape(if (isDarkTheme) 24.dp else 16.dp))
            .background(RoutesButtonBlue)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 28.dp, horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Маршруты",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = White
                )
                Text(
                    text = "Выберите маршрут транспорта",
                    fontSize = 14.sp,
                    color = if (isDarkTheme) RoutesButtonSubtitleDark else RoutesButtonSubtitleLight
                )
            }
            Icon(
                imageVector = Icons.Default.Map,
                contentDescription = null,
                tint = White,
                modifier = Modifier
                    .size(24.dp)
                    .offset(x = iconOffset)
            )
        }
    }
}

@Composable
fun SettingsButton(
    onClick: () -> Unit,
    isDarkTheme: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1.005f,
        animationSpec = tween(200),
        label = "scale"
    )
    
    var iconRotation by remember { mutableStateOf(0f) }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .then(
                if (!isDarkTheme) {
                    Modifier.shadow(
                        elevation = 1.dp,
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier
                }
            )
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isDarkTheme) MainMenuDarkButtonBg else MainMenuLightTop
            )
            .border(
                width = 1.dp,
                color = if (isDarkTheme) MainMenuDarkButtonBorder else MainMenuLightButtonBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Настройки",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDarkTheme) MainMenuDarkSettingsText else MainMenuLightSettingsText
            )
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = if (isDarkTheme) MainMenuDarkSettingsIcon else MainMenuLightSubtitle,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun AboutButton(
    onClick: () -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1.005f,
        animationSpec = tween(200),
        label = "scale"
    )
    
    var iconScale by remember { mutableStateOf(1f) }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Transparent)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = if (isDarkTheme) MainMenuDarkAboutIcon else MainMenuLightAboutIcon,
                modifier = Modifier
                    .size(24.dp)
                    .scale(iconScale)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "О приложении",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDarkTheme) MainMenuDarkAboutText else MainMenuLightAboutText
            )
        }
    }
}
