package com.xichen.cloudphoto.core.ui

import android.content.res.Configuration
import android.content.res.Resources

actual class DeviceInfo {
    private var resources: Resources? = null
    
    fun init(resources: Resources) {
        this.resources = resources
    }
    
    private val res: Resources
        get() = resources ?: throw IllegalStateException("DeviceInfo not initialized")
    
    actual val deviceType: DeviceType
        get() {
            val screenWidthDp = res.displayMetrics.widthPixels / res.displayMetrics.density
            return if (screenWidthDp >= 600) DeviceType.TABLET else DeviceType.PHONE
        }
    
    actual val screenWidth: Float
        get() = res.displayMetrics.widthPixels / res.displayMetrics.density
    
    actual val screenHeight: Float
        get() = res.displayMetrics.heightPixels / res.displayMetrics.density
    
    actual val density: Float
        get() = res.displayMetrics.density
    
    actual val orientation: ScreenOrientation
        get() = if (res.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ScreenOrientation.LANDSCAPE
        } else {
            ScreenOrientation.PORTRAIT
        }
    
    actual val screenSize: ScreenSize
        get() {
            val screenWidthDp = screenWidth
            return when {
                screenWidthDp < 600 -> ScreenSize.SMALL
                screenWidthDp < 840 -> ScreenSize.MEDIUM
                screenWidthDp < 1200 -> ScreenSize.LARGE
                else -> ScreenSize.EXTRA_LARGE
            }
        }
    
    actual fun isTablet(): Boolean = deviceType == DeviceType.TABLET
    
    actual fun isPhone(): Boolean = deviceType == DeviceType.PHONE
    
    actual fun isLandscape(): Boolean = orientation == ScreenOrientation.LANDSCAPE
    
    actual fun isPortrait(): Boolean = orientation == ScreenOrientation.PORTRAIT
}

