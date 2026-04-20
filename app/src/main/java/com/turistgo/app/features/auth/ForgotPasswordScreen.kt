// Declara el paquete donde se encuentra esta pantalla dentro de la estructura de la app.
package com.turistgo.app.features.auth

// Importaciones de Jetpack Compose para la UI
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Flecha con soporte RTL
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel // Para obtener ViewModel (sin Hilt)
// Import de NavController omitido

/**
 * Pantalla para solicitar restablecimiento de contraseña.
 * 
 * Esta pantalla permite al usuario ingresar su correo electrónico
 * para recibir un código de verificación y poder restablecer su contraseña.
 * 
 * Buenas prácticas implementadas:
 * - UI limpia y enfocada en una sola acción
 * - Manejo de Snackbar para feedback
 * - Navegación clara hacia atrás y hacia adelante
 * - Textos descriptivos para guiar al usuario
 * - Diseño centrado en Mobile First
 * 
 * @param navController Controlador de navegación para manejar transiciones (documentado pero no usado)
 * @param viewModel ViewModel que maneja la lógica de negocio
 */
@OptIn(ExperimentalMaterial3Api::class)
// Declara la función composable principal de la pantalla de recuperación de contraseña
@Composable
fun ForgotPasswordScreen(
    onNavigateToResetPassword: () -> Unit, // Callback para navegar a la pantalla de restablecer contraseña
    onBack: () -> Unit, // Callback para volver a la pantalla anterior (login)
    viewModel: ForgotPasswordViewModel = viewModel() // ViewModel obtenido (no Hilt, ciclo de vida ligado a la pantalla)
) {
    // ==================== ESTADO DEL VIEWMODEL ====================
    // Observamos los estados del ViewModel que necesitamos en la UI
    val email by viewModel.email // Correo electrónico ingresado por el usuario
    val snackbarMessage by viewModel.snackbarMessage.collectAsState() // Mensajes de error/éxito
    
    // ==================== ESTADO LOCAL DE UI ====================
    // SnackbarHostState es específico de la UI, no necesita estar en ViewModel
    val snackbarHostState = remember { SnackbarHostState() }

    // ==================== EFECTOS SECUNDARIOS ====================
    // LaunchedEffect para mostrar mensajes del Snackbar cuando cambian
    // Esto asegura que cada mensaje se muestre una sola vez
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message) // Muestra el mensaje en la parte inferior
            viewModel.clearSnackbarMessage() // Limpiamos después de mostrar para evitar duplicados
        }
    }

    // ==================== ESTRUCTURA PRINCIPAL ====================
    // Scaffold proporciona la estructura base con soporte para TopBar y Snackbar
    Scaffold(
        // Host para mostrar mensajes Snackbar
        snackbarHost = { SnackbarHost(snackbarHostState) },
        
        // Barra superior con navegación
        topBar = {
            TopAppBar(
                title = { Text("Recuperar Contraseña") }, // Título de la pantalla
                navigationIcon = {
                    IconButton(
                        onClick = { 
                            // Navegación hacia atrás segura
                            onBack()
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás" // Descripción para accesibilidad (TalkBack)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent, // Fondo transparente (se ve el fondo de la pantalla)
                    titleContentColor = MaterialTheme.colorScheme.primary // Título en color primario
                )
            )
        }
    ) { paddingValues -> // padding interno para evitar la top bar
        // ==================== CONTENIDO PRINCIPAL ====================
        // Columna centrada vertical y horizontalmente
        Column(
            modifier = Modifier
                .fillMaxSize() // Ocupa todo el espacio disponible
                .padding(paddingValues) // Padding del Scaffold (evita la top bar)
                .background(MaterialTheme.colorScheme.background) // Fondo del tema actual
                .padding(24.dp), // Padding interno de 24dp para separación de bordes
            horizontalAlignment = Alignment.CenterHorizontally, // Centra horizontalmente los elementos
            verticalArrangement = Arrangement.Center // Centra verticalmente los elementos (en medio de la pantalla)
        ) {
            // ==================== ENCABEZADOS ====================
            // Título principal con estilo amigable y tranquilizador
            Text(
                text = "¡No te preocupes!", // Mensaje empático para reducir ansiedad
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Texto descriptivo que explica el proceso al usuario
            Text(
                text = "Ingresa tu correo y te enviaremos un código para restablecer tu contraseña.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(vertical = 16.dp), // Padding vertical
                textAlign = TextAlign.Center // Centrado para mejor legibilidad en móviles
            )

            // ==================== CAMPO DE CORREO ====================
            // Campo de texto para ingresar el email
            // Características:
            // - Ícono de email para mejor reconocimiento visual
            // - Material Design 3 OutlinedTextField (estilo contorneado)
            // - Single line para evitar expansión vertical
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.onEmailChange(it) }, // Actualiza el email en el ViewModel
                label = { Text("Correo electrónico") }, // Etiqueta flotante
                leadingIcon = { 
                    Icon(
                        Icons.Default.Email, 
                        contentDescription = null // Decorativo, no necesita descripción para accesibilidad
                    ) 
                },
                modifier = Modifier.fillMaxWidth(), // Ocupa todo el ancho disponible
                shape = MaterialTheme.shapes.medium, // Bordes redondeados estándar
                singleLine = true // Una sola línea (evita saltos de línea)
            )

            // Espaciado entre campo y botón
            Spacer(modifier = Modifier.height(24.dp))

            // ==================== BOTÓN DE ACCIÓN ====================
            // Botón principal para enviar el correo de recuperación
            Button(
                onClick = {
                    // Llama al ViewModel para enviar el correo de restablecimiento
                    // El callback se ejecuta cuando la operación es exitosa
                    viewModel.sendPasswordReset {
                        onNavigateToResetPassword() // Navega a la pantalla de restablecer contraseña
                    }
                },
                modifier = Modifier
                    .fillMaxWidth() // Ocupa todo el ancho
                    .height(48.dp), // Altura estándar de 48dp para botones (accesible para toque)
                shape = MaterialTheme.shapes.medium // Consistencia visual con el campo de texto
            ) {
                Text(
                    "Enviar código", 
                    fontSize = 16.sp, 
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Nota: No se incluye indicador de carga porque la operación
            // de envío de email suele ser rápida y el ViewModel correspondiente
            // no implementa isLoading. Si se necesita, se debería agregar.
        }
    }
}
