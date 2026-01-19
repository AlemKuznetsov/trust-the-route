package com.trusttheroute.app.ui.screens.settings

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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trusttheroute.app.ui.theme.*
import com.trusttheroute.app.ui.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeScreen(
    onBackClick: () -> Unit,
    viewModel: ThemeViewModel = hiltViewModel()
) {
    val isDarkThemeEnabled by viewModel.isDarkThemeEnabled.collectAsState()
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
                        "Тема приложения",
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
                modifier = Modifier.border(
                    width = 1.dp,
                    color = if (isDarkTheme) DarkBorder else BorderLight,
                    shape = RoundedCornerShape(0.dp)
                )
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
                    ThemeOptionCard(
                        title = "Темная тема",
                        isEnabled = isDarkThemeEnabled,
                        onToggle = { 
                            viewModel.toggleDarkTheme(!isDarkThemeEnabled)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeOptionCard(
    title: String,
    isEnabled: Boolean,
    onToggle: () -> Unit
) {
    val isDarkTheme = isDarkTheme()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = if (isDarkTheme) DarkBorder else BorderLight,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) DarkSurface else White
        ),
        elevation = if (!isDarkTheme) CardDefaults.cardElevation(defaultElevation = 2.dp) else CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isDarkTheme) DarkOnSurfaceSecondary else LightOnSurface
            )
            
            Switch(
                checked = isEnabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = if (isDarkTheme) Blue400 else BluePrimary,
                    checkedTrackColor = if (isDarkTheme) Blue900.copy(alpha = 0.4f) else BluePrimary.copy(alpha = 0.5f),
                    uncheckedThumbColor = if (isDarkTheme) DarkOnSurfacePlaceholder else Color(0xFFE2E8F0),
                    uncheckedTrackColor = if (isDarkTheme) DarkBorderHover else Color(0xFFCBD5E1)
                )
            )
        }
    }
}
