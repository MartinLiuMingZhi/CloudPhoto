package com.xichen.cloudphoto.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            bottomBar = {
                if (responsiveConfig.isPhone) {
                    ModernNavigationBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            // 边到边沉浸式布局：内容完全延伸到状态栏和导航栏下方
            // 各个Screen的TopAppBar会自己处理状态栏的padding
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                NavGraph(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
private fun ModernNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val tabs = listOf(
        TabItem(
            route = Screen.Photos.route,
            icon = Icons.Default.Photo,
            label = "照片"
        ),
        TabItem(
            route = Screen.Albums.route,
            icon = Icons.Default.PhotoLibrary,
            label = "相册"
        ),
        TabItem(
            route = Screen.Settings.route,
            icon = Icons.Default.Settings,
            label = "我的"
        )
    )
    
    val colorScheme = MaterialTheme.colorScheme
    
    // 使用WindowInsets处理底部安全区域
    NavigationBar(
        modifier = Modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .windowInsetsPadding(WindowInsets.navigationBars),
        containerColor = colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        tabs.forEach { tab ->
            val selected = currentRoute == tab.route
            val animatedAlpha by animateFloatAsState(
                targetValue = if (selected) 1f else 0.6f,
                animationSpec = tween(durationMillis = 200),
                label = "tab_alpha"
            )
            
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        modifier = Modifier.size(24.dp),
                        tint = if (selected) {
                            colorScheme.primary
                        } else {
                            colorScheme.onSurface.copy(alpha = animatedAlpha)
                        }
                    )
                },
                label = {
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) {
                            colorScheme.primary
                        } else {
                            colorScheme.onSurface.copy(alpha = animatedAlpha)
                        }
                    )
                },
                selected = selected,
                onClick = { onNavigate(tab.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colorScheme.primary,
                    selectedTextColor = colorScheme.primary,
                    indicatorColor = colorScheme.primaryContainer.copy(alpha = 0.3f),
                    unselectedIconColor = colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )
        }
    }
}

private data class TabItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

