// Paquete donde se encuentra esta pantalla de registro
package com.turistgo.app.features.auth

// Importaciones de animaciones de Compose
import androidx.compose.animation.AnimatedVisibility  // Para animar la visibilidad del loading overlay
import androidx.compose.animation.fadeIn             // Animación de aparición gradual
import androidx.compose.animation.fadeOut            // Animación de desaparición gradual

// Importaciones de fundamentos de UI
import androidx.compose.foundation.background         // Para establecer fondos
import androidx.compose.foundation.layout.*           // Layouts (Column, Row, Box, Spacer, etc)
import androidx.compose.foundation.rememberScrollState // Recuerda la posición del scroll
import androidx.compose.foundation.shape.CircleShape  // Forma circular para bordes
import androidx.compose.foundation.shape.RoundedCornerShape // Bordes redondeados
import androidx.compose.foundation.text.KeyboardOptions // Configuración del teclado (tipo, acciones)
import androidx.compose.foundation.verticalScroll     // Permite scroll vertical

// Importaciones de íconos Material
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*        // Íconos: Person, DateRange, Language, etc

// Importaciones de Material Design 3
import androidx.compose.material3.*                   // Componentes Material 3

// Importaciones de runtime y estado
import androidx.compose.runtime.*                     // States, remember, LaunchedEffect

// Importaciones de UI core
import androidx.compose.ui.Alignment                  // Alineación de componentes
import androidx.compose.ui.Modifier                   // Modificador de propiedades UI
import androidx.compose.ui.draw.clip                  // Recorta con una forma específica
import androidx.compose.ui.graphics.Color             // Manejo de colores
import androidx.compose.ui.layout.ContentScale        // Escala de contenido en imágenes
import androidx.compose.ui.text.font.FontWeight       // Pesos de fuente (bold, light, etc)
import androidx.compose.ui.text.input.KeyboardType    // Tipos de teclado (email, number, phone)
import androidx.compose.ui.text.input.PasswordVisualTransformation  // Oculta caracteres de contraseña
import androidx.compose.ui.text.input.VisualTransformation         // Transformación visual de texto
import androidx.compose.ui.unit.dp                    // Unidades de densidad independiente
import androidx.compose.ui.unit.sp                    // Unidades escalables para texto

// Importaciones de ViewModel y navegación
import androidx.lifecycle.viewmodel.compose.viewModel  // Para obtener ViewModel en Compose
import androidx.navigation.NavController               // Controlador de navegación (no usado directamente)

// Importaciones del proyecto
import com.turistgo.app.core.navigation.MainRoutes    // Rutas de navegación de la app
import coil.compose.AsyncImage                        // Carga asíncrona de imágenes (Coil)

/**
 * RegisterScreen - Pantalla de registro de nuevos usuarios
 * 
 * Permite a los usuarios crear una cuenta en TuristGo con los siguientes campos:
 * - Nombre, Apellidos, Edad, País, Ciudad, Teléfono, Email, Contraseña
 * 
 * Características:
 * - Validación de campos en tiempo real
 * - Dropdowns dinámicos (País → Ciudad)
 * - Extensión telefónica seleccionable
 * - Vista de carga durante el registro
 * - Manejo de errores con Snackbar
 */
