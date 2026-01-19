package com.trusttheroute.app.ui.screens.routes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trusttheroute.app.ui.theme.*
import com.trusttheroute.app.ui.viewmodel.RouteDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailsScreen(
    routeId: String,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    viewModel: RouteDetailsViewModel = hiltViewModel()
) {
    val route by viewModel.route.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(routeId) {
        viewModel.loadRoute(routeId)
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (route == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Маршрут не найден")
        }
        return
    }

    val currentRoute = route!!
    val isDarkTheme = isDarkTheme()
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground
    val headerColor = if (isDarkTheme) DarkSurface else BluePrimary
    val headerTextColor = if (isDarkTheme) Blue400 else White
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Верхняя панель
        TopAppBar(
            title = { 
                Text(
                    "Маршрут ${currentRoute.number}",
                    color = headerTextColor
                ) 
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.Default.ArrowBack, 
                        "Назад",
                        tint = headerTextColor
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = headerColor,
                titleContentColor = headerTextColor,
                navigationIconContentColor = headerTextColor
            ),
            modifier = if (isDarkTheme) Modifier.border(
                width = 1.dp,
                color = DarkBorder,
                shape = RoundedCornerShape(0.dp)
            ) else Modifier
        )

        // Контент с прокруткой
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Название маршрута
            Text(
                text = currentRoute.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Blue400 else LightOnSurface,
                modifier = Modifier.fillMaxWidth()
            )

            // Описание маршрута
            if (currentRoute.description.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) DarkSurface else White
                    ),
                    border = if (isDarkTheme) androidx.compose.foundation.BorderStroke(1.dp, DarkBorder) else null,
                    elevation = if (!isDarkTheme) CardDefaults.cardElevation(defaultElevation = 2.dp) else CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Описание маршрута",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Blue400 else LightOnSurface
                        )
                        Text(
                            text = currentRoute.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceSecondary else LightOnSurfaceVariant
                        )
                    }
                }
            }

            // История маршрута
            if (currentRoute.history.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) DarkSurface else White
                    ),
                    border = if (isDarkTheme) androidx.compose.foundation.BorderStroke(1.dp, DarkBorder) else null,
                    elevation = if (!isDarkTheme) CardDefaults.cardElevation(defaultElevation = 2.dp) else CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "История маршрута",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = LightOnSurface
                        )
                        Text(
                            text = currentRoute.history,
                            style = MaterialTheme.typography.bodyMedium,
                                color = if (isDarkTheme) DarkOnSurfacePlaceholder else LightOnSurfaceVariant
                        )
                    }
                }
            }

            // Описание достопримечательностей
            if (currentRoute.attractionsDescription.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) DarkSurface else White
                    ),
                    border = if (isDarkTheme) androidx.compose.foundation.BorderStroke(1.dp, DarkBorder) else null,
                    elevation = if (!isDarkTheme) CardDefaults.cardElevation(defaultElevation = 2.dp) else CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Описание достопримечательностей",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = LightOnSurface
                        )
                        Text(
                            text = currentRoute.attractionsDescription,
                            style = MaterialTheme.typography.bodyMedium,
                                color = if (isDarkTheme) DarkOnSurfacePlaceholder else LightOnSurfaceVariant
                        )
                    }
                }
            }

            // Информация о маршруте
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkTheme) DarkSurface else White
                ),
                border = if (isDarkTheme) androidx.compose.foundation.BorderStroke(1.dp, DarkBorder) else null,
                elevation = if (!isDarkTheme) CardDefaults.cardElevation(defaultElevation = 2.dp) else CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Информация о маршруте",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = LightOnSurface
                    )

                    // Продолжительность поездки
                    if (currentRoute.duration.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = BluePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Продолжительность поездки",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                                )
                                Text(
                                    text = currentRoute.duration,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurface
                                )
                            }
                        }
                    }

                    // Интервал автобуса
                    if (currentRoute.interval.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = BluePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Интервал автобуса",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                                )
                                Text(
                                    text = currentRoute.interval,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurface
                                )
                            }
                        }
                    }

                    // Начало экскурсии
                    if (currentRoute.startPoint.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = BluePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Начало экскурсии",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                                )
                                Text(
                                    text = currentRoute.startPoint,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurface
                                )
                            }
                        }
                    }
                }
            }

            // Список остановок
            if (currentRoute.stops.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) DarkSurface else White
                    ),
                    border = if (isDarkTheme) androidx.compose.foundation.BorderStroke(1.dp, DarkBorder) else null,
                    elevation = if (!isDarkTheme) CardDefaults.cardElevation(defaultElevation = 2.dp) else CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Остановки",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = LightOnSurface
                        )
                        currentRoute.stops.forEachIndexed { index, stop ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "${index + 1}.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkTheme) Blue400 else BluePrimary
                                )
                                Text(
                                    text = stop,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Кнопки внизу (зафиксированы)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = backgroundColor,
            shadowElevation = if (!isDarkTheme) 8.dp else 0.dp,
            border = if (isDarkTheme) androidx.compose.foundation.BorderStroke(1.dp, DarkBorder) else null
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
            // Кнопка "Назад"
            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Назад")
            }

            // Кнопка "Продолжить"
            Button(
                onClick = onContinueClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Продолжить")
            }
            }
        }
    }
}
