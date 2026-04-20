// Declara el paquete donde se encuentra esta pantalla dentro de la estructura de la app.
package com.turistgo.app.features.post

// Importaciones de Jetpack Compose para animaciones y UI
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel // Para inyectar ViewModel con Hilt
import com.turistgo.app.data.GeminiService // Servicio de IA para generar descripciones
import com.turistgo.app.core.navigation.MainRoutes
import kotlinx.coroutines.launch // Para lanzar corrutinas
import androidx.compose.ui.res.stringResource
import com.turistgo.app.R
import com.turistgo.app.core.components.SuccessModal // Modal de éxito

// Importaciones para selector de imágenes
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.ui.layout.ContentScale

// Marca que se usan APIs experimentales de Material 3 y Layout
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
// Declara la función composable principal de la pantalla de creación de post
@Composable
fun CreatePostScreen(
    innerPadding: PaddingValues, // Padding de la navegación superior
    viewModel: CreatePostViewModel = hiltViewModel(), // ViewModel inyectado por Hilt
    mapResult: String? = null, // Resultado del selector de mapa (coordenadas: "lat,lng")
    onConsumeMapResult: () -> Unit = {}, // Callback para consumir el resultado del mapa
    onNavigateToMapPicker: () -> Unit = {}, // Callback para abrir el selector de mapa
    onBack: () -> Unit = {} // Callback para volver atrás
) {
    // Estados del ViewModel observados
    val title              by viewModel.title
    val description        by viewModel.description
    val location           by viewModel.location
    val priceRange         by viewModel.priceRange
    val selectedCategories by viewModel.selectedCategories
    val suggestedCategory  by viewModel.suggestedCategory // Categoría sugerida por IA
    val isAnalyzing        by viewModel.isAnalyzing
    val selectedImageUri   by viewModel.selectedImageUri
    val isUploading        by viewModel.isUploading
    val startTime          by viewModel.startTime
    val endTime            by viewModel.endTime
    val latitude           by viewModel.latitude
    val longitude          by viewModel.longitude
    val moderationAlert    by viewModel.moderationAlert // Alerta de moderación por IA
    
    // --- DIÁLOGO DE MODERACIÓN POR IA (PREMIUM) ---
    // Muestra un diálogo cuando la IA detecta contenido inapropiado
    com.turistgo.app.core.components.TuristGoDialog(
        state = moderationAlert,
        onDismiss = { viewModel.dismissModerationAlert() }
    )
    
    // Estados para ubicación estructurada (departamento y ciudad)
    val department           by viewModel.department
    val city                 by viewModel.city
    val availableDepartments by viewModel.availableDepartments
    val availableCities      by viewModel.availableCities
    
    // Estados para controlar la expansión de los menús desplegables
    var departmentExpanded by remember { mutableStateOf(false) }
    var cityExpanded       by remember { mutableStateOf(false) }
    
    // Color de acento (primario del tema)
    val RedAccent          = MaterialTheme.colorScheme.primary 

    // Lanzador para el selector de fotos (galería)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.onImageSelected(uri) }
    )

    // Estados para controlar los diálogos de selector de hora
    var openStartPicker by remember { mutableStateOf(false) }
    var openEndPicker   by remember { mutableStateOf(false) }

    // Generación de descripción por IA
    val scope = rememberCoroutineScope()
    var isGeneratingDesc by remember { mutableStateOf(false) }

    // Escucha el resultado del selector de mapa (cuando se selecciona una ubicación en el mapa)
    LaunchedEffect(mapResult) {
        mapResult?.let { result ->
            val coords = result.split(",") // Divide "lat,lng"
            if (coords.size == 2) {
                val lat = coords[0].toDoubleOrNull()
                val lng = coords[1].toDoubleOrNull()
                if (lat != null && lng != null) {
                    viewModel.onCoordinatesSelected(lat, lng) // Guarda coordenadas en ViewModel
                }
            }
            onConsumeMapResult() // Marca el resultado como consumido
        }
    }

    // Diálogo selector de hora de apertura
    if (openStartPicker) {
        val timePickerState = rememberTimePickerState(is24Hour = true)
        TimePickerDialog(
            title = "Hora de Apertura",
            onDismiss = { openStartPicker = false },
            onConfirm = {
                val time = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                viewModel.onStartTimeChange(time)
                openStartPicker = false
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }

    // Diálogo selector de hora de cierre
    if (openEndPicker) {
        val timePickerState = rememberTimePickerState(is24Hour = true)
        TimePickerDialog(
            title = "Hora de Cierre",
            onDismiss = { openEndPicker = false },
            onConfirm = {
                val time = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                viewModel.onEndTimeChange(time)
                openEndPicker = false
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }

    // Estado para mostrar el modal de éxito
    var showSuccessModal by remember { mutableStateOf(false) }

    // Modal de éxito que se muestra después de enviar el post
    if (showSuccessModal) {
        SuccessModal(
            title = stringResource(R.string.post_submitted_title),
            message = stringResource(R.string.post_submitted_msg),
            onDismiss = {
                showSuccessModal = false
                onBack() // Vuelve a la pantalla anterior
            }
        )
    }

    // Columna principal que ocupa toda la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = innerPadding.calculateBottomPadding()) // Padding inferior del sistema
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding() // Padding para la barra de estado
    ) {
        // Barra superior centrada con título y botón de retroceso
        CenterAlignedTopAppBar(
            title = { Text(stringResource(R.string.new_post), fontWeight = FontWeight.Bold, fontSize = 18.sp) }, // "Nuevo Post"
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = MaterialTheme.colorScheme.onBackground)
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            windowInsets = WindowInsets(0, 0, 0, 0)
        )

        // Columna con scroll vertical para el formulario
        Column(
            modifier = Modifier
                .weight(1f) // Ocupa el espacio restante
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Texto descriptivo
            Text(
                text = stringResource(R.string.share_with_community),
                fontSize = 14.sp,
                color = Color(0xFF888888),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // --- Campo: Título ---
            OutlinedTextField(
                value = title,
                onValueChange = { viewModel.onTitleChange(it) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                placeholder = { Text("Título de la publicación", color = Color(0xFF999999)) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedIndicatorColor = Color(0xFFDDDDDD),
                    focusedIndicatorColor = RedAccent
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Sugerencia de categoría por IA (animada) ---
            AnimatedVisibility(
                visible = suggestedCategory != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFE0E0) // Fondo rojo claro
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = RedAccent)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Sugerencia de IA", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RedAccent)
                            Text("¿Encaja en la categoría ${suggestedCategory}?", fontSize = 13.sp)
                        }
                        TextButton(onClick = { viewModel.acceptAiSuggestion() }) {
                            Text("Aceptar", color = RedAccent)
                        }
                    }
                }
            }

            // --- Selección de categorías (múltiple) ---
            Text(
                text = "Categorías (puedes elegir varias)",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.padding(bottom = 10.dp)
            )
            // FlowRow para que los chips fluyan en múltiples líneas
            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.categories.forEach { category ->
                    val isSelected = selectedCategories.contains(category)
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onCategoryToggle(category) },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RedAccent.copy(alpha = 0.1f),
                            selectedLabelColor = RedAccent,
                            selectedLeadingIconColor = RedAccent
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            selectedBorderColor = RedAccent,
                            borderColor = Color(0xFFDDDDDD)
                        ),
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }

            // --- Descripción con generación por IA ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.description),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color(0xFF1A1A1A),
                    modifier = Modifier.weight(1f)
                )
                // Botón de generación IA o indicador de carga
                if (isGeneratingDesc) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = RedAccent)
                        Spacer(Modifier.width(6.dp))
                        Text("Generando...", fontSize = 12.sp, color = RedAccent)
                    }
                } else {
                    Surface(
                        onClick = {
                            if (title.length > 3) { // Solo si el título tiene más de 3 caracteres
                                scope.launch {
                                    isGeneratingDesc = true
                                    val cat = if (selectedCategories.isNotEmpty()) selectedCategories.first() else "Otros"
                                    val generated = com.turistgo.app.data.GeminiService.generatePostDescription(title, cat)
                                    viewModel.onDescriptionChange(generated)
                                    isGeneratingDesc = false
                                }
                            }
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFFFE0E0)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(13.dp), tint = RedAccent)
                            Spacer(Modifier.width(4.dp))
                            Text("IA", fontSize = 11.sp, color = RedAccent, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Campo de descripción (multilínea)
            OutlinedTextField(
                value = description,
                onValueChange = { viewModel.onDescriptionChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(16.dp),
                placeholder = { Text("Escribe una descripción atractiva...", color = Color(0xFF999999)) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedIndicatorColor = Color(0xFFDDDDDD),
                    focusedIndicatorColor = RedAccent
                ),
                maxLines = 6
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Ubicación Estructurada (Colombia) ---
            Text(
                text = "Ubicación (Colombia)",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Selector de Departamento (menú desplegable)
            ExposedDropdownMenuBox(
                expanded = departmentExpanded,
                onExpandedChange = { departmentExpanded = !departmentExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = department,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Departamento") },
                    leadingIcon = { Icon(Icons.Default.Map, contentDescription = null, tint = RedAccent) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = departmentExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedIndicatorColor = Color(0xFFDDDDDD),
                        focusedIndicatorColor = RedAccent
                    )
                )
                ExposedDropdownMenu(
                    expanded = departmentExpanded,
                    onDismissRequest = { departmentExpanded = false }
                ) {
                    availableDepartments.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                viewModel.onDepartmentChange(selectionOption)
                                departmentExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Selector de Ciudad (depende del departamento seleccionado)
            ExposedDropdownMenuBox(
                expanded = cityExpanded,
                onExpandedChange = { 
                    if (department.isNotEmpty()) cityExpanded = !cityExpanded 
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = city,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Ciudad") },
                    leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null, tint = RedAccent) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    enabled = department.isNotEmpty(), // Solo habilita si hay departamento
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedIndicatorColor = Color(0xFFDDDDDD),
                        focusedIndicatorColor = RedAccent
                    )
                )
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

            // --- Dirección manual y botón de mapa ---
            OutlinedTextField(
                value = location,
                onValueChange = { viewModel.onLocationChange(it) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                label = { Text("Dirección manual") },
                placeholder = { Text("Ej: Calle 123 #45-67", color = Color(0xFF999999)) },
                leadingIcon = {
                    Icon(Icons.Default.Home, contentDescription = null, tint = Color(0xFF555555))
                },
                trailingIcon = {
                    IconButton(onClick = onNavigateToMapPicker) { // Abre el selector de mapa
                        Icon(
                            Icons.Default.MyLocation, 
                            contentDescription = "Fijar en Mapa", 
                            tint = if (latitude != null) Color(0xFF4CAF50) else RedAccent // Verde si hay coordenadas
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedIndicatorColor = Color(0xFFDDDDDD),
                    focusedIndicatorColor = RedAccent
                ),
                singleLine = true
            )
            
            // Muestra las coordenadas si están seleccionadas
            if (latitude != null && longitude != null) {
                Row(
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Ubicación GPS fijada: ${String.format("%.4f", latitude)}, ${String.format("%.4f", longitude)}",
                        fontSize = 11.sp,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Horarios ---
            Text(
                text = "¿Tiene horario específico?",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // Fila con dos tarjetas: Apertura y Cierre
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedCard(
                    onClick = { openStartPicker = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(modifier = Modifier.padding(12.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (startTime.isEmpty()) "Apertura" else startTime,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (startTime.isEmpty()) Color.Gray else Color.Black
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                OutlinedCard(
                    onClick = { openEndPicker = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(modifier = Modifier.padding(12.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (endTime.isEmpty()) "Cierre" else endTime,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (endTime.isEmpty()) Color.Gray else Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Rango de precios ---
            OutlinedTextField(
                value = priceRange,
                onValueChange = { viewModel.onPriceRangeChange(it) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                placeholder = { Text("Rango de precios (ej: $10k - $50k)", color = Color(0xFF999999)) },
                leadingIcon = {
                    Icon(Icons.Default.Payments, contentDescription = null, tint = Color(0xFF555555))
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedIndicatorColor = Color(0xFFDDDDDD),
                    focusedIndicatorColor = RedAccent
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- Subir imagen ---
            Text(
                text = "Imagen del lugar",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            // Caja clickeable para seleccionar imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .clickable { 
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    // Muestra la imagen seleccionada
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Botón de editar superpuesto
                    Surface(
                        modifier = Modifier.padding(8.dp).align(Alignment.TopEnd),
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.5f)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(8.dp).size(20.dp)
                        )
                    }
                } else {
                    // Estado sin imagen: muestra ícono y texto
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.AddAPhoto,
                            contentDescription = null,
                            tint = RedAccent,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Subir foto", color = RedAccent, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // --- Botón Publicar / Enviar a Moderación ---
            Button(
                onClick = { 
                    viewModel.savePost { 
                        showSuccessModal = true // Muestra modal de éxito al guardar
                    } 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedAccent),
                enabled = !isUploading && title.isNotEmpty() // Deshabilitado si está subiendo o sin título
            ) {
                if (isUploading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Enviar a Moderación", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            // Texto informativo sobre moderación
            Text(
                text = "Tu publicación será revisada para asegurar la calidad de la comunidad.",
                fontSize = 11.sp,
                color = Color(0xFF999999),
                modifier = Modifier.padding(top = 10.dp, bottom = 24.dp),
                textAlign = TextAlign.Center,
                lineHeight = 15.sp
            )
        }
    }
}

// Componente para el diálogo selector de hora (reutilizable)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                content() // Aquí va el TimePicker
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
