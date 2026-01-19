package com.trusttheroute.app.ui.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.layout.onPlaced
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import com.trusttheroute.app.ui.components.AttractionCard
import com.trusttheroute.app.ui.components.YandexMapView
import com.trusttheroute.app.ui.navigation.Screen
import com.trusttheroute.app.ui.theme.*
import com.trusttheroute.app.ui.viewmodel.MapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    routeId: String,
    onBackClick: () -> Unit,
    onNavigateToRoutes: () -> Unit,
    onNavigateToMainMenu: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Отслеживаем состояние drawer для закрытия при клике на карту
    val isDrawerOpen by remember {
        derivedStateOf { drawerState.currentValue == DrawerValue.Open }
    }

    // Загрузка маршрута при первом запуске
    LaunchedEffect(routeId) {
        if (routeId.isNotEmpty()) {
            viewModel.loadRoute(routeId)
            viewModel.startLocationTracking()
        }
    }

    // Обработка ошибок
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
        }
    }

    val isDarkTheme = isDarkTheme()
    val overlayColor = if (isDarkTheme) DarkOverlayStrong else androidx.compose.ui.graphics.Color.Transparent
    
    Box(modifier = Modifier.fillMaxSize()) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = false, // Отключаем свайп, чтобы карту можно было перемещать
            scrimColor = overlayColor, // Затемнение для темной темы
            drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(288.dp)
                    .then(
                        if (isDarkTheme) {
                            Modifier.border(
                                width = 1.dp,
                                color = DarkBorder,
                                shape = RoundedCornerShape(0.dp)
                            )
                        } else {
                            Modifier
                        }
                    ),
                drawerContainerColor = if (isDarkTheme) DarkSurface else MaterialTheme.colorScheme.surface,
                drawerContentColor = if (isDarkTheme) DarkOnSurfaceVariant else MaterialTheme.colorScheme.onSurface
            ) {
                DrawerHeader(isDarkTheme)
                NavigationDrawerItem(
                    icon = { 
                        Icon(
                            Icons.Default.DirectionsBus, 
                            contentDescription = null,
                            tint = if (isDarkTheme) DarkOnSurfaceSecondary else Color.Unspecified
                        ) 
                    },
                    label = { 
                        Text(
                            "Маршруты",
                            color = if (isDarkTheme) DarkOnSurfaceSecondary else Color.Unspecified
                        ) 
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToRoutes()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = if (isDarkTheme) DarkSurfaceVariant else Color.Unspecified
                    )
                )
                NavigationDrawerItem(
                    icon = { 
                        Icon(
                            Icons.Default.Home, 
                            contentDescription = null,
                            tint = if (isDarkTheme) DarkOnSurfaceSecondary else Color.Unspecified
                        ) 
                    },
                    label = { 
                        Text(
                            "Главное меню",
                            color = if (isDarkTheme) DarkOnSurfaceSecondary else Color.Unspecified
                        ) 
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToMainMenu()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = if (isDarkTheme) DarkSurfaceVariant else Color.Unspecified
                    )
                )
                NavigationDrawerItem(
                    icon = { 
                        Icon(
                            Icons.Default.Settings, 
                            contentDescription = null,
                            tint = if (isDarkTheme) DarkOnSurfaceSecondary else Color.Unspecified
                        ) 
                    },
                    label = { 
                        Text(
                            "Настройки",
                            color = if (isDarkTheme) DarkOnSurfaceSecondary else Color.Unspecified
                        ) 
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToSettings()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = if (isDarkTheme) DarkSurfaceVariant else Color.Unspecified
                    )
                )
            }
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Верхняя панель
                TopAppBar(
                title = {
                    Text(
                        text = uiState.route?.let { "Маршрут №${it.number}" } ?: "Карта маршрута",
                        color = if (isDarkTheme) Blue400 else White,
                        modifier = Modifier
                            .then(
                                if (isDrawerOpen) {
                                    Modifier.clickable { scope.launch { drawerState.close() } }
                                } else {
                                    Modifier
                                }
                            )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { 
                            if (isDrawerOpen) {
                                scope.launch { drawerState.close() }
                            } else {
                                scope.launch { drawerState.open() }
                            }
                        },
                        modifier = if (isDarkTheme) {
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .then(
                                    Modifier.clickable {
                                        if (isDrawerOpen) {
                                            scope.launch { drawerState.close() }
                                        } else {
                                            scope.launch { drawerState.open() }
                                        }
                                    }
                                )
                        } else {
                            Modifier
                        }
                    ) {
                        Icon(
                            Icons.Default.Menu, 
                            "Меню", 
                            tint = if (isDarkTheme) Blue400 else White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDarkTheme) DarkSurface else BluePrimary,
                    titleContentColor = if (isDarkTheme) Blue400 else White,
                    navigationIconContentColor = if (isDarkTheme) Blue400 else White
                ),
                modifier = Modifier
                    .then(
                        if (isDarkTheme) {
                            Modifier.border(
                                width = 1.dp,
                                color = DarkBorder,
                                shape = RoundedCornerShape(0.dp)
                            )
                        } else {
                            Modifier
                        }
                    )
                    .then(
                        if (isDrawerOpen) {
                            Modifier.clickable { scope.launch { drawerState.close() } }
                        } else {
                            Modifier
                        }
                    )
            )

            // Карта
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                if (uiState.route != null) {
                    android.util.Log.d("MapScreen", "Rendering map with ${uiState.attractions.size} attractions")
                    YandexMapView(
                        route = uiState.route,
                        attractions = uiState.attractions,
                        currentLocation = uiState.currentLocation,
                        onAttractionClick = { attraction ->
                            viewModel.selectAttraction(attraction)
                        },
                        modifier = Modifier.fillMaxSize(),
                        isDrawerOpen = isDrawerOpen,
                        onMapClick = null // Убираем обработчик клика на карте, используем только кликабельный слой
                    )
                } else if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                // Карточка достопримечательности
                uiState.nearbyAttraction?.let { attraction ->
                    AttractionCard(
                        attraction = attraction,
                        isVisible = true,
                        isAudioPlaying = uiState.isAudioPlaying,
                        onDismiss = {
                            viewModel.dismissAttractionCard()
                        },
                        onPlayAudio = {
                            viewModel.playAttractionAudio(attraction)
                        },
                        onPauseAudio = {
                            viewModel.pauseAudio()
                        },
                        onRestartAudio = {
                            viewModel.restartAudio()
                        },
                        modifier = Modifier
                            .align(androidx.compose.ui.Alignment.BottomCenter)
                            .fillMaxWidth()
                    )
                }

                // Snackbar для отображения ошибок
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(androidx.compose.ui.Alignment.BottomCenter)
                )
            }
            }
        }
    }
        
        // Прозрачный кликабельный слой справа от меню для закрытия drawer
        // Размещаем его на уровне Box после ModalNavigationDrawer, чтобы он был выше в z-order
        if (isDrawerOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 280.dp) // Начинаем справа от меню
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            android.util.Log.d("MapScreen", "DEBUG: Overlay tap detected via pointerInput at offset: $offset, drawer state: ${drawerState.currentValue}, isOpen: ${drawerState.isOpen}")
                            scope.launch { 
                                try {
                                    if (drawerState.isOpen) {
                                        android.util.Log.d("MapScreen", "DEBUG: Closing drawer, current state: ${drawerState.currentValue}")
                                        drawerState.close()
                                        android.util.Log.d("MapScreen", "DEBUG: Drawer close() called successfully, new state: ${drawerState.currentValue}")
                                    } else {
                                        android.util.Log.d("MapScreen", "DEBUG: Drawer already closed, skipping close()")
                                    }
                                } catch (e: Exception) {
                                    android.util.Log.e("MapScreen", "DEBUG: Error closing drawer", e)
                                }
                            }
                        }
                    }
            )
        }
    }
}

@Composable
fun DrawerHeader(isDarkTheme: Boolean = false) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(if (isDarkTheme) DarkSurface else BluePrimary),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Trust The Route",
                style = MaterialTheme.typography.headlineMedium,
                color = if (isDarkTheme) Blue400 else White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Навигация",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDarkTheme) DarkOnSurfacePlaceholder else White.copy(alpha = 0.8f)
            )
        }
    }
}
