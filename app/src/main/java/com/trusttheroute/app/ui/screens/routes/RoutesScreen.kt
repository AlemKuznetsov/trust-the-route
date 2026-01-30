package com.trusttheroute.app.ui.screens.routes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trusttheroute.app.ui.theme.*
import com.trusttheroute.app.ui.viewmodel.RoutesViewModel

@Composable
fun RoutesScreen(
    onBackClick: () -> Unit,
    onRouteSelected: (String) -> Unit,
    viewModel: RoutesViewModel = hiltViewModel()
) {
    val routes by viewModel.routes.collectAsState(initial = emptyList())
    val searchQuery by viewModel.searchQuery.collectAsState()
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
                    "Выберите маршрут",
                    color = headerTextColor
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
                containerColor = headerColor,
                titleContentColor = headerTextColor,
                navigationIconContentColor = headerTextColor
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

        // Поиск
        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .then(
                    if (isDarkTheme) {
                        Modifier.background(
                            color = DarkSurface,
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else {
                        Modifier
                    }
                ),
            placeholder = { 
                Text(
                    "Поиск маршрута",
                    color = if (isDarkTheme) DarkOnSurfacePlaceholder else Color.Unspecified
                ) 
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search, 
                    contentDescription = "Поиск",
                    tint = if (isDarkTheme) Blue400 else Color.Unspecified
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isDarkTheme) Blue500 else BluePrimary,
                unfocusedBorderColor = if (isDarkTheme) DarkBorder else BorderMedium,
                focusedTextColor = if (isDarkTheme) DarkOnSurfaceVariant else Color.Unspecified,
                unfocusedTextColor = if (isDarkTheme) DarkOnSurfaceVariant else Color.Unspecified
            )
        )

        // Список маршрутов
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(routes) { route ->
                RouteCard(
                    route = route,
                    onClick = { onRouteSelected(route.id) }
                )
            }
        }
    }
}

@Composable
fun RouteCard(
    route: com.trusttheroute.app.domain.model.Route,
    onClick: () -> Unit
) {
    val isDarkTheme = isDarkTheme()
    val cardColor = if (isDarkTheme) DarkSurface else White
    val borderColor = if (isDarkTheme) DarkBorderHover else BorderLight
    val hoverBorderColor = if (isDarkTheme) Blue500 else BorderMedium
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .then(
                if (isDarkTheme) {
                    Modifier.border(
                        width = 2.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(16.dp)
                    )
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        border = if (!isDarkTheme) androidx.compose.foundation.BorderStroke(1.dp, BorderLight) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Иконка автобуса
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Blue500, Blue600)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsBus,
                    contentDescription = "Автобус",
                    tint = White,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Информация о маршруте
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Маршрут №${route.number}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Blue400 else LightOnSurface
                )
                Text(
                    text = route.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDarkTheme) DarkOnSurfacePlaceholder else LightOnSurfaceVariant
                )
            }
            
            // Стрелка
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = if (isDarkTheme) Blue500 else Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
