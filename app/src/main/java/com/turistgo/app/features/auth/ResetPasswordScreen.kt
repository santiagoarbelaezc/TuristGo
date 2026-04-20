package com.turistgo.app.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turistgo.app.core.components.TuristGoDialog
import androidx.hilt.navigation.compose.hiltViewModel
// NavController import omitted

/**
 * Pantalla para restablecer la contraseña mediante código de verificación.
 * 
 * Buenas prácticas implementadas:
 * - Separación de responsabilidades (UI en Composable, lógica en ViewModel)
 * - Manejo de estados de carga y errores
 * - Feedback visual al usuario
 * - Navegación limpia con popUpTo
 * - Prevención de interacciones durante carga
 * - Manejo de eventos con LaunchedEffect
 * 
 * @param navController Controlador de navegación para manejar la transición entre pantallas
 * @param viewModel ViewModel que contiene la lógica de negocio para restablecer contraseña
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    onNavigateToLogin: () -> Unit,
    onBack: () -> Unit,
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    // ==================== ESTADO DEL VIEWMODEL ====================
    // Delegación para observar cambios en tiempo real
    val code by viewModel.code
    val newPassword by viewModel.newPassword
    val confirmPassword by viewModel.confirmPassword
    val isLoading by viewModel.isLoading
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val alertState by viewModel.alertState.collectAsState()
    
    // ==================== ESTADO LOCAL DE UI ====================
    // Estados que solo afectan la UI, no la lógica de negocio
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    
    // Estados para controlar visibilidad de contraseñas
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // ==================== EFECTOS SECUNDARIOS ====================
    // LaunchedEffect para manejar eventos de Snackbar de manera reactiva
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbarMessage() // Limpiar mensaje después de mostrarlo
        }
    }

    // Modal de Alertas Premium
    TuristGoDialog(
        state = alertState,
        onDismiss = { viewModel.dismissAlert() }
    )

    // ==================== ESTRUCTURA UI ====================
    // Scaffold proporciona la estructura base con soporte para Snackbar y TopBar
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            // TopAppBar personalizado con navegación hacia atrás
            TopAppBar(
                title = { Text("Nueva Contraseña") },
                navigationIcon = {
                    IconButton(
                        onClick = { 
                            // Navegación segura hacia atrás
                            onBack()
                        },
                        enabled = !isLoading // Deshabilitado durante carga
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Atrás"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        // Contenido principal con scroll y padding
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Padding del Scaffold
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState) // Scroll para pantallas pequeñas
                .padding(24.dp), // Padding interno
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Espaciado superior
            Spacer(modifier = Modifier.height(16.dp))

            // ==================== ENCABEZADOS ====================
            // Título principal con estilos de Material Design
            Text(
                text = "Restablecer Contraseña",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Texto descriptivo para guiar al usuario
            Text(
                text = "Ingresa el código que recibiste en tu correo y define tu nueva contraseña.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(vertical = 16.dp),
                textAlign = TextAlign.Center
            )

            // ==================== CAMPO CÓDIGO ====================
            // Campo para código de verificación con ícono
            OutlinedTextField(
                value = code,
                onValueChange = { viewModel.onCodeChange(it) },
                label = { Text("Código de verificación") },
                leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null) },
                placeholder = { Text("Ej. 123456") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                singleLine = true,
                enabled = !isLoading // Deshabilitado durante carga
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ==================== CAMPO NUEVA CONTRASEÑA ====================
            // Campo con toggle de visibilidad para mejor UX
            OutlinedTextField(
                value = newPassword,
                onValueChange = { viewModel.onNewPasswordChange(it) },
                label = { Text("Nueva Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    // Toggle de visibilidad con estado local
                    val image = if (passwordVisible) 
                        Icons.Default.Visibility 
                    else 
                        Icons.Default.VisibilityOff
                    
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                        enabled = !isLoading
                    ) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                },
                // Transformación visual según visibilidad
                visualTransformation = if (passwordVisible) 
                    VisualTransformation.None 
                else 
                    PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ==================== CAMPO CONFIRMAR CONTRASEÑA ====================
            // Similar al anterior pero para confirmación
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { viewModel.onConfirmPasswordChange(it) },
                label = { Text("Confirmar Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    val image = if (confirmPasswordVisible) 
                        Icons.Default.Visibility 
                    else 
                        Icons.Default.VisibilityOff
                    
                    IconButton(
                        onClick = { confirmPasswordVisible = !confirmPasswordVisible },
                        enabled = !isLoading
                    ) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                },
                visualTransformation = if (confirmPasswordVisible) 
                    VisualTransformation.None 
                else 
                    PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ==================== BOTÓN PRINCIPAL ====================
            // Botón de acción principal con estado de carga
            Button(
                onClick = {
                    // Callback que maneja la navegación post-éxito
                    viewModel.resetPassword {
                        onNavigateToLogin()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = !isLoading // Deshabilitado durante carga
            ) {
                // Mostrar indicador de carga o texto según estado
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Actualizar Contraseña", 
                        fontSize = 16.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==================== ENLACE SECUNDARIO ====================
            // Opción alternativa para volver al login
            TextButton(
                onClick = onNavigateToLogin,
                enabled = !isLoading
            ) {
                Text(
                    "Volver al inicio de sesión", 
                    color = MaterialTheme.colorScheme.primary, 
                    fontWeight = FontWeight.Bold
                )
            }

            // Espaciado inferior
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
