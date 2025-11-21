package com.xichen.cloudphoto.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.xichen.cloudphoto.ui.AlbumsScreen
import com.xichen.cloudphoto.ui.MainScreen
import com.xichen.cloudphoto.ui.PhotosScreen
import com.xichen.cloudphoto.ui.SettingsScreen
import com.xichen.cloudphoto.AppViewModel

/**
 * 路由定义
 */
sealed class Screen(val route: String) {
    object Photos : Screen("photos")
    object Albums : Screen("albums")
    object Settings : Screen("settings")
}

/**
 * 导航图 - 主界面导航（不包含登录界面）
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: AppViewModel,
    startDestination: String = Screen.Photos.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Photos.route) {
            PhotosScreen(viewModel = viewModel)
        }
        composable(Screen.Albums.route) {
            AlbumsScreen(viewModel = viewModel)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(viewModel = viewModel)
        }
    }
}

