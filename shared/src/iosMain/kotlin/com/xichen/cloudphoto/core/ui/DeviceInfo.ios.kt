package com.xichen.cloudphoto.core.ui

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.UIKit.UIScreen

@OptIn(ExperimentalForeignApi::class)
actual class DeviceInfo {
    private val screen = UIScreen.mainScreen
    
    private val width: Double
        get() = screen.bounds.useContents { 
            size.width
        }
    
    private val height: Double
        get() = screen.bounds.useContents { 
            size.height
        }
    
    actual val deviceType: DeviceType
        get() {
            val minDimension = minOf(width, height)
            return if (minDimension >= 768.0) DeviceType.TABLET else DeviceType.PHONE
        }
    
    actual val screenWidth: Float
        get() = width.toFloat()
    
    actual val screenHeight: Float
        get() = height.toFloat()
    
    actual val density: Float
        get() = screen.scale.toDouble().toFloat()
    
    actual val orientation: ScreenOrientation
        get() = if (width > height) ScreenOrientation.LANDSCAPE else ScreenOrientation.PORTRAIT
    
    actual val screenSize: ScreenSize
        get() {
            val minDimension = minOf(screenWidth, screenHeight)
            return when {
                minDimension < 600 -> ScreenSize.SMALL
                minDimension < 840 -> ScreenSize.MEDIUM
                minDimension < 1200 -> ScreenSize.LARGE
                else -> ScreenSize.EXTRA_LARGE
            }
        }
    
    actual fun isTablet(): Boolean = deviceType == DeviceType.TABLET
    
    actual fun isPhone(): Boolean = deviceType == DeviceType.PHONE
    
    actual fun isLandscape(): Boolean = orientation == ScreenOrientation.LANDSCAPE
    
    actual fun isPortrait(): Boolean = orientation == ScreenOrientation.PORTRAIT
}

