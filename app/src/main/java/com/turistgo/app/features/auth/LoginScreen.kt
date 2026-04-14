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
import androidx.lifecycle.viewmodel.compose.viewModel  // Para obtener ViewModel en Compose
import androidx.compose.ui.platform.LocalContext       // Obtiene el contexto actual

// Importaciones para video
import androidx.compose.ui.viewinterop.AndroidView     // Permite usar vistas de Android en Compose
import androidx.media3.common.MediaItem                // Representa un item multimedia
import androidx.media3.exoplayer.ExoPlayer             // Reproductor de video ExoPlayer

// Importaciones de componentes personalizados
import com.turistgo.app.core.components.InPlaceVideoPlayer  // Componente de video local
import com.turistgo.app.core.components.SocialLoginCard     // Tarjeta para login social

// Importaciones de imágenes
import coil.compose.AsyncImage                       // Carga asíncrona de imágenes (Coil)

// Importaciones de íconos Material
import androidx.compose.material.icons.Icons           // Set de íconos de Material
import androidx.compose.material.icons.filled.Email    // Ícono de sobre/email
import androidx.compose.material.icons.filled.Lock     // Ícono de candado
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
    viewModel: LoginViewModel = viewModel()
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
                    .padding(24.dp),  // Padding interno de 24dp
                horizontalAlignment = Alignment.CenterHorizontally,  // Centra horizontalmente
                verticalArrangement = Arrangement.Center  // Distribuye verticalmente centrado
            ) {
                // Espaciador vertical de 32dp
                Spacer(modifier = Modifier.height(32.dp))

                // --- SECCIÓN DEL LOGO / VIDEO ---
                // Box contenedor del logo (tamaño fijo 140dp)
                Box(
                    modifier = Modifier.size(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Estado local para saber si el video ya está listo para reproducirse
                    var isVideoReady by remember { mutableStateOf(false) }

                    // Si showVideoOverlay es true, muestra el video
                    if (showVideoOverlay) {
                        // URI del video almacenado en resources/raw
                        val videoUri = "android.resource://${LocalContext.current.packageName}/${com.turistgo.app.R.raw.video_login}"
                        // Surface: contenedor con forma circular y borde
                        Surface(
                            modifier = Modifier.size(140.dp),
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
                            contentDescription = "Logo de TuristGo",  // Accesibilidad
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

                // Espaciador vertical de 24dp
                Spacer(modifier = Modifier.height(24.dp))

                // --- TEXTO DEL TÍTULO PRINCIPAL ---
                Text(
                    text = "TuristGo",  // Texto a mostrar
                    fontSize = 28.sp,   // Tamaño de fuente 28sp
                    fontWeight = FontWeight.Bold,  // Negrita
                    color = MaterialTheme.colorScheme.primary  // Color primario del tema
                )
                // --- TEXTO DEL SUBTÍTULO ---
                Text(
                    text = "Descubre tu próxima aventura",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary,  // Color secundario del tema
                    modifier = Modifier.padding(bottom = 16.dp)  // Padding inferior
                )

                // --- CAMPO DE TEXTO: EMAIL ---
                OutlinedTextField(
                    value = email,  // Valor actual del campo
                    onValueChange = { viewModel.onEmailChange(it) },  // Callback cuando cambia el texto
                    label = { Text("Correo electrónico") },  // Etiqueta flotante
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },  // Ícono al inicio
                    modifier = Modifier.fillMaxWidth(),  // Ancho completo
                    shape = MaterialTheme.shapes.medium,  // Bordes medianamente redondeados
                    singleLine = true,  // Una sola línea
                    enabled = !isLoading  // Deshabilitado si está cargando
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- CAMPO DE TEXTO: CONTRASEÑA ---
                OutlinedTextField(
                    value = password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text("Contraseña") },
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

                // --- ENLACE "OLVIDASTE LA CONTRASEÑA" ---
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "¿Olvidaste la contraseña?",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { 
                            if (!isLoading) onNavigateToForgotPassword()  // Navega si no está cargando
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                    Text("Iniciar Sesión", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- ENLACE PARA REGISTRARSE ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("¿No tienes cuenta?", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
                    TextButton(
                        onClick = onNavigateToRegister,
                        enabled = !isLoading
                    ) {
                        Text("Regístrate", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- DIVISOR CON TEXTO "O continúa con" ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Línea divisoria izquierda
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                    // Texto central
                    Text(
                        text = "O continúa con",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    // Línea divisoria derecha
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- BOTONES DE REDES SOCIALES ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center  // Centra los botones
                ) {
                    // Botón de Google
                    SocialLoginCard(
                        iconUrl = "https://cdn-icons-png.flaticon.com/512/300/300221.png",
                        contentDescription = "Google",
                        enabled = !isLoading
                    )
                    Spacer(modifier = Modifier.width(20.dp))  // Espacio entre botones
                    
                    // Botón de Facebook
                    SocialLoginCard(
                        iconUrl = "https://cdn-icons-png.flaticon.com/512/5968/5968764.png",
                        contentDescription = "Facebook",
                        enabled = !isLoading
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    
                    // Botón de LinkedIn
                    SocialLoginCard(
                        iconUrl = "https://cdn-icons-png.flaticon.com/512/3536/3536505.png",
                        contentDescription = "LinkedIn",
                        enabled = !isLoading
                    )
                }
                
            } // Fin del Column
        } // Fin del Box
    } // Fin del Scaffold
} 
