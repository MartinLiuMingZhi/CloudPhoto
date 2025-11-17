package com.xichen.cloudphoto.core

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Toast 消息类型
 */
enum class ToastType {
    INFO,
    SUCCESS,
    WARNING,
    ERROR
}

/**
 * Toast 管理器
 */
object ToastManager {
    fun show(context: android.content.Context, message: String, type: ToastType = ToastType.INFO) {
        val duration = when (type) {
            ToastType.ERROR -> Toast.LENGTH_LONG
            else -> Toast.LENGTH_SHORT
        }
        Toast.makeText(context, message, duration).show()
    }
}

/**
 * Compose 中使用 Toast
 */
@Composable
fun rememberToast(): (String, ToastType) -> Unit {
    val context = LocalContext.current
    return { message, type ->
        ToastManager.show(context, message, type)
    }
}

