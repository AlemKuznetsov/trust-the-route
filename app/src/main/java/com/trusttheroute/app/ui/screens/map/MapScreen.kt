package com.trusttheroute.app.ui.screens.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
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
    val context = LocalContext.current
    var centerOnLocationTrigger by remember { mutableStateOf(0) }
    var shouldCenterOnLocation by remember { mutableStateOf(false) }
    var hasCenteredOnFirstLocation by remember { mutableStateOf(false) } // Флаг для отслеживания первого центрирования
    
    // Отслеживаем состояние drawer для закрытия при клике на карту
    val isDrawerOpen by remember {
        derivedStateOf { drawerState.currentValue == DrawerValue.Open }
    }

    // Проверка разрешения на геолокацию (обновляется динамически)
    val hasLocationPermission by remember {
        derivedStateOf {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    // Отслеживаем изменения разрешения для обновления UI
    LaunchedEffect(hasLocationPermission) {
        android.util.Log.d("MapScreen", "hasLocationPermission изменилось: $hasLocationPermission")
        if (hasLocationPermission) {
            viewModel.startLocationTracking()
        }
    }

    // Launcher для запроса разрешения на геолокацию
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        
        android.util.Log.d("MapScreen", "Результат запроса разрешения: fine=$fineLocationGranted, coarse=$coarseLocationGranted")
        
        if (fineLocationGranted || coarseLocationGranted) {
            // Разрешение получено (может быть "While using the app" или "Only this time")
            android.util.Log.d("MapScreen", "Разрешение получено, запускаем отслеживание местоположения")
            viewModel.startLocationTracking()
            
            // Пытаемся получить местоположение немедленно и центрировать карту
            scope.launch {
                try {
                    val currentLoc = viewModel.getCurrentLocation()
                    if (currentLoc != null) {
                        android.util.Log.d("MapScreen", "Получено местоположение после выдачи разрешения, центрируем карту")
                        centerOnLocationTrigger++
                    } else {
                        shouldCenterOnLocation = true
                    }
                } catch (e: Exception) {
                    android.util.Log.e("MapScreen", "Ошибка при получении местоположения после выдачи разрешения", e)
                }
            }
        } else {
            // Разрешение отклонено ("Don't allow")
            android.util.Log.d("MapScreen", "Разрешение отклонено пользователем")
            // Не показываем сообщение - кнопка все равно доступна для повторного запроса
        }
    }

    // Загрузка маршрута при первом запуске
    LaunchedEffect(routeId) {
        if (routeId.isNotEmpty()) {
            viewModel.loadRoute(routeId)
            
            // При первой загрузке маршрута всегда запрашиваем разрешение автоматически
            if (!hasLocationPermission) {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            } else {
                // Если разрешение уже есть, запускаем отслеживание местоположения
                viewModel.startLocationTracking()
            }
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

    // Автоматическое центрирование карты при первом получении местоположения
    // Используем ключ для отслеживания первого получения местоположения
    // LaunchedEffect срабатывает только когда currentLocation меняется с null на не-null
    var locationKey by remember { mutableStateOf<android.location.Location?>(null) }
    
    LaunchedEffect(uiState.currentLocation) {
        val currentLoc = uiState.currentLocation
        // Проверяем, это первое получение местоположения (было null, стало не null)
        if (currentLoc != null && locationKey == null && !hasCenteredOnFirstLocation) {
                // Первое получение местоположения - автоматически центрируем карту
            android.util.Log.d("MapScreen", "Первое получение местоположения после открытия карты, автоматически центрируем карту")
                centerOnLocationTrigger++
                hasCenteredOnFirstLocation = true
            locationKey = currentLoc
        } else if (currentLoc != null && shouldCenterOnLocation) {
                // Центрирование по нажатию кнопки
                android.util.Log.d("MapScreen", "Получено местоположение после нажатия кнопки, центрируем карту")
                centerOnLocationTrigger++
                shouldCenterOnLocation = false
            locationKey = currentLoc
        } else if (currentLoc != null) {
            // При обновлении местоположения (каждые 2 секунды) НЕ центрируем карту автоматически
            // Просто обновляем ключ, но НЕ центрируем
            locationKey = currentLoc
            android.util.Log.d("MapScreen", "Местоположение обновлено, но центрирование НЕ выполняется (hasCenteredOnFirstLocation=$hasCenteredOnFirstLocation, shouldCenterOnLocation=$shouldCenterOnLocation)")
        } else {
            // Если местоположение стало null, сбрасываем ключ
            locationKey = null
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
                        onMapClick = null, // Убираем обработчик клика на карте, используем только кликабельный слой
                        centerOnLocationTrigger = centerOnLocationTrigger
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

                // Кнопка "Моё местоположение"
                // Отображаем кнопку только когда карточка достопримечательности не видна
                if (uiState.nearbyAttraction == null) {
                FloatingActionButton(
                    onClick = {
                        android.util.Log.d("MapScreen", "Кнопка 'Моё местоположение' нажата")
                        
                        // Проверяем разрешение динамически
                        val currentHasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                        
                        if (currentHasPermission) {
                            // Если разрешение есть, определяем местоположение
                            android.util.Log.d("MapScreen", "Разрешение есть, определяем местоположение")
                            scope.launch {
                                try {
                                    // Запускаем отслеживание местоположения, если еще не запущено
                                    viewModel.startLocationTracking()
                                    
                                    // Пытаемся получить текущее местоположение немедленно
                                    val currentLoc = viewModel.getCurrentLocation()
                                    
                                    if (currentLoc != null) {
                                        android.util.Log.d("MapScreen", "Получено текущее местоположение: lat=${currentLoc.latitude}, lng=${currentLoc.longitude}")
                                        // Центрируем карту на текущем местоположении
                                        centerOnLocationTrigger++
                                    } else {
                                        android.util.Log.d("MapScreen", "Текущее местоположение недоступно, ждем обновления...")
                                        // Устанавливаем флаг, чтобы центрировать карту при получении местоположения
                                        shouldCenterOnLocation = true
                                    }
                                } catch (e: Exception) {
                                    android.util.Log.e("MapScreen", "Ошибка при обработке нажатия кнопки местоположения", e)
                                }
                            }
                        } else {
                            // Если разрешения нет, запрашиваем его
                            android.util.Log.d("MapScreen", "Разрешения нет, запрашиваем")
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .align(androidx.compose.ui.Alignment.BottomEnd)
                            .padding(bottom = 24.dp, end = 16.dp), // Поднята выше на 8dp
                    containerColor = if (isDarkTheme) DarkSurface else BluePrimary,
                    contentColor = if (isDarkTheme) Blue400 else White
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Моё местоположение",
                        tint = if (isDarkTheme) Blue400 else White
                    )
                    }
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
