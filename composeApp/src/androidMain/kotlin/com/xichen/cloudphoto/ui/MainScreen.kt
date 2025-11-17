package com.xichen.cloudphoto.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.xichen.cloudphoto.AppViewModel
import com.xichen.cloudphoto.core.ResponsiveContainer
import com.xichen.cloudphoto.core.rememberResponsiveConfig
import com.xichen.cloudphoto.navigation.NavGraph
import com.xichen.cloudphoto.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: AppViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val responsiveConfig = rememberResponsiveConfig()
    
    ResponsiveContainer {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Photos.route
        
        Scaffold(
            bottomBar = {
                if (responsiveConfig.isPhone) {
                    NavigationBar {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Photo, contentDescription = "照片") },
                            label = { Text("照片") },
                            selected = currentRoute == Screen.Photos.route,
                            onClick = { navController.navigate(Screen.Photos.route) }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.PhotoLibrary, contentDescription = "相册") },
                            label = { Text("相册") },
                            selected = currentRoute == Screen.Albums.route,
                            onClick = { navController.navigate(Screen.Albums.route) }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Settings, contentDescription = "我的") },
                            label = { Text("我的") },
                            selected = currentRoute == Screen.Settings.route,
                            onClick = { navController.navigate(Screen.Settings.route) }
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                NavGraph(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}

