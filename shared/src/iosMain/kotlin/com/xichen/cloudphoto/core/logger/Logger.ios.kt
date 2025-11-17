package com.xichen.cloudphoto.core.logger

import platform.Foundation.NSLog

actual class Logger {
    actual constructor()
    private fun formatMessage(tag: String, message: String): String {
        return "[$tag] $message"
    }
    
    actual fun v(tag: String, message: String, throwable: Throwable?) {
        NSLog("%@", formatMessage(tag, message))
        throwable?.printStackTrace()
    }
    
    actual fun d(tag: String, message: String, throwable: Throwable?) {
        NSLog("%@", formatMessage(tag, message))
        throwable?.printStackTrace()
    }
    
    actual fun i(tag: String, message: String, throwable: Throwable?) {
        NSLog("%@", formatMessage(tag, message))
        throwable?.printStackTrace()
    }
    
    actual fun w(tag: String, message: String, throwable: Throwable?) {
        NSLog("%@", formatMessage(tag, message))
        throwable?.printStackTrace()
    }
    
    actual fun e(tag: String, message: String, throwable: Throwable?) {
        NSLog("%@", formatMessage(tag, message))
        throwable?.printStackTrace()
    }
}

actual fun createLoggerInstance(): Logger = Logger()

