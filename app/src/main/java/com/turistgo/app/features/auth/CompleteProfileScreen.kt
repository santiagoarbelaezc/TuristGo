// Declara el paquete donde se encuentra esta pantalla dentro de la estructura de la app.
package com.turistgo.app.features.auth

// Importaciones para manejo de URIs y selección de imágenes
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
// Importaciones de animaciones y UI
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel // Para inyectar ViewModel con Hilt
import coil.compose.AsyncImage // Para cargar imágenes
import androidx.compose.ui.res.stringResource
import com.turistgo.app.core.locale.LanguageState
import com.turistgo.app.R
import com.turistgo.app.core.locale.AppLanguage
import com.turistgo.app.core.components.LoadingOverlay // Componente de carga overlay

// Marca que se usan APIs experimentales de Material 3 y Layout
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
// Declara la función composable principal de la pantalla de completar perfil
@Composable
fun CompleteProfileScreen(
    userId: String, // ID del usuario que acaba de registrarse
    onNavigateToFeed: () -> Unit, // Callback para navegar al feed principal después de completar perfil
    viewModel: CompleteProfileViewModel = hiltViewModel() // ViewModel inyectado por Hilt
) {
    // Estados observados del ViewModel
    val username by viewModel.username // Nombre de usuario (ej: "@juanperez")
    val photoUri by viewModel.photoUri // URI de la foto de perfil seleccionada
    val interests by viewModel.interests // Lista de intereses seleccionados
    val selectedLanguage by viewModel.selectedLanguage // Idioma preferido del usuario
    val notificationsEnabled by viewModel.notificationsEnabled // Preferencia de notificaciones
    val isLoading by viewModel.isLoading // Estado de carga (guardando perfil)
    val snackbarMessage by viewModel.snackbarMessage.collectAsState() // Mensajes de error/info

    // Estado para mostrar snackbar (mensajes temporales)
    val snackbarHostState = remember { SnackbarHostState() }

    // Efecto para mostrar mensajes de snackbar cuando llegan del ViewModel
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it) // Muestra el mensaje
            viewModel.clearSnackbarMessage() // Limpia el mensaje en el ViewModel
        }
    }

    // Lanzador para seleccionar imagen de la galería
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent() // Contrato para obtener contenido (imágenes)
    ) { uri: Uri? ->
        uri?.let { viewModel.onPhotoUriChange(it.toString()) } // Actualiza la URI en el ViewModel
    }

    // Lista de intereses disponibles (traducidos según el idioma actual)
    val availableInterests = listOf(
        stringResource(R.string.interest_tourism),   // "Turismo"
        stringResource(R.string.interest_events),    // "Eventos"
        stringResource(R.string.interest_gastronomy), // "Gastronomía"
        stringResource(R.string.interest_adventure), // "Aventura"
        stringResource(R.string.interest_culture),   // "Cultura"
        stringResource(R.string.interest_nature),    // "Naturaleza"
        stringResource(R.string.interest_beach),     // "Playa"
        stringResource(R.string.interest_concerts),  // "Conciertos"
        stringResource(R.string.interest_relax),     // "Relax"
    )

    // Scaffold proporciona la estructura base con snackbar y top bar
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }, // Host para mensajes
        topBar = {
            // Barra superior vacía (solo para tener el padding correcto)
            TopAppBar(
                title = {},
                windowInsets = WindowInsets(0, 0, 0, 0), // Sin insets
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding -> // padding interno para evitar la top bar
        // Contenedor Box para superponer el LoadingOverlay
        Box(modifier = Modifier.fillMaxSize()) {
            // Columna principal con scroll vertical
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background) // Fondo del tema
                    .padding(padding) // Aplica el padding del Scaffold
                    .verticalScroll(rememberScrollState()) // Habilita scroll vertical
                    .padding(horizontal = 24.dp), // Padding lateral de 24dp
                horizontalAlignment = Alignment.CenterHorizontally // Centra horizontalmente
            ) {
                // --- Título principal ---
                Text(
                    text = stringResource(R.string.complete_profile), // "Completa tu perfil"
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                // --- Subtítulo descriptivo ---
                Text(
                    text = stringResource(R.string.complete_profile_desc), // "Cuéntanos más sobre ti para personalizar tu experiencia"
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // --- 1. Sección: Foto de perfil ---
                Box(
                    modifier = Modifier
                        .size(110.dp) // Tamaño fijo de 110x110dp
                        .clip(CircleShape) // Forma circular
                        .background(MaterialTheme.colorScheme.surfaceVariant) // Color de fondo si no hay foto
                        .clickable(enabled = !isLoading) { photoPickerLauncher.launch("image/*") }, // Abre selector de imágenes
                    contentAlignment = Alignment.Center
                ) {
                    if (photoUri != null) {
                        // Muestra la imagen seleccionada
                        AsyncImage(
                            model = photoUri,
                            contentDescription = "Profile Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop // Recorta para llenar el círculo
                        )
                    } else {
                        // Muestra ícono de cámara si no hay foto
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Add Photo",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.profile_photo), // "Foto de perfil"
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // --- 2. Sección: Selección de idioma ---
                Text(
                    text = stringResource(R.string.select_your_language), // "Selecciona tu idioma"
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(12.dp))
                // Fila con botones para español e inglés
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AppLanguage.entries.forEach { appLang -> // Itera sobre los idiomas disponibles
                        val isSelected = selectedLanguage == appLang
                        // Animación del color del borde según selección
                        val borderColor by animateColorAsState(if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray)
                        
                        Surface(
                            modifier = Modifier
                                .weight(1f) // Cada botón ocupa el mismo ancho
                                .height(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(2.dp, borderColor, RoundedCornerShape(12.dp)) // Borde animado
                                .clickable { viewModel.onLanguageChange(appLang) }, // Cambia idioma al hacer clic
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = appLang.displayName, // "Español" o "English"
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- 3. Sección: Nombre de usuario ---
                OutlinedTextField(
                    value = username,
                    onValueChange = { viewModel.onUsernameChange(it) },
                    placeholder = { Text(stringResource(R.string.username_label)) }, // "Nombre de usuario"
                    leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null) }, // Ícono de @
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true, // Una sola línea
                    enabled = !isLoading // Deshabilitado mientras se guarda
                )

                Spacer(modifier = Modifier.height(32.dp))

                // --- 4. Sección: Intereses (FlowRow de chips) ---
                Text(
                    text = stringResource(R.string.what_interests_you), // "¿Qué te interesa?"
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                // FlowRow para que los chips fluyan en múltiples líneas
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableInterests.forEach { interest ->
                        val isSelected = interests.contains(interest)
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.toggleInterest(interest) }, // Alterna selección del interés
                            label = { Text(interest) },
                            enabled = !isLoading,
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = if (isSelected) {
                                { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) } // Check si está seleccionado
                            } else null
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- 5. Sección: Configuraciones (notificaciones) ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(stringResource(R.string.enable_notifications), fontWeight = FontWeight.SemiBold, fontSize = 14.sp) // "Activar notificaciones"
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { viewModel.onNotificationsToggle(it) }, // Alterna notificaciones
                            enabled = !isLoading
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // --- Botón final: Continuar al feed ---
                Button(
                    onClick = { viewModel.saveProfile(userId, onNavigateToFeed) }, // Guarda el perfil y navega
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isLoading // Deshabilitado mientras se guarda
                ) {
                    if (isLoading) {
                        // Muestra indicador de carga
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text(stringResource(R.string.continue_to_feed), fontSize = 18.sp, fontWeight = FontWeight.Bold) // "Continuar al feed"
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp)) // Espaciado final
            }
            
            // Overlay de carga que cubre toda la pantalla mientras isLoading = true
            LoadingOverlay(isLoading = isLoading, text = stringResource(R.string.creating_account_loading)) // "Creando cuenta..."
        }
    }
}
