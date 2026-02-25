package com.turistgo.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.turistgo.app.ui.auth.LoginScreen
import com.turistgo.app.ui.auth.RegisterScreen
import com.turistgo.app.ui.feed.FeedScreen
import com.turistgo.app.ui.notifications.NotificationsScreen
import com.turistgo.app.ui.post.CreatePostScreen
import com.turistgo.app.ui.post.PostDetailScreen
import com.turistgo.app.ui.profile.ProfileScreen
import com.turistgo.app.ui.profile.SettingsScreen
import com.turistgo.app.ui.route.RouteListScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
        
        // Pantallas principales con Bottom Nav
        composable(Screen.Feed.route) {
            FeedScreen { postId ->
                navController.navigate("post_detail/$postId")
            }
        }
        composable(Screen.Trips.route) {
            RouteListScreen()
        }
        composable(Screen.Create.route) {
            CreatePostScreen()
        }
        composable(Screen.Notifications.route) {
            NotificationsScreen()
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToSettings = {
                    navController.navigate("settings")
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable("post_detail/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")
            PostDetailScreen(postId) {
                navController.popBackStack()
            }
        }
    }
}

@Composable
fun PlaceholderScreen(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, style = MaterialTheme.typography.headlineMedium)
    }
}
