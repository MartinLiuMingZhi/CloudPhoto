package com.xichen.cloudphoto.repository

import android.content.Context
import android.content.SharedPreferences
import com.xichen.cloudphoto.model.StorageConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

actual class ConfigRepository {
    private var context: Context? = null
    private val json = Json { ignoreUnknownKeys = true }
    
    fun init(context: Context) {
        this.context = context.applicationContext
    }
    
    private val prefs: SharedPreferences?
        get() = context?.getSharedPreferences("cloudphoto_configs", Context.MODE_PRIVATE)
    
    actual suspend fun saveConfig(config: StorageConfig) = withContext(Dispatchers.IO) {
        val prefs = prefs ?: return@withContext
        val jsonString = json.encodeToString(config)
        prefs.edit().putString("config_${config.id}", jsonString).apply()
        
        if (config.isDefault) {
            prefs.edit().putString("default_config_id", config.id).apply()
        }
    }
    
    actual suspend fun getConfig(id: String): StorageConfig? = withContext(Dispatchers.IO) {
        val prefs = prefs ?: return@withContext null
        val jsonString = prefs.getString("config_$id", null) ?: return@withContext null
        try {
            json.decodeFromString<StorageConfig>(jsonString)
        } catch (e: Exception) {
            null
        }
    }
    
    actual suspend fun getAllConfigs(): List<StorageConfig> = withContext(Dispatchers.IO) {
        val prefs = prefs ?: return@withContext emptyList()
        val allKeys = prefs.all.keys.filter { it.startsWith("config_") }
        allKeys.mapNotNull { key ->
            val jsonString = prefs.getString(key, null) ?: return@mapNotNull null
            try {
                json.decodeFromString<StorageConfig>(jsonString)
            } catch (e: Exception) {
                null
            }
        }
    }
    
    actual suspend fun deleteConfig(id: String) = withContext(Dispatchers.IO) {
        prefs?.edit()?.remove("config_$id")?.apply()
    }
    
    actual suspend fun getDefaultConfig(): StorageConfig? = withContext(Dispatchers.IO) {
        val prefs = prefs ?: return@withContext null
        val defaultId = prefs.getString("default_config_id", null) ?: return@withContext null
        getConfig(defaultId)
    }
    
    actual suspend fun setDefaultConfig(id: String) = withContext(Dispatchers.IO) {
        val prefs = prefs ?: return@withContext
        prefs.edit().putString("default_config_id", id).apply()
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

