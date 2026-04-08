package com.turistgo.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.turistgo.app.features.auth.ForgotPasswordScreen
import com.turistgo.app.features.auth.LoginScreen
import com.turistgo.app.features.auth.RegisterScreen
import com.turistgo.app.features.auth.ResetPasswordScreen
import com.turistgo.app.features.home.HomeScreen
import com.turistgo.app.features.feed.FeedScreen
import com.turistgo.app.features.moderator.ModeratorDashboard
import com.turistgo.app.features.moderator.ModeratorProfileScreen
import com.turistgo.app.features.post.CreatePostScreen
import com.turistgo.app.features.post.PostDetailScreen
import com.turistgo.app.features.profile.ProfileScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MainRoutes.Home
    ) {
        composable<MainRoutes.Home> {
            HomeScreen(
                onNavigateToLogin = { navController.navigate(MainRoutes.Login) },
                onNavigateToRegister = { navController.navigate(MainRoutes.Register) }
            )
        }

        composable<MainRoutes.Login> {
            LoginScreen(
                onNavigateToFeed = {
                    navController.navigate(MainRoutes.Feed) {
                        popUpTo(MainRoutes.Login) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(MainRoutes.ModeratorDashboard) {
                        popUpTo(MainRoutes.Login) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(MainRoutes.Register) },
                onNavigateToForgotPassword = { navController.navigate(MainRoutes.ForgotPassword) }
            )
        }

        composable<MainRoutes.Register> {
            RegisterScreen(
                onNavigateToFeed = {
                    navController.navigate(MainRoutes.Feed) {
                        popUpTo(MainRoutes.Register) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable<MainRoutes.Feed> {
            FeedScreen(
                onNavigateToDetail = { postId ->
                    navController.navigate(MainRoutes.PostDetail(postId))
                }
            )
        }

        composable<MainRoutes.ForgotPassword> {
            ForgotPasswordScreen(
                onNavigateToResetPassword = { navController.navigate(MainRoutes.ResetPassword) },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable<MainRoutes.ResetPassword> {
            ResetPasswordScreen(
                onNavigateToLogin = {
                    navController.navigate(MainRoutes.Login) {
                        popUpTo(MainRoutes.ForgotPassword) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable<MainRoutes.ModeratorDashboard> {
            ModeratorDashboard(
                onLogout = {
                    navController.navigate(MainRoutes.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onReviewPost = { postId -> navController.navigate(MainRoutes.ReviewPost(postId)) }
            )
        }
        
        composable<MainRoutes.ModeratorProfile> {
            ModeratorProfileScreen(
                onLogout = {
                    navController.navigate(MainRoutes.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable<MainRoutes.Create> { backStackEntry ->
            val mapResult = backStackEntry.savedStateHandle.get<String>("selected_location")
            CreatePostScreen(
                mapResult = mapResult,
                onConsumeMapResult = { backStackEntry.savedStateHandle.remove<String>("selected_location") },
                onNavigateToMapPicker = { navController.navigate(MainRoutes.MapPicker) },
                onBack = { navController.popBackStack() }
            )
        }

        composable<MainRoutes.Profile> {
            ProfileScreen(
                onNavigateToSettings = { navController.navigate(MainRoutes.Settings) },
                onLogout = {
                    navController.navigate(MainRoutes.Login) { popUpTo(0) { inclusive = true } }
                },
                onNavigateToBadges = { navController.navigate(MainRoutes.Badges) },
                onNavigateToStats = { navController.navigate(MainRoutes.Stats) },
                onNavigateToEditPost = { postId -> navController.navigate(MainRoutes.EditPost(postId)) }
            )
        }

        composable<MainRoutes.PostDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<MainRoutes.PostDetail>()
            PostDetailScreen(
                destinationId = route.postId,
                onBack = { navController.popBackStack() }
            )
        }

        // Placeholders para rutas secundarias (se deben refactorizar después)
        composable<MainRoutes.Trips> { /* Placeholder */ }
        composable<MainRoutes.Notifications> { /* Placeholder */ }
        composable<MainRoutes.UserManagement> { /* Placeholder */ }
        composable<MainRoutes.EditPost> { /* Placeholder */ }
        composable<MainRoutes.ReviewPost> { /* Placeholder */ }
        composable<MainRoutes.Stats> { /* Placeholder */ }
        composable<MainRoutes.Badges> { /* Placeholder */ }
        composable<MainRoutes.MapPicker> { /* Placeholder */ }
        composable<MainRoutes.Settings> { /* Placeholder */ }
    }
}
