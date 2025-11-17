package com.xichen.cloudphoto.core.config

actual class ConfigManager {
    actual fun getConfig(): AppConfig {
        // 在 iOS 中，可以通过编译配置或运行时检测来判断环境
        // 这里默认返回开发环境配置，实际使用时可以通过其他方式判断
        return AppConfig(
            environment = AppEnvironment.DEVELOPMENT,
            enableLogging = true
        )
    }
    
    actual fun isDebug(): Boolean {
        // iOS 中可以通过其他方式判断是否为调试模式
        // 这里返回 true，实际使用时应该根据实际情况判断
        return true
    }
    
    actual fun isProduction(): Boolean = !isDebug()
}

