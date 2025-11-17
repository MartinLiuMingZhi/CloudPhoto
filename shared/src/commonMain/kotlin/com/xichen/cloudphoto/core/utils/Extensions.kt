package com.xichen.cloudphoto.core.utils

import kotlinx.datetime.Instant

/**
 * 字符串扩展函数
 */
fun String?.isNotNullOrBlank(): Boolean = !(this == null || this.isBlank())

/**
 * 集合扩展函数
 */
fun <T> List<T>?.isNotNullOrEmpty(): Boolean = !(this == null || this.isEmpty())

/**
 * 格式化浮点数为两位小数
 * 使用四舍五入，确保精度
 */
private fun Double.formatToTwoDecimals(): String {
    // 四舍五入到两位小数
    val rounded = kotlin.math.round(this * 100) / 100.0
    val integerPart = rounded.toInt()
    val decimalPart = kotlin.math.round((rounded - integerPart) * 100).toInt()
    
    return when {
        decimalPart == 0 -> "$integerPart"
        decimalPart < 10 -> "$integerPart.0$decimalPart"
        else -> "$integerPart.$decimalPart"
    }
}

/**
 * 数字格式化
 * 使用跨平台的格式化方法，不依赖 String.format
 */
fun Long.formatFileSize(): String {
    val kb = 1024.0
    val mb = kb * 1024
    val gb = mb * 1024
    
    return when {
        this >= gb -> {
            val size = this / gb
            "${size.formatToTwoDecimals()} GB"
        }
        this >= mb -> {
            val size = this / mb
            "${size.formatToTwoDecimals()} MB"
        }
        this >= kb -> {
            val size = this / kb
            "${size.formatToTwoDecimals()} KB"
        }
        else -> "$this B"
    }
}

/**
 * 日期时间格式化
 */
fun Instant.formatDateTime(): String {
    // 简单的格式化，可以根据需要扩展
    return toString()
}

/**
 * 安全转换
 */
inline fun <T> T?.orElse(default: T): T = this ?: default

inline fun <T> T?.orElse(block: () -> T): T = this ?: block()

/**
 * 条件执行
 */
inline fun <T> T?.takeIfNotNull(block: (T) -> Unit) {
    this?.let(block)
}

