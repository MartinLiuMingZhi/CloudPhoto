package com.xichen.cloudphoto.repository

import com.xichen.cloudphoto.model.StorageConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import platform.Foundation.NSUserDefaults

actual class ConfigRepository {
    private val json = Json { ignoreUnknownKeys = true }
    private val userDefaults = NSUserDefaults.standardUserDefaults
    
    actual suspend fun saveConfig(config: StorageConfig) = withContext(Dispatchers.Default) {
        val jsonString = json.encodeToString(config)
        userDefaults.setObject(jsonString, forKey = "config_${config.id}")
        
        if (config.isDefault) {
            userDefaults.setObject(config.id, forKey = "default_config_id")
        }
    }
    
    actual suspend fun getConfig(id: String): StorageConfig? = withContext(Dispatchers.Default) {
        val jsonString = userDefaults.stringForKey("config_$id") ?: return@withContext null
        try {
            json.decodeFromString<StorageConfig>(jsonString)
        } catch (e: Exception) {
            null
        }
    }
    
    actual suspend fun getAllConfigs(): List<StorageConfig> = withContext(Dispatchers.Default) {
        val allKeys = userDefaults.dictionaryRepresentation().keys
        allKeys.filter { (it as? String)?.startsWith("config_") == true }
            .mapNotNull { key ->
                val jsonString = userDefaults.stringForKey(key as? String ?: "") ?: return@mapNotNull null
                try {
                    json.decodeFromString<StorageConfig>(jsonString)
                } catch (e: Exception) {
                    null
                }
            }
    }
    
    actual suspend fun deleteConfig(id: String) = withContext(Dispatchers.Default) {
        userDefaults.removeObjectForKey("config_$id")
    }
    
    actual suspend fun getDefaultConfig(): StorageConfig? = withContext(Dispatchers.Default) {
        val defaultId = userDefaults.stringForKey("default_config_id") ?: return@withContext null
        getConfig(defaultId)
    }
    
    actual suspend fun setDefaultConfig(id: String) = withContext(Dispatchers.Default) {
        userDefaults.setObject(id, forKey = "default_config_id")
        
        // Update all configs to set isDefault
        getAllConfigs().forEach { config ->
            if (config.id == id) {
                saveConfig(config.copy(isDefault = true))
            } else if (config.isDefault) {
                saveConfig(config.copy(isDefault = false))
            }
        }
    }
}

