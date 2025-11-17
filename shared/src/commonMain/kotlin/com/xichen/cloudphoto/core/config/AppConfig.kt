package com.xichen.cloudphoto.core.config

/**
 * 应用环境
 */
enum class AppEnvironment {
    DEVELOPMENT,
    STAGING,
    PRODUCTION
}

/**
 * 应用配置
 */
data class AppConfig(
    val environment: AppEnvironment,
    val apiBaseUrl: String? = null,
    val enableLogging: Boolean = true,
    val enableCrashReporting: Boolean = false
)

/**
 * 配置管理器
 */
expect class ConfigManager {
    fun getConfig(): AppConfig
    fun isDebug(): Boolean
    fun isProduction(): Boolean
}

