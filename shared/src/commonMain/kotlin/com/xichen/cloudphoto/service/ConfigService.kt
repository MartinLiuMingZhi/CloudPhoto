package com.xichen.cloudphoto.service

import com.xichen.cloudphoto.model.StorageConfig
import com.xichen.cloudphoto.repository.ConfigRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConfigService(private val configRepository: ConfigRepository) {
    
    suspend fun saveConfig(config: StorageConfig) = withContext(Dispatchers.Default) {
        configRepository.saveConfig(config)
    }
    
    suspend fun getConfig(id: String): StorageConfig? = withContext(Dispatchers.Default) {
        configRepository.getConfig(id)
    }
    
    suspend fun getAllConfigs(): List<StorageConfig> = withContext(Dispatchers.Default) {
        configRepository.getAllConfigs()
    }
    
    suspend fun deleteConfig(id: String) = withContext(Dispatchers.Default) {
        configRepository.deleteConfig(id)
    }
    
    suspend fun getDefaultConfig(): StorageConfig? = withContext(Dispatchers.Default) {
        configRepository.getDefaultConfig()
    }
    
    suspend fun setDefaultConfig(id: String) = withContext(Dispatchers.Default) {
        configRepository.setDefaultConfig(id)
    }
}

