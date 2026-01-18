package com.trusttheroute.app.ui.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.layout.onPlaced
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
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

    Box(modifier = Modifier.fillMaxSize()) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = false, // Отключаем свайп, чтобы карту можно было перемещать
            scrimColor = androidx.compose.ui.graphics.Color.Transparent, // Отключаем стандартный scrim
            drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp)
            ) {
                DrawerHeader()
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.DirectionsBus, contentDescription = null) },
                    label = { Text("Маршруты") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToRoutes()
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Главное меню") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToMainMenu()
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Настройки") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToSettings()
                    }
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
                        }
                    ) {
                        Icon(Icons.Default.Menu, "Меню", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BluePrimary,
                    titleContentColor = White,
                    navigationIconContentColor = White
                ),
                modifier = Modifier
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
fun DrawerHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(BluePrimary),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Trust The Route",
                style = MaterialTheme.typography.headlineMedium,
                color = White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Навигация",
                style = MaterialTheme.typography.bodyMedium,
                color = White.copy(alpha = 0.8f)
            )
        }
    }
}
