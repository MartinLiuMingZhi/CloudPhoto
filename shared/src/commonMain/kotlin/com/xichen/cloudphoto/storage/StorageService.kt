package com.xichen.cloudphoto.storage

import com.xichen.cloudphoto.model.Photo
import com.xichen.cloudphoto.model.StorageConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.IOException

interface StorageService {
    suspend fun uploadPhoto(
        config: StorageConfig,
        photoData: ByteArray,
        fileName: String,
        mimeType: String
    ): Result<String> // Returns the URL of uploaded photo
    
    suspend fun deletePhoto(
        config: StorageConfig,
        photoUrl: String
    ): Result<Unit>
    
    suspend fun downloadPhoto(
        config: StorageConfig,
        photoUrl: String
    ): Result<ByteArray>
    
    suspend fun listPhotos(
        config: StorageConfig,
        prefix: String = ""
    ): Result<List<String>> // Returns list of photo URLs
}

// Factory to create storage service based on provider
object StorageServiceFactory {
    fun create(provider: com.xichen.cloudphoto.model.StorageProvider, httpClient: HttpClient): StorageService {
        return when (provider) {
            com.xichen.cloudphoto.model.StorageProvider.ALIYUN_OSS -> AliyunOssService(httpClient)
            com.xichen.cloudphoto.model.StorageProvider.AWS_S3 -> AwsS3Service(httpClient)
            com.xichen.cloudphoto.model.StorageProvider.TENCENT_COS -> TencentCosService(httpClient)
            com.xichen.cloudphoto.model.StorageProvider.MINIO -> MinioService(httpClient)
            com.xichen.cloudphoto.model.StorageProvider.CUSTOM_S3 -> CustomS3Service(httpClient)
        }
    }
}

