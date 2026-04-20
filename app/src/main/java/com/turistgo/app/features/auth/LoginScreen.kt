// Paquete donde se encuentra esta pantalla de autenticación
package com.turistgo.app.features.auth

// Importaciones de animaciones de Compose
import androidx.compose.animation.AnimatedVisibility  // Para animar visibilidad de componentes
import androidx.compose.animation.fadeIn             // Animación de aparición gradual
import androidx.compose.animation.fadeOut            // Animación de desaparición gradual

// Importaciones de fundamentos de UI
import androidx.compose.foundation.background         // Para establecer fondos
import androidx.compose.foundation.clickable          // Hace que un componente sea clickeable
import androidx.compose.foundation.interaction.MutableInteractionSource  // Para manejar interacciones táctiles
import androidx.compose.foundation.layout.*           // Layouts como Column, Row, Box, Spacer
import androidx.compose.foundation.rememberScrollState // Recuerda la posición del scroll
import androidx.compose.foundation.shape.CircleShape  // Forma circular para bordes
import androidx.compose.foundation.shape.RoundedCornerShape  // Bordes redondeados
import androidx.compose.foundation.verticalScroll     // Permite scroll vertical

// Importaciones de Material Design 3
import androidx.compose.material3.*                   // Componentes Material 3

// Importaciones de runtime y estado
import androidx.compose.runtime.*                     // States, remember, LaunchedEffect

// Importaciones de UI core
import androidx.compose.ui.Alignment                  // Alineación de componentes
import androidx.compose.ui.Modifier                   // Modificador de propiedades UI
import androidx.compose.ui.draw.alpha                 // Control de transparencia
import androidx.compose.ui.draw.clip                  // Recorta con una forma específica
import androidx.compose.ui.graphics.Color             // Manejo de colores
import androidx.compose.ui.layout.ContentScale        // Escala de contenido en imágenes
import androidx.compose.ui.text.font.FontWeight       // Pesos de fuente (bold, light, etc)
import androidx.compose.ui.text.input.PasswordVisualTransformation  // Oculta caracteres de contraseña
import androidx.compose.ui.text.input.VisualTransformation         // Transformación visual de texto
import androidx.compose.ui.unit.dp                    // Unidades de densidad independiente (dp)
import androidx.compose.ui.unit.sp                    // Unidades escalables para texto (sp)

// Importaciones de ViewModel y contexto
import androidx.hilt.navigation.compose.hiltViewModel  // Para obtener ViewModel de Hilt en Compose
import androidx.compose.ui.platform.LocalContext       // Obtiene el contexto actual
import androidx.compose.ui.res.stringResource

// Importaciones para video
import androidx.compose.ui.viewinterop.AndroidView     // Permite usar vistas de Android en Compose
import androidx.media3.common.MediaItem                // Representa un item multimedia
import androidx.media3.exoplayer.ExoPlayer             // Reproductor de video ExoPlayer

// Importaciones de componentes personalizados
import com.turistgo.app.R
import com.turistgo.app.core.components.InPlaceVideoPlayer
import com.turistgo.app.core.components.SocialLoginCard
import com.turistgo.app.core.components.TuristGoDialog

// Importaciones de imágenes
import coil.compose.AsyncImage                       // Carga asíncrona de imágenes (Coil)

// Importaciones de íconos Material
import androidx.compose.material.icons.Icons           // Set de íconos de Material
import androidx.compose.material.icons.filled.Email    // Ícono de sobre/email
import androidx.compose.material.icons.filled.Lock     // Ícono de candado
import androidx.compose.material.icons.filled.Person   // Ícono de persona
import androidx.compose.material.icons.filled.Visibility      // Ícono de ojo (mostrar)
import androidx.compose.material.icons.filled.VisibilityOff   // Ícono de ojo tachado (ocultar)

