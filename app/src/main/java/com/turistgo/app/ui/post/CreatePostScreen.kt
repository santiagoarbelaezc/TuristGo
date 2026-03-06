package com.turistgo.app.ui.post

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.turistgo.app.data.GeminiService
import com.turistgo.app.ui.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    viewModel: CreatePostViewModel = viewModel(),
    navController: NavController? = null,
    onBack: () -> Unit = {}
) {
    val title            by viewModel.title
    val description      by viewModel.description
    val location         by viewModel.location
    val priceRange       by viewModel.priceRange
    val selectedCategory by viewModel.selectedCategory
    val suggestedCategory by viewModel.suggestedCategory
    val isAnalyzing      by viewModel.isAnalyzing

    var openStartPicker by remember { mutableStateOf(false) }
    var openEndPicker   by remember { mutableStateOf(false) }
    var startHour   by remember { mutableIntStateOf(8) }
    var startMinute by remember { mutableIntStateOf(0) }
    var endHour     by remember { mutableIntStateOf(18) }
    var endMinute   by remember { mutableIntStateOf(0) }

    // Date Picker State
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val selectedDateText = datePickerState.selectedDateMillis?.let {
        val date = java.util.Date(it)
        val format = java.text.SimpleDateFormat("dd/MM/yy", java.util.Locale.getDefault())
        format.format(date)
    } ?: "Seleccionar fecha"

    // AI description generation
    val scope = rememberCoroutineScope()
    var isGeneratingDesc by remember { mutableStateOf(false) }

    // Listen for map picker result
    val savedStateHandle = navController?.currentBackStackEntry?.savedStateHandle
    val mapResult = savedStateHandle?.get<String>("selected_location")
    LaunchedEffect(mapResult) {
        mapResult?.let {
            viewModel.onLocationChange(it)
            savedStateHandle.remove<String>("selected_location")
        }
    }

    // Time Picker Dialogs
    if (openStartPicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = startHour,
            initialMinute = startMinute,
            is24Hour = true
        )
        TimePickerDialog(
            title = "Hora de Apertura",
            onDismiss = { openStartPicker = false },
            onConfirm = {
                startHour = timePickerState.hour
                startMinute = timePickerState.minute
                openStartPicker = false
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }

    if (openEndPicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = endHour,
            initialMinute = endMinute,
            is24Hour = true
        )
        TimePickerDialog(
            title = "Hora de Cierre",
            onDismiss = { openEndPicker = false },
            onConfirm = {
                endHour = timePickerState.hour
                endMinute = timePickerState.minute
                openEndPicker = false
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CenterAlignedTopAppBar(
            title = { Text("Crear", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            windowInsets = WindowInsets(0, 0, 0, 0)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Comparte un lugar especial con la comunidad",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Título
            OutlinedTextField(
                value = title,
                onValueChange = { viewModel.onTitleChange(it) },
                label = { Text("Nombre del lugar") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("Ej: Restaurante El Mirador") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sugerencia de categoría IA
            AnimatedVisibility(
                visible = suggestedCategory != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Sugerencia de IA", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("¿Es este un lugar de ${suggestedCategory}?", fontSize = 14.sp)
                        }
                        TextButton(onClick = { viewModel.acceptAiSuggestion() }) {
                            Text("Aceptar")
                        }
                    }
                }
            }

            // Categorías
            Text(
                text = "Categoría",
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(viewModel.categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { viewModel.onCategoryChange(category) },
                        label = { Text(category) },
                        shape = RoundedCornerShape(20.dp),
                        leadingIcon = if (selectedCategory == category) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }

            // Descripción + Botón generar con IA
            Row(
                modifier = Modifier.padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Descripción",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                if (isGeneratingDesc) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(6.dp))
                        Text("Generando...", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    Surface(
                        onClick = {
                            if (title.length > 3) {
                                scope.launch {
                                    isGeneratingDesc = true
                                    val generated = GeminiService.generatePostDescription(title, selectedCategory)
                                    viewModel.onDescriptionChange(generated)
                                    isGeneratingDesc = false
                                }
                            }
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Generar con IA", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = { viewModel.onDescriptionChange(it) },
                label = { Text("Descripción") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Fecha (Calendar)
            Text(
                text = "Fecha del Evento / Visita",
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = selectedDateText,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha (DD/MM/YY)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Abrir calendario")
                    }
                },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Horario — dos campos lado a lado (hora inicio y fin)
            Text(
                text = "Horario de Atención",
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Hora Apertura
                OutlinedTextField(
                    value = "%02d:%02d".format(startHour, startMinute),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Apertura") },
                    leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { openStartPicker = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(18.dp))
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .clickable { openStartPicker = true },
                    shape = RoundedCornerShape(12.dp)
                )
                // Hora Cierre
                OutlinedTextField(
                    value = "%02d:%02d".format(endHour, endMinute),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Cierre") },
                    leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { openEndPicker = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(18.dp))
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .clickable { openEndPicker = true },
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Precio — campo propio en su fila
            OutlinedTextField(
                value = priceRange,
                onValueChange = { viewModel.onPriceRangeChange(it) },
                label = { Text("Precio estimado") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("Ej: \$30.000 COP") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Ubicación
            OutlinedTextField(
                value = location,
                onValueChange = { viewModel.onLocationChange(it) },
                label = { Text("Ubicación (Mapa)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    IconButton(onClick = { navController?.navigate(Screen.MapPicker.route) }) {
                        Icon(Icons.Default.Map, contentDescription = "Seleccionar en mapa")
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Subir Fotos
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { /* Upload photo */ },
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.AddAPhoto,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text("Agregar fotos reales", color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de Publicar
            Button(
                onClick = { /* Publish logic */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Publicar Punto de Interés", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Text(
                text = "Como Explorador, tu publicación será enviada a moderación para ser verificada.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 12.dp),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}

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
                content()
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
