package com.xichen.cloudphoto

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xichen.cloudphoto.model.Photo
import com.xichen.cloudphoto.model.StorageConfig
import com.xichen.cloudphoto.core.di.AppContainerHolder
import com.xichen.cloudphoto.core.error.ErrorHandler
import com.xichen.cloudphoto.core.logger.Log
import com.xichen.cloudphoto.service.AlbumService
import com.xichen.cloudphoto.service.ConfigService
import com.xichen.cloudphoto.service.PhotoService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {
    // 使用依赖注入容器
    private val container = AppContainerHolder.getContainer()
    
    // 初始化 Repositories
    init {
        container.configRepository.init(application)
        container.photoRepository.init(application)
        container.albumRepository.init(application)
    }
    
    // 从容器获取服务
    private val configService: ConfigService = container.configService
    private val photoService: PhotoService = container.photoService
    private val albumService: AlbumService = container.albumService
    
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
                Log.i("AppViewModel", "Photo uploaded successfully: ${it.id}")
                loadPhotos()
            }.onFailure { error ->
                val appError = ErrorHandler.handleError(error)
                Log.e("AppViewModel", "Failed to upload photo: ${appError.message}", error)
            }
        }
    }
    
    fun deletePhoto(photoId: String) {
        viewModelScope.launch {
            photoService.deletePhoto(photoId).onSuccess {
                Log.i("AppViewModel", "Photo deleted successfully: $photoId")
                loadPhotos()
            }.onFailure { error ->
                val appError = ErrorHandler.handleError(error)
                Log.e("AppViewModel", "Failed to delete photo: ${appError.message}", error)
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
        // 容器会在应用退出时统一清理
    }
}

