// Declara el paquete donde se encuentra esta pantalla dentro de la estructura de la app.
package com.turistgo.app.features.home

// Importaciones de Jetpack Compose para la UI
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
// Importaciones para reproductor de video ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
// Importaciones de componentes personalizados
import com.turistgo.app.core.components.InPlaceVideoPlayer
import com.turistgo.app.core.navigation.MainRoutes
import coil.compose.AsyncImage
// Importaciones para internacionalización (i18n)
import com.turistgo.app.core.locale.AppStrings
import com.turistgo.app.core.locale.LanguageState
import com.turistgo.app.core.locale.AppLanguage
// Importaciones para autenticación y ViewModel
import com.turistgo.app.features.auth.LoginViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.turistgo.app.core.components.SocialLoginCard

// Declara la función composable principal de la pantalla de inicio (Home)
@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit, // Callback para navegar a la pantalla de inicio de sesión
    onNavigateToRegister: () -> Unit, // Callback para navegar a la pantalla de registro
    onNavigateToFeed: () -> Unit, // Callback para navegar al feed principal (cuando ya hay sesión)
    viewModel: LoginViewModel = hiltViewModel() // ViewModel de login inyectado por Hilt
) {
    // Obtiene el idioma actual del estado global
    val lang by LanguageState.current
    // Obtiene los strings traducidos según el idioma actual
    val s = AppStrings.get(lang)
    // Estado para el scroll vertical
    val scrollState = rememberScrollState()

    // URL del logo de TuristGo (almacenado en Cloudinary)
    val imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1771997914/logo-turist_x5xgsq.png"

    // Estado para controlar la superposición del video (animación del logo)
    var showVideoOverlay by remember { mutableStateOf(false) }

    // Contenedor Box que ocupa toda la pantalla
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Fondo del tema actual
    ) {
        // Columna principal con scroll vertical
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState) // Habilita scroll vertical
                .padding(horizontal = 24.dp), // Padding horizontal de 24dp (sin padding vertical)
            horizontalAlignment = Alignment.CenterHorizontally, // Centra horizontalmente los elementos
            verticalArrangement = Arrangement.Top // Alinea al inicio verticalmente
        ) {
            // --- Selector de idioma (esquina superior derecha) ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp), // Ocupa todo el ancho, padding superior
                horizontalArrangement = Arrangement.End // Alinea a la derecha
            ) {
                TextButton(onClick = {
                    // Alterna entre español e inglés
                    LanguageState.current.value = if (lang == AppLanguage.SPANISH) AppLanguage.ENGLISH else AppLanguage.SPANISH
                }) {
                    Text(
                        text = if (lang == AppLanguage.SPANISH) "EN" else "ES", // Muestra "EN" si está en español, "ES" si está en inglés
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // --- Logo (con animación de video al hacer clic) ---
            Box(
                modifier = Modifier.size(120.dp), // Tamaño fijo de 120x120dp
                contentAlignment = Alignment.Center
            ) {
                var isVideoReady by remember { mutableStateOf(false) } // Estado para saber si el video está listo

                // Superposición de video (se muestra cuando showVideoOverlay es true)
                if (showVideoOverlay) {
                    // URI del video almacenado en recursos raw (video_login.mp4)
                    val videoUri = "android.resource://${LocalContext.current.packageName}/${com.turistgo.app.R.raw.video_login}"
                    Surface(
                        modifier = Modifier.size(120.dp),
                        shape = CircleShape, // Forma circular igual que el logo
                        color = MaterialTheme.colorScheme.background,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) // Borde primario semitransparente
                        )
                    ) {
                        InPlaceVideoPlayer(
                            videoUrl = videoUri,
                            onReady = { isVideoReady = true }, // Callback cuando el video está listo
                            onFinished = {
                                showVideoOverlay = false // Oculta el video al terminar
                                isVideoReady = false
                            }
                        )
                    }
                }

                // La imagen del logo se oculta instantáneamente cuando el video está listo
                if (!showVideoOverlay || !isVideoReady) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Logo de TuristGo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null // Sin indicador visual de clic (efecto ripple)
                            ) {
                                showVideoOverlay = true // Al hacer clic, muestra el video
                            },
                        contentScale = ContentScale.Fit // Ajusta la imagen sin recortar
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp)) // Espaciado

            // --- Título de la aplicación ---
            Text(
                text = "TuristGo",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary // Color primario del tema
            )
            // Subtítulo (traducido)
            Text(
                text = s.discoverNextAdventure, // "Descubre tu próxima aventura" o similar
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Tarjeta con descripción de la aplicación ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium, // Bordes redondeados estándar
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) // Color de superficie semitransparente
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Sin sombra
            ) {
                Text(
                    text = s.appDescription, // Descripción traducida de la app
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp, // Altura de línea para mejor legibilidad
                    modifier = Modifier.padding(16.dp) // Padding interno
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Botón: Iniciar Sesión ---
            Button(
                onClick = onNavigateToLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(s.loginBtn, fontSize = 18.sp, fontWeight = FontWeight.SemiBold) // "Iniciar Sesión"
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- Botón: Registrarse (outlined) ---
            OutlinedButton(
                onClick = onNavigateToRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    s.registerBtn, // "Registrarse"
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Spacer(modifier = Modifier.height(24.dp))

            // --- Footer (información institucional) ---
            Text(
                text = "Universidad del Quindío · Ingeniería de Sistemas",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f), // Color secundario con 60% opacidad
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp)) // Espaciado final
        } // end Column
    } // end Box
} // end HomeScreen
