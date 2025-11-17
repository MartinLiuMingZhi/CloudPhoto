package com.xichen.cloudphoto

import androidx.compose.runtime.Composable
import com.xichen.cloudphoto.core.ResponsiveContainer
import com.xichen.cloudphoto.theme.CloudPhotoTheme
import com.xichen.cloudphoto.ui.MainScreen

@Composable
fun App() {
    ResponsiveContainer {
        CloudPhotoTheme {
            MainScreen()
        }
    }
}