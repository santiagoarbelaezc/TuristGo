package com.turistgo.app.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Feed : Screen("feed")
    object Trips : Screen("trips")
    object Create : Screen("create")
    object Saved : Screen("saved")
    object Profile : Screen("profile")
}
