package com.trusttheroute.app.data.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.trusttheroute.app.data.database.entity.AttractionEntity
import com.trusttheroute.app.data.database.entity.RouteEntity
import com.trusttheroute.app.domain.model.Attraction
import com.trusttheroute.app.domain.model.Route

fun RouteEntity.toDomain(): Route {
    val stopsList = if (stops.isNotEmpty()) {
        try {
            Gson().fromJson<List<String>>(stops, object : TypeToken<List<String>>() {}.type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    } else {
        emptyList()
    }
    
    return Route(
        id = id,
        number = number,
        name = name,
        description = description,
        polyline = polyline,
        history = history,
        attractionsDescription = attractionsDescription,
        stops = stopsList,
        duration = duration,
        interval = interval,
        startPoint = startPoint
    )
}

fun Route.toEntity(): RouteEntity {
    val stopsJson = Gson().toJson(stops)
    
    return RouteEntity(
        id = id,
        number = number,
        name = name,
        description = description,
        polyline = polyline,
        history = history,
        attractionsDescription = attractionsDescription,
        stops = stopsJson,
        duration = duration,
        interval = interval,
        startPoint = startPoint
    )
}

fun AttractionEntity.toDomain(): Attraction {
    val imageUrlsList = if (imageUrls.isNotEmpty() && imageUrls != "[]") {
        try {
            Gson().fromJson<List<String>>(imageUrls, object : TypeToken<List<String>>() {}.type) ?: emptyList()
        } catch (e: Exception) {
            // Обратная совместимость: если старая структура с одним изображением
            emptyList()
        }
    } else {
        emptyList()
    }
    
    val localImagePathsList = if (localImagePaths.isNotEmpty() && localImagePaths != "[]") {
        try {
            Gson().fromJson<List<String>>(localImagePaths, object : TypeToken<List<String>>() {}.type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    } else {
        emptyList()
    }
    
    return Attraction(
        id = id,
        routeId = routeId,
        name = name,
        description = description,
        latitude = latitude,
        longitude = longitude,
        imageUrls = imageUrlsList,
        audioUrl = audioUrl,
        localImagePaths = localImagePathsList,
        localAudioPath = localAudioPath,
        order = order
    )
}

fun Attraction.toEntity(): AttractionEntity {
    val imageUrlsJson = Gson().toJson(imageUrls)
    val localImagePathsJson = Gson().toJson(localImagePaths)
    
    return AttractionEntity(
        id = id,
        routeId = routeId,
        name = name,
        description = description,
        latitude = latitude,
        longitude = longitude,
        imageUrls = imageUrlsJson,
        audioUrl = audioUrl,
        localImagePaths = localImagePathsJson,
        localAudioPath = localAudioPath,
        order = order
    )
}
