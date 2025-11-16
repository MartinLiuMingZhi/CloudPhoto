package com.xichen.cloudphoto.repository

import com.xichen.cloudphoto.model.StorageConfig

expect class ConfigRepository() {
    suspend fun saveConfig(config: StorageConfig)
    suspend fun getConfig(id: String): StorageConfig?
    suspend fun getAllConfigs(): List<StorageConfig>
    suspend fun deleteConfig(id: String)
    suspend fun getDefaultConfig(): StorageConfig?
    suspend fun setDefaultConfig(id: String)
}

