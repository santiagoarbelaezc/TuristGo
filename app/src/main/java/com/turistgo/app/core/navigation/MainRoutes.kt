package com.turistgo.app.core.navigation

import kotlinx.serialization.Serializable

sealed class MainRoutes {
    @Serializable data object Home
    @Serializable data object Login
    @Serializable data object Register
    @Serializable data class CompleteProfile(val userId: String)
    @Serializable data object Feed
    @Serializable data object Trips
    @Serializable data object Create
    @Serializable data object Notifications
    @Serializable data object Profile
    @Serializable data object ForgotPassword
    @Serializable data object ResetPassword
    
    // Moderator Section
    @Serializable data object ModeratorDashboard
    @Serializable data object ModeratorStats
    @Serializable data object ModeratorUsers
    @Serializable data object ModeratorSettings
    @Serializable data object ModeratorProfile
    @Serializable data object Stats
    
    @Serializable data class ReviewPost(val postId: String)
    @Serializable data class EditPost(val postId: String)
    @Serializable data class PostDetail(val postId: String)
    @Serializable data class PublicProfile(val userId: String)
    @Serializable data object Badges
    @Serializable data object ProgressGuide
    @Serializable data object MapPicker
    @Serializable data object Settings
    @Serializable data object EditProfile
    @Serializable data object PrivacyPolicy
    @Serializable data object UsagePolicy
    @Serializable data object HelpSupport
}
