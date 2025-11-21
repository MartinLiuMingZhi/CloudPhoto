package com.xichen.cloudphoto.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.xichen.cloudphoto.core.theme.DefaultTheme
import com.xichen.cloudphoto.core.theme.ThemeMode

/**
 * 应用主题
 */
@Composable
fun CloudPhotoTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val isDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    
    val colorScheme = if (isDarkTheme) {
        darkColorScheme(
            primary = Color(DefaultTheme.darkColors.primary),
            secondary = Color(DefaultTheme.darkColors.secondary),
            background = Color(DefaultTheme.darkColors.background),
            surface = Color(DefaultTheme.darkColors.surface),
            error = Color(DefaultTheme.darkColors.error),
            onPrimary = Color(DefaultTheme.darkColors.onPrimary),
            onSecondary = Color(DefaultTheme.darkColors.onSecondary),
            onBackground = Color(DefaultTheme.darkColors.onBackground),
            onSurface = Color(DefaultTheme.darkColors.onSurface),
            onError = Color(DefaultTheme.darkColors.onError)
        )
    } else {
        lightColorScheme(
            primary = Color(DefaultTheme.lightColors.primary),
            secondary = Color(DefaultTheme.lightColors.secondary),
            background = Color(DefaultTheme.lightColors.background),
            surface = Color(DefaultTheme.lightColors.surface),
            error = Color(DefaultTheme.lightColors.error),
            onPrimary = Color(DefaultTheme.lightColors.onPrimary),
            onSecondary = Color(DefaultTheme.lightColors.onSecondary),
            onBackground = Color(DefaultTheme.lightColors.onBackground),
            onSurface = Color(DefaultTheme.lightColors.onSurface),
            onError = Color(DefaultTheme.lightColors.onError)
        )
    }
    
    // 设置状态栏和导航栏为透明，实现沉浸式边到边布局
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = !isDarkTheme
        )
        systemUiController.setNavigationBarColor(
            color = Color.Transparent,
            darkIcons = !isDarkTheme
        )
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

