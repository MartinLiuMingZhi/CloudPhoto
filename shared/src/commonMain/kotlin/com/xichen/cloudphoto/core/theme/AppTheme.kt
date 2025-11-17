package com.xichen.cloudphoto.core.theme

import kotlinx.serialization.Serializable

/**
 * 应用主题配置
 */
@Serializable
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

/**
 * 主题颜色配置
 */
data class AppColors(
    val primary: Long,
    val secondary: Long,
    val background: Long,
    val surface: Long,
    val error: Long,
    val onPrimary: Long,
    val onSecondary: Long,
    val onBackground: Long,
    val onSurface: Long,
    val onError: Long
)

/**
 * 默认主题颜色
 */
object DefaultTheme {
    val lightColors = AppColors(
        primary = 0xFF6200EE,
        secondary = 0xFF03DAC6,
        background = 0xFFFFFFFF,
        surface = 0xFFFFFFFF,
        error = 0xFFB00020,
        onPrimary = 0xFFFFFFFF,
        onSecondary = 0xFF000000,
        onBackground = 0xFF000000,
        onSurface = 0xFF000000,
        onError = 0xFFFFFFFF
    )
    
    val darkColors = AppColors(
        primary = 0xFFBB86FC,
        secondary = 0xFF03DAC6,
        background = 0xFF121212,
        surface = 0xFF1E1E1E,
        error = 0xFFCF6679,
        onPrimary = 0xFF000000,
        onSecondary = 0xFF000000,
        onBackground = 0xFFFFFFFF,
        onSurface = 0xFFFFFFFF,
        onError = 0xFF000000
    )
}

