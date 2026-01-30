package com.trusttheroute.app.ui.screens.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.trusttheroute.app.ui.viewmodel.FontSizeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontSizeScreen(
    onBackClick: () -> Unit,
    viewModel: FontSizeViewModel = hiltViewModel()
) {
    val currentFontSize by viewModel.currentFontSize.collectAsState()
    val isDarkTheme = isDarkTheme()
    
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
                        "Размер шрифта",
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                text = "Выберите размер шрифта",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isDarkTheme) Blue400 else BluePrimary
                            )
                            Text(
                                text = "Настройте размер текста для комфортного чтения",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isDarkTheme) DarkOnSurfaceSecondary else LightOnSurfaceVariant
                            )
                        }
                    }
                    
                    // Маленький размер
                    FontSizeOption(
                        label = "Маленький",
                        previewText = "Пример текста маленького размера",
                        size = "small",
                        isSelected = currentFontSize == "small",
                        onClick = { viewModel.setFontSize("small") },
                        fontSizeMultiplier = 0.85f
                    )
                    
                    // Средний размер (по умолчанию)
                    FontSizeOption(
                        label = "Средний",
                        previewText = "Пример текста среднего размера",
                        size = "medium",
                        isSelected = currentFontSize == "medium",
                        onClick = { viewModel.setFontSize("medium") },
                        fontSizeMultiplier = 1.0f
                    )
                    
                    // Большой размер
                    FontSizeOption(
                        label = "Большой",
                        previewText = "Пример текста большого размера",
                        size = "large",
                        isSelected = currentFontSize == "large",
                        onClick = { viewModel.setFontSize("large") },
                        fontSizeMultiplier = 1.2f
                    )
                }
            }
        }
    }
}

@Composable
fun FontSizeOption(
    label: String,
    previewText: String,
    size: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    fontSizeMultiplier: Float
) {
    val isDarkTheme = isDarkTheme()
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            if (isDarkTheme) Blue900.copy(alpha = 0.4f) else Color(0xFFDBEAFE)
        } else {
            if (isDarkTheme) DarkSurface else White
        },
        animationSpec = tween(200)
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) {
            if (isDarkTheme) Blue400 else BluePrimary
        } else {
            if (isDarkTheme) DarkBorderHover else BorderLight
        },
        animationSpec = tween(200)
    )
    
    val textColor by animateColorAsState(
        targetValue = if (isSelected) {
            if (isDarkTheme) DarkOnSurfaceVariant else BluePrimary
        } else {
            if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurface
        },
        animationSpec = tween(200)
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = if (!isDarkTheme) CardDefaults.cardElevation(defaultElevation = 2.dp) else CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    ),
                    color = textColor
                )
                
                AnimatedVisibility(
                    visible = isSelected,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = if (isDarkTheme) Blue400 else BluePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            // Превью текста с соответствующим размером
            Text(
                text = previewText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize * fontSizeMultiplier
                ),
                color = if (isDarkTheme) DarkOnSurfacePlaceholder else LightOnSurfaceVariant
            )
        }
    }
}
