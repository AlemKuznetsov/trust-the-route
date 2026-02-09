package com.trusttheroute.app.data.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import java.io.File

/**
 * API для работы с медиафайлами (загрузка в Yandex Object Storage)
 */
interface MediaApi {
    
    /**
     * Загрузка изображения
     * @param routeId ID маршрута (например, "bus_b")
     * @param file Файл изображения
     * @return URL загруженного файла
     */
    @Multipart
    @POST("media/images/{routeId}")
    suspend fun uploadImage(
        @Path("routeId") routeId: String,
        @Part file: MultipartBody.Part
    ): Response<UploadResponse>
    
    /**
     * Загрузка аудиофайла
     * @param routeId ID маршрута
     * @param file Файл аудио
     * @return URL загруженного файла
     */
    @Multipart
    @POST("media/audio/{routeId}")
    suspend fun uploadAudio(
        @Path("routeId") routeId: String,
        @Part file: MultipartBody.Part
    ): Response<UploadResponse>
    
    /**
     * Удаление файла
     * @param request Запрос с URL файла для удаления
     * @return Результат удаления
     */
    @DELETE("media/files")
    suspend fun deleteFile(@Body request: DeleteFileRequest): Response<MessageResponse>
}

/**
 * Ответ при загрузке файла
 */
data class UploadResponse(
    val url: String,
    val message: String
)

/**
 * Запрос на удаление файла
 */
data class DeleteFileRequest(
    val url: String
)
