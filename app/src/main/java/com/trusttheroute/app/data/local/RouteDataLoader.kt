package com.trusttheroute.app.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.trusttheroute.app.data.database.dao.AttractionDao
import com.trusttheroute.app.data.database.dao.RouteDao
import com.trusttheroute.app.data.database.entity.AttractionEntity
import com.trusttheroute.app.data.database.entity.RouteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.InputStream

/**
 * DTO классы для парсинга JSON из assets
 */
data class RouteDataDto(
    @SerializedName("route")
    val route: RouteDto,
    @SerializedName("attractions")
    val attractions: List<AttractionDto>
)

data class RouteDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("number")
    val number: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("polyline")
    val polyline: String = "",
    @SerializedName("history")
    val history: String = "",
    @SerializedName("attractionsDescription")
    val attractionsDescription: String = "",
    @SerializedName("stops")
    val stops: List<String> = emptyList(),
    @SerializedName("duration")
    val duration: String = "",
    @SerializedName("interval")
    val interval: String = "",
    @SerializedName("startPoint")
    val startPoint: String = ""
)

data class AttractionDto(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("order")
    val order: Int = 0,
    @SerializedName("imageFileName")
    val imageFileName: String? = null, // Обратная совместимость: одно изображение
    @SerializedName("imageFileNames")
    val imageFileNames: List<String>? = null, // Список имен файлов изображений
    @SerializedName("audioFileName")
    val audioFileName: String? = null,
    @SerializedName("imageUrl")
    val imageUrl: String = "", // Обратная совместимость: один URL
    @SerializedName("imageUrls")
    val imageUrls: List<String>? = null, // Список URL изображений
    @SerializedName("audioUrl")
    val audioUrl: String? = null
)

/**
 * Класс для загрузки данных маршрутов из assets
 */
class RouteDataLoader(
    private val context: Context,
    private val routeDao: RouteDao,
    private val attractionDao: AttractionDao,
    private val gson: Gson
) {
    
    /**
     * Загружает все маршруты из папки assets/routes/
     */
    suspend fun loadRoutesFromAssets() = withContext(Dispatchers.IO) {
        try {
            val routesFolder = "routes"
            val files = context.assets.list(routesFolder) ?: emptyArray()
            
            files.filter { it.endsWith(".json") }.forEach { fileName ->
                try {
                    val inputStream = context.assets.open("$routesFolder/$fileName")
                    val routeData = parseRouteData(inputStream)
                    saveRouteData(routeData)
                } catch (e: Exception) {
                    android.util.Log.e("RouteDataLoader", "Ошибка загрузки файла $fileName", e)
                }
            }
            
            android.util.Log.d("RouteDataLoader", "Загружено маршрутов из assets: ${files.size}")
        } catch (e: Exception) {
            android.util.Log.e("RouteDataLoader", "Ошибка загрузки маршрутов из assets", e)
        }
    }
    
    /**
     * Парсит JSON файл маршрута
     */
    private fun parseRouteData(inputStream: InputStream): RouteDataDto {
        val json = inputStream.bufferedReader().use { it.readText() }
        return gson.fromJson(json, RouteDataDto::class.java)
    }
    
    /**
     * Сохраняет данные маршрута в базу данных
     */
    private suspend fun saveRouteData(routeData: RouteDataDto) {
        // Конвертируем RouteDto в RouteEntity
        val routeEntity = RouteEntity(
            id = routeData.route.id,
            number = routeData.route.number,
            name = routeData.route.name,
            description = routeData.route.description,
            polyline = routeData.route.polyline,
            history = routeData.route.history,
            attractionsDescription = routeData.route.attractionsDescription,
            stops = gson.toJson(routeData.route.stops),
            duration = routeData.route.duration,
            interval = routeData.route.interval,
            startPoint = routeData.route.startPoint
        )
        
        // Сохраняем маршрут
        routeDao.insertRoute(routeEntity)
        
        // Конвертируем AttractionDto в AttractionEntity
        val attractionEntities = routeData.attractions.mapNotNull { dto ->
            try {
                // Проверяем обязательные поля
                val id = dto.id ?: run {
                    android.util.Log.e("RouteDataLoader", "Attraction id is null, skipping")
                    return@mapNotNull null
                }
                val name = dto.name ?: "Без названия"
                val description = dto.description ?: ""
                
                // Обрабатываем несколько изображений
                val imageUrlsList = when {
                    // Новый формат: список URL
                    !dto.imageUrls.isNullOrEmpty() -> dto.imageUrls
                    // Новый формат: список имен файлов
                    !dto.imageFileNames.isNullOrEmpty() -> dto.imageFileNames.mapNotNull { 
                        if (it.isNullOrBlank()) null else "file:///android_asset/images/$it"
                    }
                    // Старый формат: один URL
                    !dto.imageUrl.isNullOrBlank() -> listOf(dto.imageUrl)
                    // Старый формат: одно имя файла
                    !dto.imageFileName.isNullOrBlank() -> listOf("file:///android_asset/images/${dto.imageFileName}")
                    else -> emptyList()
                }
                
                val localImagePathsList = when {
                    // Новый формат: список имен файлов
                    !dto.imageFileNames.isNullOrEmpty() -> dto.imageFileNames.filterNotNull().filter { it.isNotBlank() }
                    // Старый формат: одно имя файла
                    !dto.imageFileName.isNullOrBlank() -> listOf(dto.imageFileName)
                    else -> emptyList()
                }
                
                // Обрабатываем audioUrl - может быть null или пустым
                val audioUrl = when {
                    !dto.audioUrl.isNullOrBlank() -> dto.audioUrl
                    !dto.audioFileName.isNullOrBlank() -> "file:///android_asset/audio/${dto.audioFileName}"
                    else -> ""
                }
                
                AttractionEntity(
                    id = id,
                    routeId = routeData.route.id,
                    name = name,
                    description = description,
                    latitude = dto.latitude,
                    longitude = dto.longitude,
                    imageUrls = gson.toJson(imageUrlsList),
                    audioUrl = audioUrl,
                    localImagePaths = if (localImagePathsList.isNotEmpty()) gson.toJson(localImagePathsList) else "[]",
                    localAudioPath = dto.audioFileName?.takeIf { it.isNotBlank() },
                    order = dto.order
                )
            } catch (e: Exception) {
                android.util.Log.e("RouteDataLoader", "Error processing attraction: ${dto.id}", e)
                null
            }
        }
        
        // Сохраняем достопримечательности
        if (attractionEntities.isNotEmpty()) {
            attractionDao.insertAttractions(attractionEntities)
        }
    }
    
    /**
     * Проверяет, были ли уже загружены маршруты
     */
    suspend fun hasRoutes(): Boolean {
        return try {
            routeDao.getAllRoutes().first().isNotEmpty()
        } catch (e: NoSuchElementException) {
            false
        } catch (e: Exception) {
            android.util.Log.e("RouteDataLoader", "Ошибка проверки наличия маршрутов", e)
            false
        }
    }
}
