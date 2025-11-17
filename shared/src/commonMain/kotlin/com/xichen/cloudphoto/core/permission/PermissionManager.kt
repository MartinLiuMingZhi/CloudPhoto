package com.xichen.cloudphoto.core.permission

import kotlinx.serialization.Serializable

/**
 * 权限类型
 */
@Serializable
enum class Permission {
    CAMERA,
    PHOTO_LIBRARY,
    STORAGE
}

/**
 * 权限状态
 */
@Serializable
enum class PermissionStatus {
    GRANTED,
    DENIED,
    PERMANENTLY_DENIED
}

/**
 * 权限管理器接口
 */
expect class PermissionManager {
    /**
     * 检查权限状态
     */
    suspend fun checkPermission(permission: Permission): PermissionStatus
    
    /**
     * 请求权限（注意：在 Android 中需要在 Activity 中使用 Activity Result API）
     * 这个方法主要用于检查当前状态，实际请求需要在平台特定代码中处理
     */
    suspend fun requestPermission(permission: Permission): PermissionStatus
    
    /**
     * 检查是否应该显示权限说明
     * 注意：在 Android 中需要 Activity 实例，这里返回 false
     * 实际使用中应该在 Compose 中使用 rememberLauncherForActivityResult
     */
    fun shouldShowRationale(permission: Permission): Boolean
}