// Anotación que indica que usamos APIs experimentales de Material 3
@OptIn(ExperimentalMaterial3Api::class)
// Composable principal: la pantalla de Login
@Composable
fun LoginScreen(
    // Callback que se ejecuta al navegar al Feed (para usuarios normales)
    onNavigateToFeed: () -> Unit,
    // Callback que se ejecuta al navegar al Dashboard (para administradores)
    onNavigateToDashboard: () -> Unit,
    // Callback que se ejecuta al navegar a la pantalla de registro
    onNavigateToRegister: () -> Unit,
    // Callback que se ejecuta al navegar a la pantalla de recuperación de contraseña
    onNavigateToForgotPassword: () -> Unit,
    // ViewModel que maneja la lógica de negocio (inyectado automáticamente)
    viewModel: LoginViewModel = hiltViewModel()
) {
    // --- OBSERVACIÓN DE ESTADOS DEL VIEWMODEL ---
    // Estado del campo email (observable)
    val email by viewModel.email
    // Estado del campo password (observable)
    val password by viewModel.password
    // Estado de carga (true = mostrando loading, false = idle)
    val isLoading by viewModel.isLoading
    // Mensaje para mostrar en Snackbar (notificación temporal)
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val alertState by viewModel.alertState.collectAsState()
    
    // --- ESTADOS LOCALES DEL COMPOSABLE ---
    // Controla si la contraseña es visible (true) u oculta con puntos (false)
    var passwordVisible by remember { mutableStateOf(false) }
    // Controla si se muestra el video overlay al hacer click en el logo
    var showVideoOverlay by remember { mutableStateOf(false) }
    
    // Estado para manejar el scroll de la pantalla (recuerda posición al rotar)
    val scrollState = rememberScrollState()
    // Estado para manejar el Snackbar (notificaciones)
    val snackbarHostState = remember { SnackbarHostState() }

    // Efecto que se ejecuta cada vez que cambia snackbarMessage
    LaunchedEffect(snackbarMessage) {
        // Si hay un mensaje, lo muestra en el Snackbar
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            // Limpia el mensaje del ViewModel para que no se muestre de nuevo
            viewModel.clearSnackbarMessage()
        }
    }

    // Modal de Alertas Premium
    TuristGoDialog(
        state = alertState,
        onDismiss = { viewModel.dismissAlert() }
    )

    // URLs de las imágenes (logo principal y logo de carga)
    val imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1771997914/logo-turist_x5xgsq.png"
    val loadingLogoUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1771977314/turistgo-logo_evi36h.png"

    // Scaffold: estructura base de Material Design (proporciona Snackbar, TopBar, etc)
    Scaffold(
        // Define el host para mostrar Snackbars (notificaciones)
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->  // padding: espacio para evitar que el contenido quede debajo de la Snackbar
        // Box: contenedor que puede superponer elementos
        Box(
            modifier = Modifier
                .fillMaxSize()  // Ocupa todo el espacio disponible
                .padding(padding)  // Aplica el padding del Scaffold
                .background(MaterialTheme.colorScheme.background)  // Fondo con color del tema
        ) {
            // Column: disposición vertical de elementos
            Column(
                modifier = Modifier
                    .fillMaxSize()  // Ocupa todo el espacio
                    .verticalScroll(scrollState)  // Permite scroll vertical
                    .padding(horizontal = 24.dp),  // Removido padding vertical
                horizontalAlignment = Alignment.CenterHorizontally,  // Centra horizontalmente
                verticalArrangement = Arrangement.Top  // Cambiado de Center a Top para quitar margenes
            ) {

                // --- SECCIÓN DEL LOGO / VIDEO ---
                // Box contenedor del logo (tamaño aumentado a 200dp para recuperar el estilo original)
                Box(
                    modifier = Modifier.size(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Estado local para saber si el video ya está listo para reproducirse
                    var isVideoReady by remember { mutableStateOf(false) }

                    // Si showVideoOverlay es true, muestra el video
                    if (showVideoOverlay) {
                        // URI del video almacenado en resources/raw
                        val videoUri = "android.resource://${LocalContext.current.packageName}/${R.raw.video_login}"
                        // Surface: contenedor con forma circular y borde
                        Surface(
                            modifier = Modifier.size(200.dp),
                            shape = CircleShape,  // Forma circular
                            color = MaterialTheme.colorScheme.background,  // Color de fondo
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))  // Borde semitransparente
                        ) {
                            // Componente personalizado que reproduce el video
                            InPlaceVideoPlayer(
                                videoUrl = videoUri,  // URL del video
                                onReady = { isVideoReady = true },  // Callback cuando el video está listo
                                onFinished = {  // Callback cuando el video termina
                                    showVideoOverlay = false  // Oculta el video
                                    isVideoReady = false  // Reinicia estado de "listo"
                                }
                            )
                        }
                    }

                    // Si no se muestra el video O el video no está listo, muestra el logo
                    if (!showVideoOverlay || !isVideoReady) {
                        // AsyncImage: carga imagen desde URL de forma asíncrona
                        AsyncImage(
                            model = imageUrl,  // URL de la imagen
                            contentDescription = stringResource(R.string.logo_description),  // Accesibilidad
                            modifier = Modifier
                                .fillMaxSize()  // Ocupa todo el espacio del contenedor
                                .clickable(  // Hace la imagen clickeable
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null  // Sin indicador visual de click
                                ) {
                                    showVideoOverlay = true  // Al hacer click, muestra el video
                                },
                            contentScale = ContentScale.Fit  // Escala la imagen para que quepa
                        )
                    }
                }

                // --- TÍTULOS (ESTILO ORIGINAL) ---
                Text(
                    text = stringResource(R.string.app_name),
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFE53935) // Rojo TuristGo
                )
                
                Text(
                    text = stringResource(R.string.discover_next_adventure),
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // --- CAMPO DE TEXTO: CORREO O USUARIO ---
                OutlinedTextField(
                    value = email,  // Valor actual del campo (reutilizamos variable 'email' para identidad)
                    onValueChange = { viewModel.onEmailChange(it) },  // Callback cuando cambia el texto
                    label = { Text("Correo o Usuario") },  // Etiqueta dual
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },  // Ícono de usuario
                    modifier = Modifier.fillMaxWidth(),  // Ancho completo
                    shape = MaterialTheme.shapes.medium,  // Bordes medianamente redondeados
                    singleLine = true,  // Una sola línea
                    enabled = !isLoading  // Deshabilitado si está cargando
                )

                Spacer(modifier = Modifier.height(12.dp))

                // --- CAMPO DE TEXTO: CONTRASEÑA ---
                OutlinedTextField(
                    value = password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text(stringResource(R.string.password_label)) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },  // Ícono de candado
                    trailingIcon = {  // Ícono al final (para mostrar/ocultar contraseña)
                        val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(
                            onClick = { passwordVisible = !passwordVisible },  // Alterna visibilidad
                            enabled = !isLoading
                        ) {
                            Icon(imageVector = image, contentDescription = null)
                        }
                    },
                    // Transformación visual: muestra puntos si está oculta, texto normal si es visible
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(8.dp))

                // --- CONSENTIMIENTO DE PRIVACIDAD ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = viewModel.isPrivacyAccepted.value,
                        onCheckedChange = { viewModel.onPrivacyAcceptanceChange(it) },
                        enabled = !isLoading
                    )
                    Text(
                        text = stringResource(R.string.accept_terms),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        lineHeight = 14.sp
                    )
                }

                // --- ENLACE "OLVIDASTE LA CONTRASEÑA" ---
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.forgot_password),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { 
                            if (!isLoading) onNavigateToForgotPassword()  // Navega si no está cargando
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // --- BOTÓN DE INICIO DE SESIÓN ---
                Button(
                    onClick = { 
                        // Llama al login del ViewModel, recibe un callback con el rol del usuario
                        viewModel.login { isAdmin ->
                            if (isAdmin) {
                                onNavigateToDashboard()  // Admin va al Dashboard
                            } else {
                                onNavigateToFeed()  // Usuario normal va al Feed
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),  // Altura fija
                    shape = MaterialTheme.shapes.medium,
                    enabled = !isLoading  // Deshabilitado mientras carga
                ) {
                    Text(stringResource(R.string.login_btn), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- ENLACE PARA REGISTRARSE ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.no_account), fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
                    TextButton(
                        onClick = onNavigateToRegister,
                        enabled = !isLoading
                    ) {
                        Text(stringResource(R.string.register_action), fontSize = 14.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- DIVISOR CON TEXTO "O continúa con" ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Línea divisoria izquierda
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                    // Texto central
                    Text(
                        text = stringResource(R.string.or_continue_with),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    // Línea divisoria derecha
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- BOTONES DE REDES SOCIALES ---
                val context = LocalContext.current
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center  // Centra los botones
                ) {
                    // Botón de Google
                    SocialLoginCard(
                        iconUrl = "https://cdn-icons-png.flaticon.com/512/300/300221.png",
                        contentDescription = "Google",
                        enabled = !isLoading,
                        onClick = { 
                            viewModel.loginWithSocial(context, "Google") { onNavigateToFeed() }
                        }
                    )
                    Spacer(modifier = Modifier.width(20.dp))  // Espacio entre botones
                    
                    // Botón de Facebook
                    SocialLoginCard(
                        iconUrl = "https://cdn-icons-png.flaticon.com/512/5968/5968764.png",
                        contentDescription = "Facebook",
                        enabled = !isLoading,
                        onClick = { 
                            viewModel.loginWithSocial(context, "Facebook") { onNavigateToFeed() }
                        }
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    
                    // Botón de LinkedIn
                    SocialLoginCard(
                        iconUrl = "https://cdn-icons-png.flaticon.com/512/3536/3536505.png",
                        contentDescription = "LinkedIn",
                        enabled = !isLoading,
                        onClick = { 
                            viewModel.loginWithSocial(context, "LinkedIn") { onNavigateToFeed() }
                        }
                    )
                }
            }

            // --- OVERLAY DE CARGA PREMIUM ---
            com.turistgo.app.core.components.LoadingOverlay(
                isLoading = isLoading,
                text = "Iniciando sesión..."
            )
        }
    }
}
