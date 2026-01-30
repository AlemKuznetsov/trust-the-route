package com.trusttheroute.app.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.PolylineMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import com.trusttheroute.app.R
import com.trusttheroute.app.domain.model.Attraction
import com.trusttheroute.app.domain.model.Route
import android.graphics.PointF

@Composable
fun YandexMapView(
    route: Route?,
    attractions: List<Attraction>,
    currentLocation: android.location.Location?,
    onAttractionClick: ((Attraction) -> Unit)? = null,
    modifier: Modifier = Modifier,
    isDrawerOpen: Boolean = false,
    onMapClick: (() -> Unit)? = null,
    centerOnLocationTrigger: Int = 0
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapView = remember {
        MapView(context).apply {
            map.isRotateGesturesEnabled = true
            map.isZoomGesturesEnabled = true
            map.isScrollGesturesEnabled = true
            map.isTiltGesturesEnabled = false
        }
    }
    
    // Состояние для управления маркерами отдельно
    var attractionMarkersRef by remember { mutableStateOf<List<PlacemarkMapObject>>(emptyList()) }
    var routePolylineRef by remember { mutableStateOf<PolylineMapObject?>(null) }
    var locationMarkerRef by remember { mutableStateOf<PlacemarkMapObject?>(null) }
    var hasCenteredOnRoute by remember { mutableStateOf(false) }
    
    // Отключаем жесты карты когда drawer открыт
    LaunchedEffect(isDrawerOpen) {
        mapView.map.isRotateGesturesEnabled = !isDrawerOpen
        mapView.map.isZoomGesturesEnabled = !isDrawerOpen
        mapView.map.isScrollGesturesEnabled = !isDrawerOpen
        android.util.Log.d("YandexMapView", "Gestures enabled: ${!isDrawerOpen}, drawer open: $isDrawerOpen")
    }
    
    // Добавляем обработчик клика на карту для закрытия drawer через InputListener
    val mapTapListener = remember(isDrawerOpen, onMapClick) {
        if (isDrawerOpen && onMapClick != null) {
            object : InputListener {
                override fun onMapTap(map: com.yandex.mapkit.map.Map, point: Point) {
                    android.util.Log.d("YandexMapView", "Map tapped at point: $point, closing drawer")
                    onMapClick()
                }
                
                override fun onMapLongTap(map: com.yandex.mapkit.map.Map, point: Point) {
                    android.util.Log.d("YandexMapView", "Map long tapped, closing drawer")
                    onMapClick()
                }
            }
        } else {
            null
        }
    }
    
    // Управление обработчиком клика
    DisposableEffect(mapTapListener) {
        if (mapTapListener != null) {
            try {
                mapView.map.addInputListener(mapTapListener)
                android.util.Log.d("YandexMapView", "Map tap listener added for drawer close")
            } catch (e: Exception) {
                android.util.Log.e("YandexMapView", "Error adding map tap listener", e)
            }
        }
        
        onDispose {
            if (mapTapListener != null) {
                try {
                    mapView.map.removeInputListener(mapTapListener)
                    android.util.Log.d("YandexMapView", "Map tap listener removed")
                } catch (e: Exception) {
                    android.util.Log.e("YandexMapView", "Error removing map tap listener", e)
                }
            }
        }
    }

    // Создаем и сохраняем listener один раз
    val tapListener = remember(onAttractionClick) {
        if (onAttractionClick != null) {
            object : MapObjectTapListener {
                override fun onMapObjectTap(mapObject: MapObject, point: Point): Boolean {
                    val attraction = mapObject.userData as? Attraction
                    android.util.Log.d("YandexMapView", "MapObject tapped, userData: $attraction")
                    if (attraction != null) {
                        android.util.Log.d("YandexMapView", "Calling onAttractionClick for: ${attraction.name}")
                        onAttractionClick(attraction)
                        return true
                    }
                    return false
                }
            }
        } else {
            null
        }
    }

    // Добавляем/удаляем listener при изменении
    DisposableEffect(tapListener) {
        try {
            if (tapListener != null) {
                mapView.map.mapObjects.addTapListener(tapListener)
                android.util.Log.d("YandexMapView", "Tap listener added successfully")
            }
        } catch (e: Exception) {
            android.util.Log.e("YandexMapView", "Error adding tap listener", e)
        }
        
        onDispose {
            try {
                if (tapListener != null) {
                    mapView.map.mapObjects.removeTapListener(tapListener)
                    android.util.Log.d("YandexMapView", "Tap listener removed")
                }
            } catch (e: Exception) {
                android.util.Log.e("YandexMapView", "Error removing tap listener", e)
            }
        }
    }

    // Обновление маршрута и достопримечательностей
    LaunchedEffect(route, attractions) {
        updateRouteAndAttractions(
            mapView = mapView,
            route = route,
            attractions = attractions,
            context = context,
            attractionMarkersRef = { attractionMarkersRef },
            setAttractionMarkersRef = { attractionMarkersRef = it },
            routePolylineRef = { routePolylineRef },
            setRoutePolylineRef = { routePolylineRef = it },
            hasCenteredOnRoute = { hasCenteredOnRoute },
            setHasCenteredOnRoute = { hasCenteredOnRoute = it }
        )
    }
    
    // Обновление маркера местоположения отдельно
    LaunchedEffect(currentLocation) {
        locationMarkerRef = updateLocationMarker(
            mapView = mapView,
            currentLocation = currentLocation,
            context = context,
            existingMarker = locationMarkerRef
        )
    }
    
    // Центрирование карты на текущем местоположении ТОЛЬКО при изменении centerOnLocationTrigger
    // НЕ зависим от currentLocation, чтобы избежать центрирования при каждом обновлении местоположения
    // Сохраняем последний использованный trigger, чтобы не центрировать повторно с тем же значением
    var lastCenteredTrigger by remember { mutableStateOf(0) }
    
    LaunchedEffect(centerOnLocationTrigger) {
        // Центрируем только если trigger изменился и больше 0, и есть местоположение
        if (centerOnLocationTrigger > 0 && centerOnLocationTrigger != lastCenteredTrigger && currentLocation != null) {
            android.util.Log.d("YandexMapView", "Центрирование карты на местоположении: lat=${currentLocation.latitude}, lng=${currentLocation.longitude}, trigger=$centerOnLocationTrigger")
            val userPoint = Point(currentLocation.latitude, currentLocation.longitude)
            mapView.map.move(
                CameraPosition(userPoint, 15f, 0f, 0f),
                Animation(Animation.Type.SMOOTH, 0.5f),
                null
            )
            lastCenteredTrigger = centerOnLocationTrigger
            android.util.Log.d("YandexMapView", "Карта центрирована успешно, lastCenteredTrigger=$lastCenteredTrigger")
        } else if (centerOnLocationTrigger > 0 && centerOnLocationTrigger != lastCenteredTrigger && currentLocation == null) {
            android.util.Log.w("YandexMapView", "Попытка центрирования карты, но местоположение не определено, trigger=$centerOnLocationTrigger")
        } else if (centerOnLocationTrigger == lastCenteredTrigger) {
            android.util.Log.d("YandexMapView", "Центрирование пропущено: trigger не изменился ($centerOnLocationTrigger == $lastCenteredTrigger)")
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier.fillMaxSize(),
        update = { view ->
            // Отключаем взаимодействие с картой когда drawer открыт
            view.isClickable = !isDrawerOpen
            view.isEnabled = !isDrawerOpen
            view.isFocusable = !isDrawerOpen
            // Отключаем все жесты через MapView
            view.map.isRotateGesturesEnabled = !isDrawerOpen
            view.map.isZoomGesturesEnabled = !isDrawerOpen
            view.map.isScrollGesturesEnabled = !isDrawerOpen
            android.util.Log.d("YandexMapView", "Map interaction disabled: $isDrawerOpen")
        }
    )

    // Управление жизненным циклом карты
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    MapKitFactory.getInstance().onStart()
                    mapView.onStart()
                }
                Lifecycle.Event.ON_STOP -> {
                    mapView.onStop()
                    MapKitFactory.getInstance().onStop()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

private fun updateRouteAndAttractions(
    mapView: MapView,
    route: Route?,
    attractions: List<Attraction>,
    context: android.content.Context,
    attractionMarkersRef: () -> List<PlacemarkMapObject>,
    setAttractionMarkersRef: (List<PlacemarkMapObject>) -> Unit,
    routePolylineRef: () -> PolylineMapObject?,
    setRoutePolylineRef: (PolylineMapObject?) -> Unit,
    hasCenteredOnRoute: () -> Boolean,
    setHasCenteredOnRoute: (Boolean) -> Unit
) {
    val mapObjects = mapView.map.mapObjects

    // Remove old attraction markers
    attractionMarkersRef().forEach { marker ->
        try {
            mapObjects.remove(marker)
        } catch (e: Exception) {
            android.util.Log.e("YandexMapView", "Ошибка при удалении старого маркера достопримечательности", e)
        }
    }
    setAttractionMarkersRef(emptyList())

    // Remove old route polyline
    routePolylineRef()?.let { polyline ->
        try {
            mapObjects.remove(polyline)
        } catch (e: Exception) {
            android.util.Log.e("YandexMapView", "Ошибка при удалении старого маршрута", e)
        }
    }
    setRoutePolylineRef(null)

    // Display route
    route?.let { r ->
        if (r.polyline.isNotEmpty()) {
            try {
                val points = decodePolyline(r.polyline)
                if (points.isNotEmpty()) {
                    val polyline = com.yandex.mapkit.geometry.Polyline(points)
                    val polylineMapObject = mapObjects.addPolyline(polyline)
                    polylineMapObject.setStrokeColor(android.graphics.Color.parseColor("#2196F3"))
                    polylineMapObject.strokeWidth = 5f
                    setRoutePolylineRef(polylineMapObject)

                    // Center on route only once when route is first loaded
                    if (!hasCenteredOnRoute()) {
                        val center = points[points.size / 2]
                        mapView.map.move(
                            CameraPosition(center, 13f, 0f, 0f),
                            Animation(Animation.Type.SMOOTH, 1f),
                            null
                        )
                        setHasCenteredOnRoute(true)
                        android.util.Log.d("YandexMapView", "Карта центрирована на маршруте: lat=${center.latitude}, lng=${center.longitude}")
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("YandexMapView", "Ошибка при отображении маршрута", e)
                e.printStackTrace()
            }
        }
    }

    // Display attractions
    val newMarkers = mutableListOf<PlacemarkMapObject>()
        android.util.Log.d("YandexMapView", "Adding ${attractions.size} attractions to map")
        if (attractions.isEmpty()) {
            android.util.Log.w("YandexMapView", "Список достопримечательностей пуст")
        }
        attractions.forEach { attraction ->
            try {
                val point = Point(attraction.latitude, attraction.longitude)
                val placemark = mapObjects.addPlacemark(point)
                
            // Create Bitmap from vector drawable for better compatibility
            val drawable = context.getDrawable(R.drawable.ic_attraction_marker)
            if (drawable != null) {
                val bitmap = android.graphics.Bitmap.createBitmap(
                    (64 * context.resources.displayMetrics.density).toInt(),
                    (64 * context.resources.displayMetrics.density).toInt(),
                    android.graphics.Bitmap.Config.ARGB_8888
                )
                val canvas = android.graphics.Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                val iconProvider = ImageProvider.fromBitmap(bitmap)
                placemark.setIcon(iconProvider)

                val iconStyle = IconStyle()
                iconStyle.scale = 0.63f // Adjusted scale
                iconStyle.anchor = PointF(0.5f, 1.0f) // Anchor at bottom center
                placemark.setIconStyle(iconStyle)

                android.util.Log.d("YandexMapView", "Золотистый маркер установлен для: ${attraction.name}, placemark visible: ${placemark.isVisible}")
            } else {
                android.util.Log.e("YandexMapView", "Drawable for ic_attraction_marker not found")
                }
                
                placemark.userData = attraction
            android.util.Log.d("YandexMapView", "Added placemark for: ${attraction.name}, userData set: ${placemark.userData != null}, placemark: $placemark")
            newMarkers.add(placemark)
            } catch (e: Exception) {
                android.util.Log.e("YandexMapView", "Error adding placemark for ${attraction.name}", e)
                e.printStackTrace()
            }
        }
    android.util.Log.d("YandexMapView", "Всего добавлено ${newMarkers.size} маркеров достопримечательностей")
    setAttractionMarkersRef(newMarkers)
    android.util.Log.d("YandexMapView", "Проверка: маркеры сохранены в attractionMarkersRef, размер: ${attractionMarkersRef().size}")
    android.util.Log.d("YandexMapView", "attractionMarkersRef обновлен, новый размер: ${attractionMarkersRef().size}")
}

private fun updateLocationMarker(
    mapView: MapView,
    currentLocation: android.location.Location?,
    context: android.content.Context,
    existingMarker: PlacemarkMapObject?
): PlacemarkMapObject? {
    val mapObjects = mapView.map.mapObjects

    // Remove old marker if it exists
    existingMarker?.let {
        try {
            mapObjects.remove(it)
            android.util.Log.d("YandexMapView", "Старый маркер местоположения удален")
    } catch (e: Exception) {
            android.util.Log.e("YandexMapView", "Ошибка при удалении старого маркера местоположения", e)
        }
    }

    // Add new marker if location is available
    return currentLocation?.let { location ->
        try {
            val userPoint = Point(location.latitude, location.longitude)
            val userPlacemark = mapObjects.addPlacemark(userPoint)
            
            // Create Bitmap from vector drawable for better compatibility
            val drawable = context.getDrawable(R.drawable.ic_user_location_marker)
            if (drawable != null) {
                val bitmap = android.graphics.Bitmap.createBitmap(
                    (64 * context.resources.displayMetrics.density).toInt(),
                    (64 * context.resources.displayMetrics.density).toInt(),
                    android.graphics.Bitmap.Config.ARGB_8888
                )
                val canvas = android.graphics.Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                val iconProvider = ImageProvider.fromBitmap(bitmap)
                userPlacemark.setIcon(iconProvider)
                
                val iconStyle = IconStyle()
                iconStyle.scale = 0.63f // Same scale as attraction markers
                iconStyle.anchor = PointF(0.5f, 0.5f) // Center anchor for user location
                userPlacemark.setIconStyle(iconStyle)
                
                android.util.Log.d("YandexMapView", "Маркер местоположения установлен успешно с кастомной иконкой (белый круг с красным кругом внутри)")
            } else {
                android.util.Log.e("YandexMapView", "Drawable for ic_user_location_marker not found")
            }
            
            userPlacemark
        } catch (e: Exception) {
            android.util.Log.e("YandexMapView", "Ошибка при добавлении маркера местоположения", e)
            e.printStackTrace()
            null
        }
    }
}

/**
 * Декодирует encoded polyline в список точек
 * Формат: Google Polyline Encoding Algorithm
 */
private fun decodePolyline(encoded: String): List<Point> {
    val points = mutableListOf<Point>()
    var index = 0
    var lat = 0
    var lng = 0

    while (index < encoded.length) {
        var shift = 0
        var result = 0
        var byte: Int
        do {
            byte = encoded[index++].code - 63
            result = result or (byte and 0x1F shl shift)
            shift += 5
        } while (byte >= 0x20)
        val deltaLat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lat += deltaLat

        shift = 0
        result = 0
        do {
            byte = encoded[index++].code - 63
            result = result or (byte and 0x1F shl shift)
            shift += 5
        } while (byte >= 0x20)
        val deltaLng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lng += deltaLng

        points.add(Point(lat / 1e5, lng / 1e5))
    }

    return points
}