@OptIn(ExperimentalMaterial3Api::class)  // Permite usar APIs experimentales de Material 3 (ExposedDropdownMenuBox)
@Composable
fun RegisterScreen(
    // Callback que se ejecuta al navegar al Feed después del registro exitoso
    onNavigateToFeed: () -> Unit,
    // Callback para volver a la pantalla de Login
    onBack: () -> Unit,
    // ViewModel que maneja la lógica de negocio del registro
    viewModel: RegisterViewModel = viewModel()
) {
    // ==================== OBSERVACIÓN DE ESTADOS DEL VIEWMODEL ====================
    
    // Datos personales del usuario
    val name by viewModel.name                       // Nombre
    val lastName by viewModel.lastName               // Apellidos
    val age by viewModel.age                         // Edad
    val country by viewModel.country                 // País seleccionado
    val city by viewModel.city                       // Ciudad seleccionada
    val availableCities by viewModel.availableCities // Lista de ciudades del país seleccionado
    
    // Datos de contacto
    val phoneExtension by viewModel.phoneExtension   // Prefijo telefónico (+34, +1, etc)
    val phone by viewModel.phone                     // Número de teléfono
    
    // Credenciales
    val email by viewModel.email                     // Correo electrónico
    val password by viewModel.password               // Contraseña
    val confirmPassword by viewModel.confirmPassword // Confirmación de contraseña
    
    // Estados de UI
    val isLoading by viewModel.isLoading             // Estado de carga (true = registrando)
    val snackbarMessage by viewModel.snackbarMessage.collectAsState() // Mensajes de error/éxito

    // ==================== ESTADOS LOCALES DEL COMPOSABLE ====================
    
    // Controlan la expansión de los dropdowns
    var countryExpanded by remember { mutableStateOf(false) }  // Dropdown de países
    var cityExpanded by remember { mutableStateOf(false) }     // Dropdown de ciudades
    var phoneExpanded by remember { mutableStateOf(false) }    // Dropdown de extensiones telefónicas
    
    // Controlan la visibilidad de las contraseñas
    var passwordVisible by remember { mutableStateOf(false) }        // Mostrar/ocultar contraseña
    var confirmPasswordVisible by remember { mutableStateOf(false) } // Mostrar/ocultar confirmación
    
    // Estado del Snackbar para notificaciones
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Estado del scroll (recuerda posición al rotar)
    val scrollState = rememberScrollState()

    // ==================== EFECTOS SECUNDARIOS ====================
    
    /**
     * LaunchedEffect: Se ejecuta cada vez que cambia snackbarMessage
     * Muestra el mensaje en el Snackbar y luego lo limpia
     */
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)  // Muestra la notificación
            viewModel.clearSnackbarMessage()    // Limpia el mensaje para que no se repita
        }
    }

    // URL del logo que se muestra durante el loading
    val logoUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1771977314/turistgo-logo_evi36h.png"

    // ==================== ESTRUCTURA PRINCIPAL ====================
    
    // Scaffold: proporciona estructura base con Snackbar
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }  // Host para notificaciones
    ) { padding ->  // padding: espacio para evitar que el contenido quede debajo del Snackbar
        // Box: contenedor que permite superponer elementos (el loading overlay)
        Box(
            modifier = Modifier
                .fillMaxSize()              // Ocupa toda la pantalla
                .padding(padding)           // Aplica el padding del Scaffold
        ) {
            // ==================== CONTENIDO PRINCIPAL (FORMULARIO) ====================
            Column(
                modifier = Modifier
                    .fillMaxSize()                              // Ocupa todo el espacio
                    .background(MaterialTheme.colorScheme.background)  // Fondo del tema
                    .verticalScroll(scrollState)                // Permite scroll vertical
                    .padding(24.dp),                            // Padding interno
                horizontalAlignment = Alignment.CenterHorizontally  // Centra horizontalmente
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // --- TÍTULOS DE LA PANTALLA ---
                Text(
                    text = "Crear Cuenta",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Únete a la comunidad TuristGo",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // --- CAMPO: NOMBRE ---
                OutlinedTextField(
                    value = name,
                    onValueChange = { viewModel.onNameChange(it) },  // Actualiza en ViewModel
                    label = { Text("Nombre") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true,          // Una sola línea
                    enabled = !isLoading        // Deshabilitado durante registro
                )

                Spacer(modifier = Modifier.height(12.dp))

                // --- CAMPO: APELLIDOS ---
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { viewModel.onLastNameChange(it) },
                    label = { Text("Apellidos") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                // --- FILA: EDAD + PAÍS (Dropdown) ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)  // Espacio entre elementos
                ) {
                    // EDAD (campo numérico)
                    OutlinedTextField(
                        value = age,
                        onValueChange = { viewModel.onAgeChange(it) },
                        label = { Text("Edad") },
                        leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                        modifier = Modifier.weight(1f),  // Ocupa la mitad del espacio
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), // Teclado numérico
                        enabled = !isLoading
                    )
                    
                    // PAÍS (Dropdown con lista predefinida)
                    ExposedDropdownMenuBox(
                        expanded = countryExpanded,
                        onExpandedChange = { if (!isLoading) countryExpanded = !countryExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = country,
                            onValueChange = {},  // Solo lectura, se cambia desde el dropdown
                            readOnly = true,     // No se puede editar directamente
                            label = { Text("País") },
                            leadingIcon = { Icon(Icons.Default.Language, contentDescription = null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = countryExpanded) },
                            modifier = Modifier.menuAnchor(),  // Ancla para el menú desplegable
                            shape = MaterialTheme.shapes.medium,
                            enabled = !isLoading,
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        // Menú desplegable con lista de países
                        ExposedDropdownMenu(
                            expanded = countryExpanded,
                            onDismissRequest = { countryExpanded = false }
                        ) {
                            viewModel.countries.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        viewModel.onCountryChange(selectionOption)  // Actualiza país
                                        countryExpanded = false  // Cierra el dropdown
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // --- CAMPO: CIUDAD (Dropdown dinámico basado en país) ---
                ExposedDropdownMenuBox(
                    expanded = cityExpanded,
                    onExpandedChange = { 
                        // Solo se puede expandir si hay un país seleccionado y no está cargando
                        if (!isLoading && country.isNotEmpty()) cityExpanded = !cityExpanded 
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Ciudad") },
                        leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded) },
                        modifier = Modifier.menuAnchor(),
                        shape = MaterialTheme.shapes.medium,
                        enabled = !isLoading && country.isNotEmpty(),  // Habilitado solo si hay país
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        placeholder = { 
                            if (country.isEmpty()) Text("Selecciona primero un país")  // Mensaje contextual
                        }
                    )
                    // Menú de ciudades (solo se muestra si hay ciudades disponibles)
                    if (availableCities.isNotEmpty()) {
                        ExposedDropdownMenu(
                            expanded = cityExpanded,
                            onDismissRequest = { cityExpanded = false }
                        ) {
                            availableCities.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        viewModel.onCityChange(selectionOption)
                                        cityExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // --- FILA: EXTENSIÓN TELEFÓNICA + NÚMERO ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // EXTENSIÓN TELEFÓNICA (Dropdown)
                    ExposedDropdownMenuBox(
                        expanded = phoneExpanded,
                        onExpandedChange = { if (!isLoading) phoneExpanded = !phoneExpanded },
                        modifier = Modifier.width(100.dp)  // Ancho fijo para la extensión
                    ) {
                        OutlinedTextField(
                            value = phoneExtension,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.menuAnchor(),
                            shape = MaterialTheme.shapes.medium,
                            enabled = !isLoading,
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            singleLine = true
                        )
                        // Menú de extensiones (+34, +1, +52, etc)
                        ExposedDropdownMenu(
                            expanded = phoneExpanded,
                            onDismissRequest = { phoneExpanded = false }
                        ) {
                            viewModel.phoneExtensions.forEach { ext ->
                                DropdownMenuItem(
                                    text = { Text(ext) },
                                    onClick = {
                                        viewModel.onPhoneExtensionChange(ext)
                                        phoneExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // NÚMERO DE TELÉFONO
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { viewModel.onPhoneChange(it) },
                        label = { Text("Teléfono") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        modifier = Modifier.weight(1f),  // Ocupa el espacio restante
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),  // Teclado telefónico
                        enabled = !isLoading
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // --- CAMPO: CORREO ELECTRÓNICO ---
                OutlinedTextField(
                    value = email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = { Text("Correo electrónico") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),  // Teclado con @
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                // --- CAMPO: CONTRASEÑA ---
                OutlinedTextField(
                    value = password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        // Botón para mostrar/ocultar contraseña
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

                Spacer(modifier = Modifier.height(12.dp))

                // --- CAMPO: CONFIRMAR CONTRASEÑA ---
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { viewModel.onConfirmPasswordChange(it) },
                    label = { Text("Confirmar contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        val image = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }, enabled = !isLoading) {
                            Icon(imageVector = image, contentDescription = null)
                        }
                    },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(24.dp))

                // --- BOTÓN DE REGISTRO ---
                Button(
                    onClick = {
                        // Llama al registro del ViewModel
                        viewModel.register {
                            onNavigateToFeed()  // Navega al Feed después del registro exitoso
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium,
                    enabled = !isLoading  // Deshabilitado mientras se registra
                ) {
                    Text("Empezar ahora", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- ENLACE PARA VOLVER AL LOGIN ---
                Row(
                    modifier = Modifier.padding(bottom = 32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("¿Ya tienes cuenta?", color = MaterialTheme.colorScheme.secondary)
                    TextButton(onClick = onBack, enabled = !isLoading) {
                        Text("Inicia sesión", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }  // Fin del Column (contenido principal)

            // ==================== OVERLAY DE CARGA ====================
            // Se muestra cuando isLoading = true
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),   // Animación de entrada: aparecer gradualmente
                exit = fadeOut()    // Animación de salida: desaparecer gradualmente
            ) {
                // Box semi-transparente que cubre toda la pantalla
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),  // Fondo negro 70% opaco
                    contentAlignment = Alignment.Center
                ) {
                    // Tarjeta con el indicador de carga
                    Card(
                        modifier = Modifier.padding(32.dp),
                        shape = RoundedCornerShape(24.dp),  // Bordes redondeados
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Logo animado durante la carga
                            AsyncImage(
                                model = logoUrl,
                                contentDescription = "Cargando",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape),  // Forma circular
                                contentScale = ContentScale.Crop  // Recorta para llenar el círculo
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Creando cuenta...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            // Barra de progreso lineal (indeterminada)
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(CircleShape),  // Bordes redondeados
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        }
                    }
                }
            }  // Fin de AnimatedVisibility
        }  // Fin del Box
    }  // Fin del Scaffold
}  // Fin de RegisterScreen
