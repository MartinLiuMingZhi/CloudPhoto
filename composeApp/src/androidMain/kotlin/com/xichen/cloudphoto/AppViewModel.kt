package com.xichen.cloudphoto

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xichen.cloudphoto.model.Photo
import com.xichen.cloudphoto.model.StorageConfig
import com.xichen.cloudphoto.repository.AlbumRepository
import com.xichen.cloudphoto.repository.ConfigRepository
import com.xichen.cloudphoto.repository.PhotoRepository
import com.xichen.cloudphoto.service.AlbumService
import com.xichen.cloudphoto.service.ConfigService
import com.xichen.cloudphoto.service.PhotoService
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val configRepository = ConfigRepository().apply {
        init(application)
    }
    private val photoRepository = PhotoRepository().apply {
        init(application)
    }
    private val albumRepository = AlbumRepository().apply {
        init(application)
    }
    
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }
    
    private val configService = ConfigService(configRepository)
    private val photoService = PhotoService(photoRepository, configRepository, httpClient)
    private val albumService = AlbumService(albumRepository, photoRepository)
    
    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos: StateFlow<List<Photo>> = _photos.asStateFlow()
    
    private val _configs = MutableStateFlow<List<StorageConfig>>(emptyList())
    val configs: StateFlow<List<StorageConfig>> = _configs.asStateFlow()
    
    private val _defaultConfig = MutableStateFlow<StorageConfig?>(null)
    val defaultConfig: StateFlow<StorageConfig?> = _defaultConfig.asStateFlow()
    
    init {
        loadPhotos()
        loadConfigs()
    }
    
    fun loadPhotos() {
        viewModelScope.launch {
            _photos.value = photoService.getAllPhotos()
        }
    }
    
    fun loadConfigs() {
        viewModelScope.launch {
            _configs.value = configService.getAllConfigs()
            _defaultConfig.value = configService.getDefaultConfig()
        }
    }
    
    fun uploadPhoto(photoData: ByteArray, fileName: String, mimeType: String, width: Int, height: Int) {
        viewModelScope.launch {
            val result = photoService.uploadPhoto(
                photoData = photoData,
                fileName = fileName,
                mimeType = mimeType,
                width = width,
                height = height
            )
            result.onSuccess {
                loadPhotos()
            }
        }
    }
    
    fun deletePhoto(photoId: String) {
        viewModelScope.launch {
            photoService.deletePhoto(photoId).onSuccess {
                loadPhotos()
            }
        }
    }
    
    fun saveConfig(config: StorageConfig) {
        viewModelScope.launch {
            configService.saveConfig(config)
            loadConfigs()
        }
    }
    
    fun deleteConfig(configId: String) {
        viewModelScope.launch {
            configService.deleteConfig(configId)
            loadConfigs()
        }
    }
    
    fun setDefaultConfig(configId: String) {
        viewModelScope.launch {
            configService.setDefaultConfig(configId)
            loadConfigs()
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        httpClient.close()
    }
}

