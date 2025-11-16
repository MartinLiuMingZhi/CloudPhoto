package com.xichen.cloudphoto.storage.impl

import com.xichen.cloudphoto.model.StorageConfig
import com.xichen.cloudphoto.storage.StorageService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MinioService(private val httpClient: HttpClient) : StorageService {
    
    override suspend fun uploadPhoto(
        config: StorageConfig,
        photoData: ByteArray,
        fileName: String,
        mimeType: String
    ): Result<String> = withContext(Dispatchers.Default) {
        try {
            val objectKey = "photos/${System.currentTimeMillis()}_$fileName"
            val endpoint = config.endpoint.removeSuffix("/")
            val url = "$endpoint/${config.bucketName}/$objectKey"
            
            val response = httpClient.put(url) {
                headers {
                    append(HttpHeaders.ContentType, mimeType)
                }
                setBody(photoData)
            }
            
            if (response.status.isSuccess()) {
                Result.success(url)
            } else {
                Result.failure(Exception("Upload failed: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deletePhoto(
        config: StorageConfig,
        photoUrl: String
    ): Result<Unit> = withContext(Dispatchers.Default) {
        try {
            val response = httpClient.delete(photoUrl)
            if (response.status.isSuccess()) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Delete failed: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun downloadPhoto(
        config: StorageConfig,
        photoUrl: String
    ): Result<ByteArray> = withContext(Dispatchers.Default) {
        try {
            val response = httpClient.get(photoUrl)
            if (response.status.isSuccess()) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Download failed: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun listPhotos(
        config: StorageConfig,
        prefix: String
    ): Result<List<String>> = withContext(Dispatchers.Default) {
        try {
            val endpoint = config.endpoint.removeSuffix("/")
            val url = "$endpoint/${config.bucketName}/?prefix=photos/$prefix"
            
            val response = httpClient.get(url)
            if (response.status.isSuccess()) {
                Result.success(emptyList())
            } else {
                Result.failure(Exception("List failed: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

