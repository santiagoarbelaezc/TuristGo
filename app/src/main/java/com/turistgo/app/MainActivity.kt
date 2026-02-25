package com.turistgo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.turistgo.app.ui.navigation.BottomNavigationBar
import com.turistgo.app.ui.navigation.NavGraph
import com.turistgo.app.ui.theme.TuristGoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TuristGoTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = { BottomNavigationBar(navController) }
                ) { innerPadding ->
                    NavMain(
                        navController = navController, 
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun NavMain(
    navController: androidx.navigation.NavHostController, 
    modifier: Modifier
) {
    // Aplicamos el padding del Scaffold al NavHost para que el contenido no quede debajo del menú
    androidx.compose.foundation.layout.Box(modifier = modifier) {
        NavGraph(navController = navController)
    }
}
