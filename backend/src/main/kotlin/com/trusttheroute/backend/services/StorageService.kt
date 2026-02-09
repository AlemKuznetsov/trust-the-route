package com.trusttheroute.backend.services

import com.trusttheroute.backend.config.StorageConfig
import io.ktor.http.content.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.net.URI
import java.time.Duration
import java.util.*

/**
 * Сервис для работы с Yandex Object Storage
 */
class StorageService {
    private val s3Client: S3Client by lazy {
        if (!StorageConfig.isConfigured()) {
            throw IllegalStateException("Yandex Object Storage не настроен. Установите переменные окружения YANDEX_STORAGE_ACCESS_KEY и YANDEX_STORAGE_SECRET_KEY")
        }
        
        S3Client.builder()
            .endpointOverride(URI.create(StorageConfig.endpointUrl))
            .region(Region.of(StorageConfig.region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        StorageConfig.accessKeyId,
                        StorageConfig.secretAccessKey
                    )
                )
            )
            .forcePathStyle(true) // Важно для Yandex Object Storage
            .build()
    }
    
    /**
     * Загружает изображение в Object Storage
     * @param routeId ID маршрута (например, "bus_b")
     * @param fileName Имя файла
     * @param fileData Данные файла
     * @param contentType MIME тип файла
     * @return URL загруженного файла
     */
    suspend fun uploadImage(
        routeId: String,
        fileName: String,
        fileData: ByteArray,
        contentType: String
    ): String = withContext(Dispatchers.IO) {
        // Валидация размера
        if (fileData.size > StorageConfig.MAX_IMAGE_SIZE) {
            throw IllegalArgumentException("Размер изображения превышает максимально допустимый (${StorageConfig.MAX_IMAGE_SIZE / 1024 / 1024} MB)")
        }
        
        // Валидация типа
        if (contentType !in StorageConfig.allowedImageTypes) {
            throw IllegalArgumentException("Неподдерживаемый тип изображения: $contentType")
        }
        
        val s3Key = "${StorageConfig.imagesPath}/$routeId/$fileName"
        
        try {
            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(StorageConfig.bucketName)
                    .key(s3Key)
                    .contentType(contentType)
                    .acl(ObjectCannedACL.PUBLIC_READ) // Публичный доступ для чтения
                    .build(),
                RequestBody.fromBytes(fileData)
            )
            
            // Возвращаем публичный URL
            "${StorageConfig.baseUrl}/$s3Key"
        } catch (e: Exception) {
            throw RuntimeException("Ошибка при загрузке изображения: ${e.message}", e)
        }
    }
    
    /**
     * Загружает аудиофайл в Object Storage
     * @param routeId ID маршрута
     * @param fileName Имя файла
     * @param fileData Данные файла
     * @param contentType MIME тип файла
     * @return URL загруженного файла
     */
    suspend fun uploadAudio(
        routeId: String,
        fileName: String,
        fileData: ByteArray,
        contentType: String
    ): String = withContext(Dispatchers.IO) {
        // Валидация размера
        if (fileData.size > StorageConfig.MAX_AUDIO_SIZE) {
            throw IllegalArgumentException("Размер аудиофайла превышает максимально допустимый (${StorageConfig.MAX_AUDIO_SIZE / 1024 / 1024} MB)")
        }
        
        // Валидация типа
        if (contentType !in StorageConfig.allowedAudioTypes) {
            throw IllegalArgumentException("Неподдерживаемый тип аудиофайла: $contentType")
        }
        
        val s3Key = "${StorageConfig.audioPath}/$routeId/$fileName"
        
        try {
            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(StorageConfig.bucketName)
                    .key(s3Key)
                    .contentType(contentType)
                    .acl(ObjectCannedACL.PUBLIC_READ) // Публичный доступ для чтения
                    .build(),
                RequestBody.fromBytes(fileData)
            )
            
            // Возвращаем публичный URL
            "${StorageConfig.baseUrl}/$s3Key"
        } catch (e: Exception) {
            throw RuntimeException("Ошибка при загрузке аудиофайла: ${e.message}", e)
        }
    }
    
    /**
     * Удаляет файл из Object Storage
     * @param fileUrl URL файла для удаления
     */
    suspend fun deleteFile(fileUrl: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Извлекаем ключ из URL
            val s3Key = fileUrl.removePrefix("${StorageConfig.baseUrl}/")
            
            s3Client.deleteObject(
                DeleteObjectRequest.builder()
                    .bucket(StorageConfig.bucketName)
                    .key(s3Key)
                    .build()
            )
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Проверяет существование файла
     * @param fileUrl URL файла
     * @return true если файл существует
     */
    suspend fun fileExists(fileUrl: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val s3Key = fileUrl.removePrefix("${StorageConfig.baseUrl}/")
            
            s3Client.headObject(
                HeadObjectRequest.builder()
                    .bucket(StorageConfig.bucketName)
                    .key(s3Key)
                    .build()
            )
            
            true
        } catch (e: NoSuchKeyException) {
            false
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Генерирует подписанный URL для временного доступа к приватному файлу
     * @param fileUrl URL файла
     * @param expirationMinutes Время действия URL в минутах (по умолчанию 60)
     * @return Подписанный URL
     */
    suspend fun generatePresignedUrl(fileUrl: String, expirationMinutes: Int = 60): String = withContext(Dispatchers.IO) {
        val s3Key = fileUrl.removePrefix("${StorageConfig.baseUrl}/")
        
        val presigner = S3Presigner.builder()
            .endpointOverride(URI.create(StorageConfig.endpointUrl))
            .region(Region.of(StorageConfig.region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        StorageConfig.accessKeyId,
                        StorageConfig.secretAccessKey
                    )
                )
            )
            .build()
        
        val getObjectRequest = GetObjectRequest.builder()
            .bucket(StorageConfig.bucketName)
            .key(s3Key)
            .build()
        
        val presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(expirationMinutes.toLong()))
            .getObjectRequest(getObjectRequest)
            .build()
        
        presigner.presignGetObject(presignRequest).url().toString()
    }
    
    /**
     * Закрывает клиент (вызывать при остановке приложения)
     */
    fun close() {
        s3Client.close()
    }
}
