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
            PlaceholderScreen("Inicio / Feed")
        }
        composable(Screen.Trips.route) {
            PlaceholderScreen("Mis Viajes")
        }
        composable(Screen.Create.route) {
            PlaceholderScreen("Crear nuevo post")
        }
        composable(Screen.Saved.route) {
            PlaceholderScreen("Posts Guardados")
        }
        composable(Screen.Profile.route) {
            PlaceholderScreen("Mi Perfil")
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
