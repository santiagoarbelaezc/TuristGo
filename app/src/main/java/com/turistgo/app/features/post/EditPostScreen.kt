// Declara el paquete donde se encuentra esta pantalla dentro de la estructura de la app.
package com.turistgo.app.features.post

// Importaciones de Jetpack Compose para la UI
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel // Para obtener ViewModel en Compose
import androidx.navigation.NavController // Para navegación entre pantallas
import coil.compose.AsyncImage // Para cargar imágenes desde URL
import kotlinx.coroutines.launch // Para lanzar corrutinas

// Marca que se usan APIs experimentales de Material 3
@OptIn(ExperimentalMaterial3Api::class)
// Declara la función composable principal de la pantalla de edición de publicación
@Composable
fun EditPostScreen(
    navController: NavController, // Controlador de navegación para volver atrás
    postId: String?, // ID de la publicación a editar (puede ser null si no se proporciona)
    viewModel: EditPostViewModel = viewModel() // ViewModel obtenido mediante viewModel() (no Hilt)
) {
    // Observa el estado de la UI del ViewModel
    val uiState by viewModel.uiState
    // Estado para mostrar mensajes (snackbar) en la parte inferior
    val snackbarHostState = remember { SnackbarHostState() }
    // CoroutineScope para lanzar operaciones asíncronas (como mostrar snackbar y esperar)
    val scope = rememberCoroutineScope()
    // Lista de categorías disponibles para la publicación
    val categories = listOf("General", "Seguridad", "Turismo", "Evento", "Emergencia", "Otro")

    // Efecto que se ejecuta cuando cambia el postId para cargar los datos de la publicación
    LaunchedEffect(postId) {
        viewModel.loadPost(postId) // Carga la publicación desde el repositorio/backend
    }

    // Scaffold proporciona la estructura base con top bar y snackbar
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }, // Host para mostrar mensajes
        topBar = {
            // Barra superior con título y botón de retroceso
            TopAppBar(
                title = { Text("Editar Publicación", fontWeight = FontWeight.Bold) }, // Título de la pantalla
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Botón de flecha hacia atrás
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background // Mismo color que el fondo
                ),
                windowInsets = WindowInsets(0, 0, 0, 0) // Sin insets adicionales
            )
        }
    ) { padding -> // padding interno para evitar la top bar
        // Columna principal que ocupa toda la pantalla
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding) // Aplica el padding del Scaffold
                .background(MaterialTheme.colorScheme.background) // Fondo del tema actual
        ) {
            // Columna con scroll vertical para el formulario de edición
            Column(
                modifier = Modifier
                    .weight(1f) // Ocupa el espacio restante (para que el botón no se esconda)
                    .padding(20.dp) // Padding interno de 20dp
                    .verticalScroll(rememberScrollState()) // Habilita scroll vertical
            ) {
                // --- Imagen actual de la publicación (si existe) ---
                if (uiState.imageUrl.isNotEmpty()) {
                    // Muestra la imagen actual del lugar
                    AsyncImage(
                        model = uiState.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp) // Altura fija de 180dp
                            .clip(RoundedCornerShape(16.dp)), // Bordes redondeados de 16dp
                        contentScale = ContentScale.Crop // Ajusta la imagen recortando para llenar el espacio
                    )
                    Spacer(modifier = Modifier.height(16.dp)) // Espacio después de la imagen
                }

                // --- Campo: Título ---
                OutlinedTextField(
                    value = uiState.title, // Valor actual del título
                    onValueChange = { viewModel.updateTitle(it) }, // Actualiza en el ViewModel
                    label = { Text("Título de la publicación") }, // Etiqueta flotante
                    modifier = Modifier.fillMaxWidth(), // Ocupa todo el ancho disponible
                    shape = RoundedCornerShape(12.dp) // Bordes redondeados de 12dp
                )

                Spacer(modifier = Modifier.height(16.dp)) // Espaciado

                // --- Selección de categorías (fila horizontal con chips) ---
                Text(
                    text = "Selecciona una categoría",
                    fontWeight = FontWeight.SemiBold, // Semi-negrita
                    modifier = Modifier.padding(bottom = 8.dp) // Padding inferior
                )
                // LazyRow para scroll horizontal de los chips de categoría
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp), // Espacio de 8dp entre chips
                    modifier = Modifier.padding(bottom = 16.dp) // Padding inferior
                ) {
                    // Itera sobre la lista de categorías
                    items(categories) { category ->
                        FilterChip(
                            selected = uiState.category == category, // Marca como seleccionada si coincide
                            onClick = { viewModel.updateCategory(category) }, // Actualiza la categoría en el ViewModel
                            label = { Text(category) }, // Texto del chip
                            shape = RoundedCornerShape(20.dp) // Bordes redondeados de 20dp (forma de píldora)
                        )
                    }
                }

                // --- Campo: Descripción ---
                OutlinedTextField(
                    value = uiState.description, // Valor actual de la descripción
                    onValueChange = { viewModel.updateDescription(it) }, // Actualiza en el ViewModel
                    label = { Text("Descripción") }, // Etiqueta flotante
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp), // Altura fija de 150dp
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 5 // Máximo 5 líneas de texto
                )

                Spacer(modifier = Modifier.height(16.dp)) // Espaciado

                // --- Campo: Ubicación ---
                OutlinedTextField(
                    value = uiState.location, // Valor actual de la ubicación
                    onValueChange = { viewModel.updateLocation(it) }, // Actualiza en el ViewModel
                    label = { Text("Ubicación exacta o zona") }, // Etiqueta flotante
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) }, // Ícono de ubicación a la izquierda
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(32.dp)) // Espaciado más grande antes del botón

                // --- Botón: Guardar Cambios ---
                Button(
                    onClick = {
                        // Cuando se hace clic en guardar:
                        scope.launch {
                            // Muestra un mensaje de éxito en el snackbar
                            snackbarHostState.showSnackbar("Publicación actualizada correctamente")
                            // Espera 1 segundo para que el usuario vea el mensaje
                            kotlinx.coroutines.delay(1000)
                            // Vuelve a la pantalla anterior
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp), // Altura fija de 56dp
                    shape = RoundedCornerShape(16.dp) // Bordes redondeados de 16dp
                ) {
                    Text("Guardar Cambios", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(16.dp)) // Espaciado

                // --- Botón: Cancelar Edición (outlined) ---
                OutlinedButton(
                    onClick = { navController.popBackStack() }, // Vuelve atrás sin guardar
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error) // Texto en color de error (rojo)
                ) {
                    Text("Cancelar Edición", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
