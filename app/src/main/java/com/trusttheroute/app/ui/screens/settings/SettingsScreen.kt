package com.trusttheroute.app.ui.screens.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.trusttheroute.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onLanguageClick: () -> Unit = {},
    onThemeClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onFontSizeClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    onAboutClick: () -> Unit = {}
) {
    val isDarkTheme = isDarkTheme()
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground
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
            // TopAppBar с белым фоном и синей границей
            TopAppBar(
                title = { 
                    Text(
                        "Настройки",
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (isDarkTheme) Blue400 else BluePrimary
                    ) 
                },
                navigationIcon = {
                    val interactionSource = remember { MutableInteractionSource() }
                    val isHovered by interactionSource.collectIsHoveredAsState()
                    val backgroundColor by animateColorAsState(
                        targetValue = if (isHovered) {
                            if (isDarkTheme) DarkSurfaceVariant else Color(0xFFEFF6FF)
                        } else {
                            Color.Transparent
                        },
                        animationSpec = tween(200)
                    )
                    
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(backgroundColor)
                    ) {
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
                modifier = Modifier.border(
                    width = 1.dp,
                    color = if (isDarkTheme) DarkBorder else BorderLight,
                    shape = RoundedCornerShape(0.dp)
                )
            )
            
            // Список категорий с анимацией
            AnimatedVisibility(
                visible = true,
                enter = slideInHorizontally(
                    initialOffsetX = { -20 },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(300))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Настройка учетной записи
                    SettingsCategoryCard(
                        title = "Настройка учетной записи",
                        icon = Icons.Default.Person,
                        iconBackgroundColor = Color(0xFFEFF6FF), // blue-50
                        iconTint = BluePrimary, // blue-600
                        onClick = onAccountClick
                    )
                    
                    // Язык
                    SettingsCategoryCard(
                        title = "Язык",
                        icon = Icons.Default.Public,
                        iconBackgroundColor = Color(0xFFECFEFF), // cyan-50
                        iconTint = Color(0xFF0891B2), // cyan-600
                        onClick = onLanguageClick
                    )
                    
                    // Тема приложения
                    SettingsCategoryCard(
                        title = "Тема приложения",
                        icon = Icons.Default.Palette,
                        iconBackgroundColor = Color(0xFFEEF2FF), // indigo-50
                        iconTint = IndigoAccent, // indigo-600
                        onClick = onThemeClick
                    )
                    
                    // Уведомления
                    SettingsCategoryCard(
                        title = "Уведомления",
                        icon = Icons.Default.Notifications,
                        iconBackgroundColor = Color(0xFFEFF6FF), // blue-50
                        iconTint = Color(0xFF3B82F6), // blue-500
                        onClick = onNotificationsClick
                    )
                    
                    // Размер шрифта
                    SettingsCategoryCard(
                        title = "Размер шрифта",
                        icon = Icons.Default.FormatSize,
                        iconBackgroundColor = Color(0xFFF8FAFC), // slate-50
                        iconTint = LightOnSurfaceVariant, // slate-600
                        onClick = onFontSizeClick
                    )
                    
                    // Конфиденциальность и безопасность
                    SettingsCategoryCard(
                        title = "Конфиденциальность и безопасность",
                        icon = Icons.Default.Security,
                        iconBackgroundColor = Color(0xFFF0FDF4), // green-50
                        iconTint = Color(0xFF16A34A), // green-600
                        onClick = onPrivacyClick
                    )
                    
                    // Информация о приложении
                    SettingsCategoryCard(
                        title = "Информация о приложении",
                        icon = Icons.Default.Info,
                        iconBackgroundColor = Color(0xFFFAF5FF), // purple-50
                        iconTint = Color(0xFF9333EA), // purple-600
                        onClick = onAboutClick
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsCategoryCard(
    title: String,
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    val isDarkTheme = isDarkTheme()
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isHovered) {
            if (isDarkTheme) DarkSurfaceVariant else Color(0xFFEFF6FF)
        } else {
            if (isDarkTheme) DarkSurface else White
        },
        animationSpec = tween(200)
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (isHovered) {
            if (isDarkTheme) Blue500 else Color(0xFF93C5FD)
        } else {
            if (isDarkTheme) DarkBorder else BorderLight
        },
        animationSpec = tween(200)
    )
    
    val elevation by animateDpAsState(
        targetValue = if (isHovered) 4.dp else 2.dp,
        animationSpec = tween(200)
    )
    
    val chevronOffset by animateDpAsState(
        targetValue = if (isHovered) 4.dp else 0.dp,
        animationSpec = tween(200)
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = if (!isDarkTheme) CardDefaults.cardElevation(defaultElevation = elevation) else CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Контейнер иконки с фоном
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDarkTheme) DarkSurfaceVariant else iconBackgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isDarkTheme) DarkOnSurfaceSecondary else LightOnSurface
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = if (isDarkTheme) DarkOnSurfaceDescription else LightOnSurfaceVariant,
                modifier = Modifier
                    .size(20.dp)
                    .offset(x = chevronOffset)
            )
        }
    }
}
