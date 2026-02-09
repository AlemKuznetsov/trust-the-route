package com.trusttheroute.app.ui.screens.review

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trusttheroute.app.ui.theme.*
import com.trusttheroute.app.ui.viewmodel.RouteReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteReviewScreen(
    routeId: String,
    routeName: String = "Маршрут",
    visitedAttractions: List<String> = emptyList(),
    onBackClick: () -> Unit,
    onReviewSubmitted: () -> Unit,
    viewModel: RouteReviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDarkTheme = isDarkTheme()
    
    // Загружаем данные маршрута при первом запуске
    LaunchedEffect(routeId) {
        // TODO: Загрузить имя маршрута из RouteRepository если нужно
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
    
    // Обработка успешной отправки
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onReviewSubmitted()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Завершение экскурсии",
                        color = if (isDarkTheme) Blue400 else BluePrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            "Назад",
                            tint = if (isDarkTheme) Blue400 else BluePrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDarkTheme) DarkSurface else LightSurface
                )
            )
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Иконка завершения
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = if (isDarkTheme) Color(0xFF4ADE80) else Color(0xFF16A34A)
                )
                
                Text(
                    text = "Экскурсия завершена!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (isDarkTheme) Blue400 else BluePrimary,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = routeName,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isDarkTheme) DarkOnSurfaceSecondary else LightOnSurfaceVariant
                )
                
                Text(
                    text = "Посещено достопримечательностей: ${visitedAttractions.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                )
                
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = if (isDarkTheme) DarkSurfaceVariant else LightSurfaceVariant
                )
                
                // Оценка
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) DarkSurface else White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Оцените экскурсию",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isDarkTheme) Blue400 else BluePrimary,
                            fontWeight = FontWeight.Bold
                        )
                        
                        // Звезды для оценки
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (i in 1..5) {
                                IconButton(
                                    onClick = { viewModel.setRating(i) },
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(
                                        imageVector = if (i <= uiState.rating) {
                                            Icons.Default.Star
                                        } else {
                                            Icons.Default.StarBorder
                                        },
                                        contentDescription = "$i звезд",
                                        modifier = Modifier.size(40.dp),
                                        tint = if (i <= uiState.rating) {
                                            Color(0xFFFFD700) // Золотой цвет для выбранных звезд
                                        } else {
                                            if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                                        }
                                    )
                                }
                            }
                        }
                        
                        if (uiState.rating > 0) {
                            Text(
                                text = when (uiState.rating) {
                                    1 -> "Плохо"
                                    2 -> "Не очень"
                                    3 -> "Нормально"
                                    4 -> "Хорошо"
                                    5 -> "Отлично"
                                    else -> ""
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isDarkTheme) DarkOnSurfaceSecondary else LightOnSurfaceVariant
                            )
                        }
                    }
                }
                
                // Отзыв
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) DarkSurface else White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Оставьте отзыв (необязательно)",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isDarkTheme) Blue400 else BluePrimary,
                            fontWeight = FontWeight.Medium
                        )
                        
                        OutlinedTextField(
                            value = uiState.review,
                            onValueChange = { viewModel.setReview(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            placeholder = {
                                Text(
                                    "Поделитесь своими впечатлениями об экскурсии...",
                                    color = if (isDarkTheme) DarkOnSurfacePlaceholder else LightOnSurfacePlaceholder
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = if (isDarkTheme) Blue400 else BluePrimary,
                                unfocusedTextColor = if (isDarkTheme) DarkOnSurfaceSecondary else LightOnSurfaceVariant,
                                focusedBorderColor = if (isDarkTheme) Blue500 else BluePrimary,
                                unfocusedBorderColor = if (isDarkTheme) DarkBorder else BorderLight
                            ),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 5
                        )
                    }
                }
                
                // Ошибка
                if (uiState.error != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDarkTheme) Color(0xFF7F1D1D) else Color(0xFFFEE2E2)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = if (isDarkTheme) Color(0xFFF87171) else Color(0xFFDC2626)
                            )
                            Text(
                                text = uiState.error!!,
                                color = if (isDarkTheme) Color(0xFFF87171) else Color(0xFFDC2626),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                
                // Кнопка отправки
                Button(
                    onClick = {
                        viewModel.submitReview(routeId, visitedAttractions)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !uiState.isLoading && uiState.rating > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDarkTheme) Blue500 else BluePrimary
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = White
                        )
                    } else {
                        Text(
                            text = "Отправить отзыв",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    }
                }
            }
        }
    }
}
