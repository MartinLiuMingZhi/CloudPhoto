package com.xichen.cloudphoto.core.config

import android.content.Context
import android.content.pm.ApplicationInfo

actual class ConfigManager {
    private var context: Context? = null
    
    fun init(context: Context) {
        this.context = context.applicationContext
    }
    
    actual fun getConfig(): AppConfig {
        val context = context ?: return defaultConfig()
        val isDebug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        
        return AppConfig(
            environment = if (isDebug) AppEnvironment.DEVELOPMENT else AppEnvironment.PRODUCTION,
            enableLogging = isDebug,
            enableCrashReporting = !isDebug
        )
    }
    
    actual fun isDebug(): Boolean {
        val context = context ?: return false
        return (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }
    
    actual fun isProduction(): Boolean = !isDebug()
    
    private fun defaultConfig(): AppConfig {
        return AppConfig(
            environment = AppEnvironment.DEVELOPMENT,
            enableLogging = true
        )
    }
}

