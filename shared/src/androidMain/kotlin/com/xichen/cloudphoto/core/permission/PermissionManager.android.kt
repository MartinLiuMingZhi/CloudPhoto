package com.xichen.cloudphoto.core.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Android 权限管理器
 * 
 * 注意：Android 的权限请求需要在 Activity 中使用 Activity Result API
 * 建议在 Compose 中使用 rememberLauncherForActivityResult 来处理权限请求
 * 
 * 使用示例：
 * ```kotlin
 * val permissionLauncher = rememberLauncherForActivityResult(
 *     ActivityResultContracts.RequestPermission()
 * ) { isGranted ->
 *     // 处理权限结果
 * }
 * 
 * // 请求权限
 * permissionLauncher.launch(Manifest.permission.CAMERA)
 * ```
 */
actual class PermissionManager {
    private var context: Context? = null
    
    /**
     * 初始化权限管理器
     * 必须在检查权限前调用
     */
    fun init(context: Context) {
        this.context = context.applicationContext
    }
    
    /**
     * 将通用权限类型转换为 Android 权限字符串
     */
    private fun getAndroidPermission(permission: Permission): String {
        return when (permission) {
            Permission.CAMERA -> Manifest.permission.CAMERA
            Permission.PHOTO_LIBRARY -> Manifest.permission.READ_EXTERNAL_STORAGE
            Permission.STORAGE -> Manifest.permission.WRITE_EXTERNAL_STORAGE
        }
    }
    
    /**
     * 检查权限状态
     */
    actual suspend fun checkPermission(permission: Permission): PermissionStatus = withContext(Dispatchers.IO) {
        val context = this@PermissionManager.context 
            ?: return@withContext PermissionStatus.DENIED
        
        val androidPermission = getAndroidPermission(permission)
        
        when (ContextCompat.checkSelfPermission(context, androidPermission)) {
            PackageManager.PERMISSION_GRANTED -> PermissionStatus.GRANTED
            else -> PermissionStatus.DENIED
        }
    }
    
    /**
     * 请求权限
     * 
     * 注意：在 Android 中，权限请求必须在 Activity 中使用 Activity Result API
     * 这个方法只返回当前权限状态，不会实际请求权限
     * 
     * 实际使用中，请在 Compose 中使用：
     * ```kotlin
     * val permissionLauncher = rememberLauncherForActivityResult(
     *     ActivityResultContracts.RequestPermission()
     * ) { isGranted -> ... }
     * permissionLauncher.launch(Manifest.permission.CAMERA)
     * ```
     */
    actual suspend fun requestPermission(permission: Permission): PermissionStatus {
        // Android 需要在 Activity 中请求权限，这里只返回当前状态
        return checkPermission(permission)
    }
    
    /**
     * 检查是否应该显示权限说明
     * 
     * 注意：在 Android 中，这个方法需要 Activity 实例
     * 由于 expect/actual 的限制，这里返回 false
     * 
     * 实际使用中，请在 Compose 中使用：
     * ```kotlin
     * val activity = LocalContext.current as? ComponentActivity
     * val shouldShow = activity?.shouldShowRequestPermissionRationale(permission) ?: false
     * ```
     */
    actual fun shouldShowRationale(permission: Permission): Boolean {
        // Android 需要在 Activity 中检查，这里返回 false
        // 实际使用中应该在 Compose 中直接使用 Activity 的方法
        return false
    }
}

