package com.trusttheroute.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trusttheroute.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppInfoScreen(
    onBackClick: () -> Unit
) {
    val isDarkTheme = isDarkTheme()
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { 
                Text(
                    "Информация о приложении",
                    color = if (isDarkTheme) Blue400 else White
                ) 
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.Default.ArrowBack, 
                        "Назад",
                        tint = if (isDarkTheme) Blue400 else White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = if (isDarkTheme) DarkSurface else BluePrimary,
                titleContentColor = if (isDarkTheme) Blue400 else White,
                navigationIconContentColor = if (isDarkTheme) Blue400 else White
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
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDarkTheme) DarkBackground else LightBackground)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Версия
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
                        text = "Версия",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDarkTheme) DarkOnSurfaceSecondary else LightOnSurfaceVariant
                    )
                    Text(
                        text = "1.0.0",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = if (isDarkTheme) DarkOnSurface else LightOnSurface
                    )
                }
            }
            
            // Разработчик
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
                        text = "Разработчик",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDarkTheme) DarkOnSurfaceSecondary else LightOnSurfaceVariant
                    )
                    Text(
                        text = "Trust The Route Team",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = if (isDarkTheme) DarkOnSurface else LightOnSurface
                    )
                }
            }
        }
    }
}
