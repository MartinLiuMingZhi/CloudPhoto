package com.xichen.cloudphoto.core.permission

/**
 * 权限辅助工具类
 * 提供一些通用的权限相关工具函数
 */
object PermissionHelper {
    /**
     * 获取权限的显示名称
     */
    fun getPermissionName(permission: Permission): String {
        return when (permission) {
            Permission.CAMERA -> "相机"
            Permission.PHOTO_LIBRARY -> "相册"
            Permission.STORAGE -> "存储"
        }
    }
    
    /**
     * 获取权限说明文本
     */
    fun getPermissionRationale(permission: Permission): String {
        return when (permission) {
            Permission.CAMERA -> "需要相机权限来拍摄照片并上传到云端"
            Permission.PHOTO_LIBRARY -> "需要相册权限来选择照片并上传到云端"
            Permission.STORAGE -> "需要存储权限来保存临时文件"
        }
    }
    
    /**
     * 检查权限状态是否为已授权
     */
    fun isGranted(status: PermissionStatus): Boolean {
        return status == PermissionStatus.GRANTED
    }
    
    /**
     * 检查权限状态是否为永久拒绝
     */
    fun isPermanentlyDenied(status: PermissionStatus): Boolean {
        return status == PermissionStatus.PERMANENTLY_DENIED
    }
}

