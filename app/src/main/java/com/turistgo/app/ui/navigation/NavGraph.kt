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
import com.turistgo.app.ui.home.HomeScreen
import com.turistgo.app.ui.notifications.NotificationsScreen
import com.turistgo.app.ui.post.CreatePostScreen
import com.turistgo.app.ui.post.PostDetailScreen
import com.turistgo.app.ui.profile.*
import com.turistgo.app.ui.route.RouteListScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
        composable(Screen.ForgotPassword.route) {
            com.turistgo.app.ui.auth.ForgotPasswordScreen(navController)
        }
        composable(Screen.ResetPassword.route) {
            com.turistgo.app.ui.auth.ResetPasswordScreen(navController)
        }
        composable(Screen.ModeratorDashboard.route) {
            com.turistgo.app.ui.moderator.ModeratorDashboard(navController)
        }
        composable(Screen.ReviewPost.route) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")
            com.turistgo.app.ui.moderator.ReviewPostScreen(navController, postId)
        }
        composable(Screen.UserManagement.route) {
            com.turistgo.app.ui.moderator.UserManagementScreen(navController)
        }
        composable(Screen.ModeratorProfile.route) {
            com.turistgo.app.ui.moderator.ModeratorProfileScreen(navController)
        }
        composable(Screen.EditPost.route) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")
            com.turistgo.app.ui.post.EditPostScreen(navController, postId)
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
            CreatePostScreen(navController = navController)
        }
        composable(Screen.Notifications.route) {
            NotificationsScreen()
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                navController = navController,
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
        composable(Screen.Stats.route) {
            StatsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Badges.route) {
            BadgesScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.MapPicker.route) {
            com.turistgo.app.ui.post.MapPickerScreen(
                onLocationSelected = { lat, lng ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selected_location", "$lat,$lng")
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
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
