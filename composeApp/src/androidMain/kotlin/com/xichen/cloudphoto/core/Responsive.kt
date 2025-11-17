package com.xichen.cloudphoto.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xichen.cloudphoto.core.ui.DeviceInfo
import com.xichen.cloudphoto.core.ui.DeviceType
import com.xichen.cloudphoto.core.ui.ScreenOrientation
import com.xichen.cloudphoto.core.ui.ScreenSize

/**
 * 响应式布局配置
 */
data class ResponsiveConfig(
    val deviceType: DeviceType,
    val screenSize: ScreenSize,
    val orientation: ScreenOrientation,
    val screenWidth: Dp,
    val screenHeight: Dp,
    val isTablet: Boolean,
    val isPhone: Boolean,
    val isLandscape: Boolean,
    val isPortrait: Boolean
)

/**
 * 响应式断点
 */
object Breakpoints {
    val phone: Dp = 600.dp
    val tablet: Dp = 840.dp
    val desktop: Dp = 1200.dp
}

/**
 * Local Responsive Config
 */
val LocalResponsiveConfig = compositionLocalOf<ResponsiveConfig> {
    error("No ResponsiveConfig provided")
}

/**
 * 响应式容器
 */
@Composable
fun ResponsiveContainer(
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    
    val deviceType = if (screenWidth >= Breakpoints.phone) {
        DeviceType.TABLET
    } else {
        DeviceType.PHONE
    }
    
    val screenSize = when {
        screenWidth < Breakpoints.phone -> ScreenSize.SMALL
        screenWidth < Breakpoints.tablet -> ScreenSize.MEDIUM
        screenWidth < Breakpoints.desktop -> ScreenSize.LARGE
        else -> ScreenSize.EXTRA_LARGE
    }
    
    val orientation = if (screenWidth > screenHeight) {
        ScreenOrientation.LANDSCAPE
    } else {
        ScreenOrientation.PORTRAIT
    }
    
    val config = ResponsiveConfig(
        deviceType = deviceType,
        screenSize = screenSize,
        orientation = orientation,
        screenWidth = screenWidth,
        screenHeight = screenHeight,
        isTablet = deviceType == DeviceType.TABLET,
        isPhone = deviceType == DeviceType.PHONE,
        isLandscape = orientation == ScreenOrientation.LANDSCAPE,
        isPortrait = orientation == ScreenOrientation.PORTRAIT
    )
    
    CompositionLocalProvider(LocalResponsiveConfig provides config) {
        content()
    }
}

/**
 * 获取当前响应式配置
 */
@Composable
fun rememberResponsiveConfig(): ResponsiveConfig {
    return LocalResponsiveConfig.current
}

