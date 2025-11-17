package com.xichen.cloudphoto.core.ui

/**
 * 设备类型
 */
enum class DeviceType {
    PHONE,
    TABLET,
    DESKTOP
}

/**
 * 屏幕方向
 */
enum class ScreenOrientation {
    PORTRAIT,
    LANDSCAPE
}

/**
 * 屏幕尺寸类别
 */
enum class ScreenSize {
    SMALL,      // 手机竖屏
    MEDIUM,     // 手机横屏 / 小平板
    LARGE,      // 大平板 / 桌面
    EXTRA_LARGE // 超大屏幕
}

/**
 * 设备信息接口
 */
expect class DeviceInfo {
    val deviceType: DeviceType
    val screenWidth: Float
    val screenHeight: Float
    val density: Float
    val orientation: ScreenOrientation
    val screenSize: ScreenSize
    
    fun isTablet(): Boolean
    fun isPhone(): Boolean
    fun isLandscape(): Boolean
    fun isPortrait(): Boolean
}

