package com.turistgo.app.features.home

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
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.turistgo.app.core.components.InPlaceVideoPlayer
import com.turistgo.app.core.navigation.MainRoutes
import coil.compose.AsyncImage
import com.turistgo.app.core.locale.AppStrings
import com.turistgo.app.core.locale.LanguageState
import com.turistgo.app.core.locale.AppLanguage
import com.turistgo.app.features.auth.LoginViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.turistgo.app.core.components.SocialLoginCard

@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToFeed: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {

    val lang by LanguageState.current
    val s = AppStrings.get(lang)
    val scrollState = rememberScrollState()

    // Mismas URLs que LoginScreen
    val imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1771997914/logo-turist_x5xgsq.png"

    var showVideoOverlay by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),  // Removido padding vertical
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
                // Language Toggle at the top right
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        LanguageState.current.value = if (lang == AppLanguage.SPANISH) AppLanguage.ENGLISH else AppLanguage.SPANISH
                    }) {
                        Text(
                            text = if (lang == AppLanguage.SPANISH) "EN" else "ES", 
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                // Logo — mismo comportamiento que LoginScreen (clic activa video)
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    var isVideoReady by remember { mutableStateOf(false) }

                    if (showVideoOverlay) {
                        val videoUri = "android.resource://${LocalContext.current.packageName}/${com.turistgo.app.R.raw.video_login}"
                        Surface(
                            modifier = Modifier.size(120.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.background,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                        ) {
                            InPlaceVideoPlayer(
                                videoUrl = videoUri,
                                onReady = { isVideoReady = true },
                                onFinished = {
                                    showVideoOverlay = false
                                    isVideoReady = false
                                }
                            )
                        }
                    }

                    // La imagen se oculta instantáneamente cuando el video está listo
                    if (!showVideoOverlay || !isVideoReady) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Logo de TuristGo",
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    showVideoOverlay = true
                                },
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Título — mismo estilo que LoginScreen
                Text(
                    text = "TuristGo",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = s.discoverNextAdventure,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Descripción de la app — card con el mismo estilo surface
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = s.appDescription,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botón Iniciar Sesión — mismo estilo que LoginScreen
                Button(
                    onClick = onNavigateToLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(s.loginBtn, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onNavigateToRegister,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        s.registerBtn,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Spacer(modifier = Modifier.height(24.dp))

                // Footer
                Text(
                    text = "Universidad del Quindío · Ingeniería de Sistemas",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))
        }   // end Column
    }   // end Box
}   // end HomeScreen
