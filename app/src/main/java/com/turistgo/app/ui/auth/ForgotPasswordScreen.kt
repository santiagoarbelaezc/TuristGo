package com.turistgo.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.turistgo.app.ui.navigation.Screen

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
 * @param navController Controlador de navegación para manejar transiciones
 * @param viewModel ViewModel que maneja la lógica de negocio
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: ForgotPasswordViewModel = viewModel()
) {
    // ==================== ESTADO DEL VIEWMODEL ====================
    // Observamos los estados del ViewModel que necesitamos en la UI
    val email by viewModel.email
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    
    // ==================== ESTADO LOCAL DE UI ====================
    // SnackbarHostState es específico de la UI, no necesita estar en ViewModel
    val snackbarHostState = remember { SnackbarHostState() }

    // ==================== EFECTOS SECUNDARIOS ====================
    // LaunchedEffect para mostrar mensajes del Snackbar cuando cambian
    // Esto asegura que cada mensaje se muestre una sola vez
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbarMessage() // Limpiamos después de mostrar
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
                title = { Text("Recuperar Contraseña") },
                navigationIcon = {
                    IconButton(
                        onClick = { 
                            // Navegación hacia atrás segura
                            navController.popBackStack() 
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás" // Accesibilidad
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent, // Fondo transparente
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        // ==================== CONTENIDO PRINCIPAL ====================
        // Columna centrada vertical y horizontalmente
        Column(
            modifier = Modifier
                .fillMaxSize() // Ocupa todo el espacio disponible
                .padding(paddingValues) // Padding del Scaffold
                .background(MaterialTheme.colorScheme.background) // Fondo del tema
                .padding(24.dp), // Padding interno para separación
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Centrado vertical
        ) {
            // ==================== ENCABEZADOS ====================
            // Título principal con estilo amigable
            Text(
                text = "¡No te preocupes!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Texto descriptivo que explica el proceso
            Text(
                text = "Ingresa tu correo y te enviaremos un código para restablecer tu contraseña.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(vertical = 16.dp),
                textAlign = TextAlign.Center // Mejor legibilidad en móviles
            )

            // ==================== CAMPO DE CORREO ====================
            // Campo de texto para ingresar el email
            // Características:
            // - Ícono de email para mejor reconocimiento
            // - Material Design 3 OutlinedTextField
            // - Single line para evitar expansión
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Correo electrónico") },
                leadingIcon = { 
                    Icon(
                        Icons.Default.Email, 
                        contentDescription = null // Decorativo, no necesita descripción
                    ) 
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium, // Bordes redondeados
                singleLine = true
            )

            // Espaciado entre campo y botón
            Spacer(modifier = Modifier.height(24.dp))

            // ==================== BOTÓN DE ACCIÓN ====================
            // Botón principal para enviar el correo
            Button(
                onClick = {
                    // Callback que maneja la navegación post-éxito
                    viewModel.sendPasswordReset {
                        // Navega a la pantalla de restablecimiento
                        // No necesita popUpTo porque ForgotPassword debe permanecer
                        // en la pila para poder volver
                        navController.navigate(Screen.ResetPassword.route)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp), // Altura estándar para botones
                shape = MaterialTheme.shapes.medium // Consistencia visual
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
