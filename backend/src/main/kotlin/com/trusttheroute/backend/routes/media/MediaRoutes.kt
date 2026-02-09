package com.trusttheroute.backend.routes.media

import com.trusttheroute.backend.config.StorageConfig
import com.trusttheroute.backend.models.ErrorResponse
import com.trusttheroute.backend.models.MessageResponse
import com.trusttheroute.backend.services.StorageService
import com.trusttheroute.backend.utils.JwtUtils
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class UploadResponse(
    val url: String,
    val message: String
)

/**
 * Маршруты для работы с медиафайлами (загрузка в Yandex Object Storage)
 */
fun Application.configureMediaRoutes() {
    val storageService = StorageService()
    
    routing {
        route("/api/v1/media") {
            
            // Middleware для проверки аутентификации
            val authenticate: (ApplicationCall) -> String? = { call ->
                val authHeader = call.request.header("Authorization")
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    null
                } else {
                    val token = authHeader.removePrefix("Bearer ")
                    val decodedJWT = JwtUtils.verifyToken(token)
                    decodedJWT?.getClaim("userId")?.asString()
                }
            }
            
            /**
             * Загрузка изображения
             * POST /api/v1/media/images/{routeId}
             * Content-Type: multipart/form-data
             * Body: file (изображение)
             */
            post("/images/{routeId}") {
                val userId = authenticate(call)
                if (userId == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ErrorResponse("Требуется аутентификация")
                    )
                    return@post
                }
                
                try {
                    val routeId = call.parameters["routeId"]
                    if (routeId.isNullOrBlank()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("ID маршрута обязателен")
                        )
                        return@post
                    }
                    
                    val multipartData = call.receiveMultipart()
                    var fileName: String? = null
                    var fileData: ByteArray? = null
                    var contentType: String? = null
                    
                    multipartData.forEachPart { part ->
                        when (part) {
                            is PartData.FileItem -> {
                                fileName = part.originalFileName ?: "image_${UUID.randomUUID()}.jpg"
                                contentType = part.contentType?.toString() ?: "image/jpeg"
                                
                                fileData = withContext(Dispatchers.IO) {
                                    val input = part.provider()
                                    // Input имеет метод readBytes() напрямую
                                    input.readBytes()
                                }
                            }
                            else -> part.dispose()
                        }
                    }
                    
                    if (fileData == null || fileName == null) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("Файл не найден в запросе")
                        )
                        return@post
                    }
                    
                    // Загружаем файл в Object Storage
                    val fileUrl = storageService.uploadImage(
                        routeId = routeId,
                        fileName = fileName!!,
                        fileData = fileData!!,
                        contentType = contentType ?: "image/jpeg"
                    )
                    
                    call.respond(
                        HttpStatusCode.Created,
                        UploadResponse(
                            url = fileUrl,
                            message = "Изображение успешно загружено"
                        )
                    )
                } catch (e: IllegalArgumentException) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(e.message ?: "Неверный формат файла")
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse("Ошибка при загрузке изображения: ${e.message}")
                    )
                }
            }
            
            /**
             * Загрузка аудиофайла
             * POST /api/v1/media/audio/{routeId}
             * Content-Type: multipart/form-data
             * Body: file (аудиофайл)
             */
            post("/audio/{routeId}") {
                val userId = authenticate(call)
                if (userId == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ErrorResponse("Требуется аутентификация")
                    )
                    return@post
                }
                
                try {
                    val routeId = call.parameters["routeId"]
                    if (routeId.isNullOrBlank()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("ID маршрута обязателен")
                        )
                        return@post
                    }
                    
                    val multipartData = call.receiveMultipart()
                    var fileName: String? = null
                    var fileData: ByteArray? = null
                    var contentType: String? = null
                    
                    multipartData.forEachPart { part ->
                        when (part) {
                            is PartData.FileItem -> {
                                fileName = part.originalFileName ?: "audio_${UUID.randomUUID()}.mp3"
                                contentType = part.contentType?.toString() ?: "audio/mpeg"
                                
                                fileData = withContext(Dispatchers.IO) {
                                    val input = part.provider()
                                    // Input имеет метод readBytes() напрямую
                                    input.readBytes()
                                }
                            }
                            else -> part.dispose()
                        }
                    }
                    
                    if (fileData == null || fileName == null) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("Файл не найден в запросе")
                        )
                        return@post
                    }
                    
                    // Загружаем файл в Object Storage
                    val fileUrl = storageService.uploadAudio(
                        routeId = routeId,
                        fileName = fileName!!,
                        fileData = fileData!!,
                        contentType = contentType ?: "audio/mpeg"
                    )
                    
                    call.respond(
                        HttpStatusCode.Created,
                        UploadResponse(
                            url = fileUrl,
                            message = "Аудиофайл успешно загружен"
                        )
                    )
                } catch (e: IllegalArgumentException) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(e.message ?: "Неверный формат файла")
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse("Ошибка при загрузке аудиофайла: ${e.message}")
                    )
                }
            }
            
            /**
             * Удаление файла
             * DELETE /api/v1/media/files
             * Body: { "url": "https://..." }
             */
            delete("/files") {
                val userId = authenticate(call)
                if (userId == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ErrorResponse("Требуется аутентификация")
                    )
                    return@delete
                }
                
                try {
                    val body = call.receive<Map<String, String>>()
                    val fileUrl = body["url"]
                    
                    if (fileUrl.isNullOrBlank()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("URL файла обязателен")
                        )
                        return@delete
                    }
                    
                    val deleted = storageService.deleteFile(fileUrl)
                    
                    if (deleted) {
                        call.respond(
                            HttpStatusCode.OK,
                            MessageResponse("Файл успешно удален")
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            ErrorResponse("Файл не найден")
                        )
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse("Ошибка при удалении файла: ${e.message}")
                    )
                }
            }
        }
    }
}
