package com.turistgo.app.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Login : Screen("login")
    object Register : Screen("register")
    object Feed : Screen("feed")
    object Trips : Screen("trips")
    object Create : Screen("create")
    object Notifications : Screen("notifications")
    object Profile : Screen("profile")
    object ForgotPassword : Screen("forgot_password")
    object ResetPassword : Screen("reset_password")
    object ModeratorDashboard : Screen("moderator_dashboard")
    object ReviewPost : Screen("review_post/{postId}") {
        fun createRoute(postId: String) = "review_post/$postId"
    }
    object UserManagement : Screen("user_management")
    object ModeratorProfile : Screen("moderator_profile")
    object EditPost : Screen("edit_post/{postId}") {
        fun createRoute(postId: String) = "edit_post/$postId"
    }
}
