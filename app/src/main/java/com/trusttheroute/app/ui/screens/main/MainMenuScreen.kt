package com.trusttheroute.app.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trusttheroute.app.ui.theme.*

@Composable
fun MainMenuScreen(
    onRoutesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        CyanAccent.copy(alpha = 0.1f),
                        LightBackground
                    )
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        
        // Заголовок
        Text(
            text = "Trust The Route",
            style = MaterialTheme.typography.displayMedium,
            color = BluePrimary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Кнопка "Маршруты"
        MenuButton(
            text = "Маршруты",
            icon = Icons.Default.Map,
            gradient = Brush.horizontalGradient(
                colors = listOf(CyanAccent, BluePrimary)
            ),
            onClick = onRoutesClick
        )

        // Кнопка "Настройки"
        MenuButton(
            text = "Настройки",
            icon = Icons.Default.Settings,
            gradient = Brush.horizontalGradient(
                colors = listOf(CyanAccent, IndigoAccent)
            ),
            onClick = onSettingsClick
        )

        // Кнопка "О нас"
        MenuButton(
            text = "О нас",
            icon = Icons.Default.Info,
            gradient = Brush.horizontalGradient(
                colors = listOf(BluePrimary, IndigoAccent)
            ),
            onClick = onAboutClick
        )
    }
}

@Composable
fun MenuButton(
    text: String,
    icon: ImageVector,
    gradient: Brush,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(gradient)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = White,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge,
                    color = White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
