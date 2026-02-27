package com.turistgo.app.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.turistgo.app.ui.navigation.Screen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel()
) {
    val email by viewModel.email
    val password by viewModel.password
    val isLoading by viewModel.isLoading
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    
    var passwordVisible by remember { mutableStateOf(false) }
    var showVideoOverlay by remember { mutableStateOf(false) }
    
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbarMessage()
        }
    }

    // URL del logo principal y del logo de carga
    val imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1771997914/logo-turist_x5xgsq.png"
    val loadingLogoUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1771977314/turistgo-logo_evi36h.png"

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Logo o Video In-place
                Box(
                    modifier = Modifier.size(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    var isVideoReady by remember { mutableStateOf(false) }

                    // El video está presente si showVideoOverlay es true, pero oculto hasta que esté listo
                    if (showVideoOverlay) {
                        val videoUri = "android.resource://${LocalContext.current.packageName}/${com.turistgo.app.R.raw.video_login}"
                        Surface(
                            modifier = Modifier.size(140.dp),
                            shape = CircleShape,
                            color = Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
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

                    // La imagen se mantiene encima hasta que el video esté listo para reproducirse
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

                Text(
                    text = "TuristGo",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Descubre tu próxima aventura",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Formulario
                OutlinedTextField(
                    value = email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = { Text("Correo electrónico") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }, enabled = !isLoading) {
                            Icon(imageVector = image, contentDescription = null)
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "¿Olvidaste la contraseña?",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { 
                            if (!isLoading) navController.navigate(Screen.ForgotPassword.route)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { 
                        viewModel.login { isAdmin ->
                            if (isAdmin) {
                                navController.navigate(Screen.ModeratorDashboard.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            } else {
                                navController.navigate(Screen.Feed.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = MaterialTheme.shapes.medium,
                    enabled = !isLoading
                ) {
                    Text("Iniciar Sesión", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("¿No tienes cuenta?", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
                    TextButton(onClick = { navController.navigate(Screen.Register.route) }, enabled = !isLoading) {
                        Text("Regístrate", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Divisor
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                    Text(
                        text = "O continúa con",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botones de Redes Sociales
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    SocialLoginCard(
                        iconUrl = "https://cdn-icons-png.flaticon.com/512/300/300221.png",
                        contentDescription = "Google",
                        enabled = !isLoading
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    SocialLoginCard(
                        iconUrl = "https://cdn-icons-png.flaticon.com/512/5968/5968764.png",
                        contentDescription = "Facebook",
                        enabled = !isLoading
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    SocialLoginCard(
                        iconUrl = "https://cdn-icons-png.flaticon.com/512/3536/3536505.png",
                        contentDescription = "LinkedIn",
                        enabled = !isLoading
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Overlay de Carga (Iniciando sesión)
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.padding(32.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = loadingLogoUrl,
                                contentDescription = "Cargando",
                                modifier = Modifier.size(100.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Iniciando sesión...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth().clip(CircleShape),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InPlaceVideoPlayer(
    videoUrl: String, 
    onReady: () -> Unit = {},
    onFinished: () -> Unit
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
            playWhenReady = true
            
            addListener(object : androidx.media3.common.Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == androidx.media3.common.Player.STATE_READY) {
                        onReady()
                    }
                    if (state == androidx.media3.common.Player.STATE_ENDED) {
                        onFinished()
                    }
                }
            })
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                player = exoPlayer
                useController = false
                // Remove black shutter background for instant transition
                setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                // Zoom mode to fill the circle container
                resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .clip(CircleShape)
    )
}

@Composable
fun SocialLoginCard(iconUrl: String, contentDescription: String, enabled: Boolean = true) {
    Surface(
        modifier = Modifier
            .size(56.dp)
            .clickable(enabled = enabled) { /* Social Login */ },
        shape = CircleShape,
        color = Color.White,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box(contentAlignment = Alignment.Center) {
            AsyncImage(
                model = iconUrl,
                contentDescription = contentDescription,
                modifier = Modifier.size(30.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}
