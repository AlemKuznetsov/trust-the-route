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
import com.trusttheroute.app.ui.viewmodel.LanguageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(
    onBackClick: () -> Unit,
    viewModel: LanguageViewModel = hiltViewModel()
) {
    val currentLanguageCode by viewModel.currentLanguage.collectAsState()
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
                        "Язык",
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
                    LanguageOption(
                        language = "Русский",
                        code = "ru",
                        isSelected = currentLanguageCode == "ru",
                        onClick = { viewModel.setLanguage("ru") }
                    )
                    
                    LanguageOption(
                        language = "English",
                        code = "en",
                        isSelected = currentLanguageCode == "en",
                        onClick = { viewModel.setLanguage("en") }
                    )
                    
                    LanguageOption(
                        language = "Deutsch",
                        code = "de",
                        isSelected = currentLanguageCode == "de",
                        onClick = { viewModel.setLanguage("de") }
                    )
                }
            }
        }
    }
}

@Composable
fun LanguageOption(
    language: String,
    code: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isDarkTheme = isDarkTheme()
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            if (isDarkTheme) Blue900.copy(alpha = 0.4f) else Color(0xFFDBEAFE)
        } else {
            if (isDarkTheme) DarkSurfaceVariant else White
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = language,
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
    }
}
