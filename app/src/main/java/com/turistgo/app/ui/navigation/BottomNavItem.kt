package com.turistgo.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("Inicio", Screen.Feed.route, Icons.Default.Home)
    object Trips : BottomNavItem("Viajes", Screen.Trips.route, Icons.Default.Map)
    object Create : BottomNavItem("Crear", Screen.Create.route, Icons.Default.Add)
    object Notifications : BottomNavItem("Alertas", Screen.Notifications.route, Icons.Default.Notifications)
    object Profile : BottomNavItem("Perfil", Screen.Profile.route, Icons.Default.Person)

    companion object {
        val items = listOf(Home, Trips, Create, Notifications, Profile)
    }
}

sealed class ModeratorBottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
) {
    object Dashboard : ModeratorBottomNavItem("Dashboard", Screen.ModeratorDashboard.route, Icons.Default.Home)
    object Users : ModeratorBottomNavItem("Usuarios", Screen.UserManagement.route, Icons.Default.Person)
    object Profile : ModeratorBottomNavItem("Perfil", Screen.ModeratorProfile.route, Icons.Default.Person)

    companion object {
        val items = listOf(Dashboard, Users, Profile)
    }
}
