package com.turistgo.app.core.navigation

import kotlinx.serialization.Serializable

sealed class MainRoutes {
    @Serializable data object Home
    @Serializable data object Login
    @Serializable data object Register
    @Serializable data object Feed
    @Serializable data object Trips
    @Serializable data object Create
    @Serializable data object Notifications
    @Serializable data object Profile
    @Serializable data object ForgotPassword
    @Serializable data object ResetPassword
    @Serializable data object ModeratorDashboard
    @Serializable data class ReviewPost(val postId: String)
    @Serializable data object UserManagement
    @Serializable data object ModeratorProfile
    @Serializable data class EditPost(val postId: String)
    @Serializable data class PostDetail(val postId: String)
    @Serializable data object Stats
    @Serializable data object Badges
    @Serializable data object MapPicker
    @Serializable data object Settings
}
