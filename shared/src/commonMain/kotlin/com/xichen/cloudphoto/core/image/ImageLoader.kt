package com.xichen.cloudphoto.core.image

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

/**
 * 图片加载结果
 */
sealed class ImageLoadResult {
    data class Success(val data: ByteArray) : ImageLoadResult()
    data class Error(val exception: Throwable) : ImageLoadResult()
    object Loading : ImageLoadResult()
}

/**
 * 图片加载器接口
 */
interface ImageLoader {
    suspend fun loadImage(url: String): ImageLoadResult
    suspend fun loadImageWithCache(url: String): ImageLoadResult
    fun clearCache()
}

/**
 * 简单的图片加载器实现
 * 注意：这是一个基础实现，生产环境建议使用专业的图片加载库（如 Coil、Glide 等）
 */
class SimpleImageLoader(
    private val httpClient: HttpClient
) : ImageLoader {
    
    // 简单的内存缓存
    private val memoryCache = mutableMapOf<String, ByteArray>()
    private val maxCacheSize = 50 * 1024 * 1024 // 50MB
    private var currentCacheSize = 0L
    
    override suspend fun loadImage(url: String): ImageLoadResult = withContext(Dispatchers.IO) {
        try {
            val data = httpClient.get(url).body<ByteArray>()
            ImageLoadResult.Success(data)
        } catch (e: Exception) {
            ImageLoadResult.Error(e)
        }
    }
    
    override suspend fun loadImageWithCache(url: String): ImageLoadResult = withContext(Dispatchers.IO) {
        // 先检查缓存
        memoryCache[url]?.let {
            return@withContext ImageLoadResult.Success(it)
        }
        
        // 从网络加载
        loadImage(url).let { result ->
            if (result is ImageLoadResult.Success) {
                // 添加到缓存
                addToCache(url, result.data)
            }
            result
        }
    }
    
    private fun addToCache(url: String, data: ByteArray) {
        val dataSize = data.size.toLong()
        
        // 如果缓存太大，清理一些
        while (currentCacheSize + dataSize > maxCacheSize && memoryCache.isNotEmpty()) {
            val firstKey = memoryCache.keys.first()
            val removedData = memoryCache.remove(firstKey)
            currentCacheSize -= removedData?.size?.toLong() ?: 0
        }
        
        memoryCache[url] = data
        currentCacheSize += dataSize
    }
    
    override fun clearCache() {
        memoryCache.clear()
        currentCacheSize = 0
    }
}

