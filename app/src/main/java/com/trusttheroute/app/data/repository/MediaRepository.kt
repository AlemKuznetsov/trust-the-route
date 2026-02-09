package com.trusttheroute.app.data.repository

import com.trusttheroute.app.data.api.MediaApi
import com.trusttheroute.app.data.api.DeleteFileRequest
import com.trusttheroute.app.data.api.UploadResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Репозиторий для работы с медиафайлами
 */
@Singleton
class MediaRepository @Inject constructor(
    private val mediaApi: MediaApi
) {
    
    /**
     * Загружает изображение в Yandex Object Storage
     * @param routeId ID маршрута
     * @param imageFile Файл изображения
     * @return URL загруженного изображения
     */
    suspend fun uploadImage(routeId: String, imageFile: File): Result<String> {
        return try {
            if (!imageFile.exists()) {
                return Result.failure(Exception("Файл не найден: ${imageFile.path}"))
            }
            
            // Определяем MIME тип по расширению
            val contentType = getImageContentType(imageFile.name)
            
            // Создаем RequestBody для файла
            val requestFile = imageFile.asRequestBody(contentType.toMediaType())
            
            // Создаем MultipartBody.Part
            val body = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)
            
            // Загружаем файл
            val response = mediaApi.uploadImage(routeId, body)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.url)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Неизвестная ошибка"
                Result.failure(Exception("Ошибка загрузки изображения: $errorMessage"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Загружает аудиофайл в Yandex Object Storage
     * @param routeId ID маршрута
     * @param audioFile Файл аудио
     * @return URL загруженного аудиофайла
     */
    suspend fun uploadAudio(routeId: String, audioFile: File): Result<String> {
        return try {
            if (!audioFile.exists()) {
                return Result.failure(Exception("Файл не найден: ${audioFile.path}"))
            }
            
            // Определяем MIME тип по расширению
            val contentType = getAudioContentType(audioFile.name)
            
            // Создаем RequestBody для файла
            val requestFile = audioFile.asRequestBody(contentType.toMediaType())
            
            // Создаем MultipartBody.Part
            val body = MultipartBody.Part.createFormData("file", audioFile.name, requestFile)
            
            // Загружаем файл
            val response = mediaApi.uploadAudio(routeId, body)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.url)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Неизвестная ошибка"
                Result.failure(Exception("Ошибка загрузки аудиофайла: $errorMessage"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Удаляет файл из Yandex Object Storage
     * @param fileUrl URL файла для удаления
     * @return true если файл успешно удален
     */
    suspend fun deleteFile(fileUrl: String): Result<Boolean> {
        return try {
            val response = mediaApi.deleteFile(DeleteFileRequest(fileUrl))
            
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Неизвестная ошибка"
                Result.failure(Exception("Ошибка удаления файла: $errorMessage"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Определяет MIME тип изображения по расширению файла
     */
    private fun getImageContentType(fileName: String): String {
        val extension = fileName.substringAfterLast('.', "").lowercase()
        return when (extension) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "webp" -> "image/webp"
            "gif" -> "image/gif"
            else -> "image/jpeg" // По умолчанию
        }
    }
    
    /**
     * Определяет MIME тип аудио по расширению файла
     */
    private fun getAudioContentType(fileName: String): String {
        val extension = fileName.substringAfterLast('.', "").lowercase()
        return when (extension) {
            "mp3" -> "audio/mpeg"
            "ogg" -> "audio/ogg"
            "wav" -> "audio/wav"
            "m4a" -> "audio/mp4"
            else -> "audio/mpeg" // По умолчанию
        }
    }
}
