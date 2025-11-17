package com.xichen.cloudphoto.core.permission

import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.Photos.PHPhotoLibrary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.AVFoundation.authorizationStatusForMediaType

/**
 * iOS 权限状态常量
 * 
 * AVAuthorizationStatus 和 PHAuthorizationStatus 在 Kotlin/Native 中被映射为 NSInteger 类型别名
 * 因此需要使用数值常量进行比较
 */
private object iOSAuthorizationStatus {
    // AVAuthorizationStatus 枚举值
    // 参考: https://developer.apple.com/documentation/avfoundation/avauthorizationstatus
    const val AV_NOT_DETERMINED: Int = 0
    const val AV_RESTRICTED: Int = 1
    const val AV_DENIED: Int = 2
    const val AV_AUTHORIZED: Int = 3
    
    // PHAuthorizationStatus 枚举值
    // 参考: https://developer.apple.com/documentation/photokit/phauthorizationstatus
    const val PH_NOT_DETERMINED: Int = 0
    const val PH_RESTRICTED: Int = 1
    const val PH_DENIED: Int = 2
    const val PH_AUTHORIZED: Int = 3
    const val PH_LIMITED: Int = 4  // iOS 14+
}

/**
 * 将 AVAuthorizationStatus (NSInteger) 转换为 PermissionStatus
 */
private fun Int.toPermissionStatus(): PermissionStatus {
    return when (this) {
        iOSAuthorizationStatus.AV_AUTHORIZED -> PermissionStatus.GRANTED
        iOSAuthorizationStatus.AV_DENIED, iOSAuthorizationStatus.AV_RESTRICTED -> PermissionStatus.PERMANENTLY_DENIED
        iOSAuthorizationStatus.AV_NOT_DETERMINED -> PermissionStatus.DENIED
        else -> PermissionStatus.DENIED
    }
}

/**
 * 将 PHAuthorizationStatus (NSInteger) 转换为 PermissionStatus
 */
private fun Int.toPhotoPermissionStatus(): PermissionStatus {
    return when (this) {
        iOSAuthorizationStatus.PH_AUTHORIZED, iOSAuthorizationStatus.PH_LIMITED -> PermissionStatus.GRANTED
        iOSAuthorizationStatus.PH_DENIED, iOSAuthorizationStatus.PH_RESTRICTED -> PermissionStatus.PERMANENTLY_DENIED
        iOSAuthorizationStatus.PH_NOT_DETERMINED -> PermissionStatus.DENIED
        else -> PermissionStatus.DENIED
    }
}

/**
 * iOS 权限管理器
 * 
 * 提供统一的权限状态检查接口。注意：实际的权限请求需要在 Swift 代码中处理。
 * 
 * 使用示例：
 * ```kotlin
 * val permissionManager = PermissionManager()
 * val status = permissionManager.checkPermission(Permission.CAMERA)
 * when (status) {
 *     PermissionStatus.GRANTED -> { /* 可以使用 */ }
 *     PermissionStatus.DENIED -> { /* 需要请求权限 */ }
 *     PermissionStatus.PERMANENTLY_DENIED -> { /* 引导用户到设置 */ }
 * }
 * ```
 * 
 * 在 Swift 中请求权限：
 * ```swift
 * AVCaptureDevice.requestAccess(for: .video) { granted in
 *     // 处理结果
 * }
 * ```
 */
actual class PermissionManager {
    /**
     * 检查权限状态
     * 
     * @param permission 要检查的权限类型
     * @return 权限状态
     */
    actual suspend fun checkPermission(permission: Permission): PermissionStatus = withContext(Dispatchers.Default) {
        when (permission) {
            Permission.CAMERA -> {
                val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
                status.toInt().toPermissionStatus()
            }
            Permission.PHOTO_LIBRARY -> {
                val status = PHPhotoLibrary.authorizationStatus()
                status.toInt().toPhotoPermissionStatus()
            }
            Permission.STORAGE -> {
                // iOS 从 iOS 11 开始不再需要存储权限
                // 应用可以使用自己的沙盒目录，无需额外权限
                PermissionStatus.GRANTED
            }
        }
    }
    
    /**
     * 请求权限
     * 
     * 注意：在 iOS 中，权限请求需要在 Swift 中使用相应的 API
     * 这个方法只返回当前权限状态，不会实际请求权限
     * 
     * 实际使用中，请在 Swift 中使用：
     * ```swift
     * AVCaptureDevice.requestAccess(for: .video) { granted in
     *     // 处理权限结果
     * }
     * ```
     */
    actual suspend fun requestPermission(permission: Permission): PermissionStatus = withContext(Dispatchers.Default) {
        when (permission) {
            Permission.CAMERA -> {
                // iOS 需要在 Swift 中请求权限，这里只返回当前状态
                checkPermission(permission)
            }
            Permission.PHOTO_LIBRARY -> {
                // iOS 需要在 Swift 中请求权限，这里只返回当前状态
                checkPermission(permission)
            }
            Permission.STORAGE -> PermissionStatus.GRANTED
        }
    }
    
    /**
     * 检查是否应该显示权限说明
     * 
     * 在 iOS 中，如果权限状态是 DENIED，通常应该显示说明
     */
    actual fun shouldShowRationale(permission: Permission): Boolean {
        // iOS 中，如果权限被拒绝，应该显示说明
        // 但由于这是同步方法，无法检查权限状态
        // 实际使用中应该在 Swift 中检查权限状态后决定
        return false
    }
}

