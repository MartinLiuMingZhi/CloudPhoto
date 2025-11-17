package com.xichen.cloudphoto.core.theme

expect class ThemeRepository() {
    suspend fun getThemeMode(): ThemeMode
    suspend fun setThemeMode(mode: ThemeMode)
}

