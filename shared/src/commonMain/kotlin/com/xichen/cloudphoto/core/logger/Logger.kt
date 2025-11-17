package com.xichen.cloudphoto.core.logger

/**
 * 日志级别
 */
enum class LogLevel {
    VERBOSE,
    DEBUG,
    INFO,
    WARN,
    ERROR
}

/**
 * 日志接口
 */
expect class Logger {
    constructor()
    fun v(tag: String, message: String, throwable: Throwable? = null)
    fun d(tag: String, message: String, throwable: Throwable? = null)
    fun i(tag: String, message: String, throwable: Throwable? = null)
    fun w(tag: String, message: String, throwable: Throwable? = null)
    fun e(tag: String, message: String, throwable: Throwable? = null)
}

/**
 * 创建 Logger 实例的工厂函数
 */
expect fun createLoggerInstance(): Logger

/**
 * 日志工具类
 */
object Log {
    private val logger: Logger by lazy { createLoggerInstance() }
    
    fun v(tag: String, message: String, throwable: Throwable? = null) {
        logger.v(tag, message, throwable)
    }
    
    fun d(tag: String, message: String, throwable: Throwable? = null) {
        logger.d(tag, message, throwable)
    }
    
    fun i(tag: String, message: String, throwable: Throwable? = null) {
        logger.i(tag, message, throwable)
    }
    
    fun w(tag: String, message: String, throwable: Throwable? = null) {
        logger.w(tag, message, throwable)
    }
    
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        logger.e(tag, message, throwable)
    }
    
    // 便捷方法
    inline fun <reified T> T.logV(message: String, throwable: Throwable? = null) {
        v(T::class.simpleName ?: "Unknown", message, throwable)
    }
    
    inline fun <reified T> T.logD(message: String, throwable: Throwable? = null) {
        d(T::class.simpleName ?: "Unknown", message, throwable)
    }
    
    inline fun <reified T> T.logI(message: String, throwable: Throwable? = null) {
        i(T::class.simpleName ?: "Unknown", message, throwable)
    }
    
    inline fun <reified T> T.logW(message: String, throwable: Throwable? = null) {
        w(T::class.simpleName ?: "Unknown", message, throwable)
    }
    
    inline fun <reified T> T.logE(message: String, throwable: Throwable? = null) {
        e(T::class.simpleName ?: "Unknown", message, throwable)
    }
}

