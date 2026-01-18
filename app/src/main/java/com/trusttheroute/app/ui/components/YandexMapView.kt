package com.trusttheroute.app.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.trusttheroute.app.domain.model.Attraction
import com.trusttheroute.app.domain.model.Route

@Composable
fun YandexMapView(
    route: Route?,
    attractions: List<Attraction>,
    currentLocation: android.location.Location?,
    onAttractionClick: ((Attraction) -> Unit)? = null,
    modifier: Modifier = Modifier,
    isDrawerOpen: Boolean = false,
    onMapClick: (() -> Unit)? = null
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

    // Обновление карты при изменении данных
    LaunchedEffect(route, attractions, currentLocation) {
        updateMap(mapView, route, attractions, currentLocation)
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

private fun updateMap(
    mapView: MapView,
    route: Route?,
    attractions: List<Attraction>,
    currentLocation: android.location.Location?
) {
    val mapObjects = mapView.map.mapObjects

    // Очистка предыдущих объектов
    mapObjects.clear()

    // Отображение маршрута (polyline)
    route?.let { r ->
        if (r.polyline.isNotEmpty()) {
            try {
                val points = decodePolyline(r.polyline)
                if (points.isNotEmpty()) {
                    val polyline = com.yandex.mapkit.geometry.Polyline(points)
                    val polylineMapObject = mapObjects.addPolyline(polyline)
                    // Установка цвета и ширины линии маршрута
                    polylineMapObject.setStrokeColor(android.graphics.Color.parseColor("#2196F3"))
                    polylineMapObject.strokeWidth = 5f

                    // Установка камеры на маршрут только если нет текущего местоположения
                    if (currentLocation == null) {
                        val center = points[points.size / 2]
                        mapView.map.move(
                            CameraPosition(center, 13f, 0f, 0f),
                            Animation(Animation.Type.SMOOTH, 1f),
                            null
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Отображение достопримечательностей
    try {
        android.util.Log.d("YandexMapView", "Adding ${attractions.size} attractions to map")
        attractions.forEach { attraction ->
            try {
                val point = Point(attraction.latitude, attraction.longitude)
                val placemark = mapObjects.addPlacemark(point)
                
                // Можно добавить кастомную иконку
                // placemark.setIcon(ImageProvider.fromResource(context, R.drawable.attraction_icon))
                
                // Сохраняем данные достопримечательности в userData для обработки кликов
                placemark.userData = attraction
                android.util.Log.d("YandexMapView", "Added placemark for: ${attraction.name}, userData set: ${placemark.userData != null}")
            } catch (e: Exception) {
                android.util.Log.e("YandexMapView", "Error adding placemark for ${attraction.name}", e)
            }
        }
    } catch (e: Exception) {
        android.util.Log.e("YandexMapView", "Error adding attractions to map", e)
    }

    // Отображение текущего местоположения пользователя
    currentLocation?.let { location ->
        val userPoint = Point(location.latitude, location.longitude)
        mapObjects.addPlacemark(userPoint)
        
        // Перемещение камеры к пользователю
        mapView.map.move(
            CameraPosition(userPoint, 15f, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 0.5f),
            null
        )
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
