package com.turistgo.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.FirebaseMessaging
import com.turistgo.app.core.navigation.AppNavigation
import com.turistgo.app.core.theme.TuristGoTheme
import com.turistgo.app.data.datastore.UserSessionManager
import com.turistgo.app.domain.repository.AppDataRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var repository: AppDataRepository

    @Inject
    lateinit var sessionManager: UserSessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Solicitar permisos de notificaciones push en Android 13+
        requestNotificationPermission()

        // Obtener y registrar el token FCM para notificaciones push
        fetchAndRegisterFcmToken()

        setContent {
            TuristGoTheme {
                AppNavigation()
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }

    private fun fetchAndRegisterFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("MainActivity", "Fallo al obtener token FCM", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("MainActivity", "Token FCM obtenido con éxito: $token")

            lifecycleScope.launch {
                val session = sessionManager.userSession.firstOrNull()
                val userId = session?.userId
                if (userId != null && token != null) {
                    repository.updateFcmToken(userId, token)
                }
            }
        }
    }
}

