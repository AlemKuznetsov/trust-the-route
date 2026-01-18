package com.trusttheroute.app.ui.screens.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.trusttheroute.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { 
                Text(
                    "О нас",
                    color = White
                ) 
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.Default.ArrowBack, 
                        "Назад",
                        tint = White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = CyanAccent
            )
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(LightBackground)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Основная карточка с описанием
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Trust The Route",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = BluePrimary
                    )
                    
                    Text(
                        text = "Trust The Route — это инновационное приложение для изучения городских маршрутов и знакомства с достопримечательностями во время поездок на общественном транспорте.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LightOnSurface,
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                    )
                    
                    Text(
                        text = "Мы создаем уникальный опыт городских путешествий, превращая обычные поездки в увлекательные экскурсии с аудиогидами и интерактивными картами.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LightOnSurface,
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                    )
                }
            }
            
            // Карточка "Наша миссия"
            InfoCard(
                title = "Наша миссия",
                description = "Помочь людям открыть свой город заново и узнать больше о культурном наследии.",
                icon = Icons.Default.LocationOn,
                gradient = Brush.horizontalGradient(
                    colors = listOf(BluePrimary, CyanAccent)
                )
            )
            
            // Карточка "Наши ценности"
            InfoCard(
                title = "Наши ценности",
                description = "Доступность, качество информации и удобство использования для всех.",
                icon = Icons.Default.Favorite,
                gradient = Brush.horizontalGradient(
                    colors = listOf(CyanAccent, CyanAccent.copy(alpha = 0.8f))
                )
            )
            
            // Карточка "Команда"
            InfoCard(
                title = "Команда",
                description = "Команда энтузиастов, увлеченных историей и технологиями.",
                icon = Icons.Default.People,
                gradient = Brush.horizontalGradient(
                    colors = listOf(IndigoAccent, BluePrimary)
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Футер
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "© 2026 Trust The Route",
                    style = MaterialTheme.typography.bodySmall,
                    color = LightOnSurfaceVariant
                )
                Text(
                    text = "Версия 1.0.1",
                    style = MaterialTheme.typography.bodySmall,
                    color = LightOnSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradient: Brush
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient)
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(32.dp)
                )
                
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = White
                    )
                }
            }
        }
    }
}
