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
import com.turistgo.app.ui.profile.ProfileScreen

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
            FeedScreen() // Cargamos la pantalla real de Inicio
        }
        composable(Screen.Trips.route) {
            PlaceholderScreen("Mis Viajes")
        }
        composable(Screen.Create.route) {
            PlaceholderScreen("Crear nuevo post")
        }
        composable(Screen.Notifications.route) { // Cambiado de Saved a Notifications
            PlaceholderScreen("Centro de Notificaciones")
        }
        composable(Screen.Profile.route) {
            ProfileScreen()
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
