package com.turistgo.app.features.post

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.turistgo.app.data.GeminiService
import com.turistgo.app.core.navigation.MainRoutes
import kotlinx.coroutines.launch

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    viewModel: CreatePostViewModel = hiltViewModel(),
    mapResult: String? = null,
    onConsumeMapResult: () -> Unit = {},
    onNavigateToMapPicker: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val title             by viewModel.title
    val description       by viewModel.description
    val location          by viewModel.location
    val priceRange        by viewModel.priceRange
    val selectedCategory  by viewModel.selectedCategory
    val suggestedCategory by viewModel.suggestedCategory
    val isAnalyzing       by viewModel.isAnalyzing
    val selectedImageUri  by viewModel.selectedImageUri
    val isUploading       by viewModel.isUploading
    
    val RedAccent         = MaterialTheme.colorScheme.primary 

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.onImageSelected(uri) }
    )

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
    LaunchedEffect(mapResult) {
        mapResult?.let {
            viewModel.onLocationChange(it)
            onConsumeMapResult()
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
        // Top Bar — same pattern as ProfileScreen for consistent alignment
        TopAppBar(
            title = {
                Text(
                    text = "Nueva Publicación",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = MaterialTheme.colorScheme.onBackground)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            windowInsets = WindowInsets(0, 0, 0, 0)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Comparte algo con la comunidad",
                fontSize = 14.sp,
                color = Color(0xFF888888),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // --- Título ---
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

            Spacer(modifier = Modifier.height(20.dp))

            // --- Sugerencia IA ---
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
                    color = Color(0xFFFFE0E0)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = RedAccent)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Sugerencia de IA", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RedAccent)
                            Text("¿Es un lugar de ${suggestedCategory}?", fontSize = 13.sp)
                        }
                        TextButton(onClick = { viewModel.acceptAiSuggestion() }) {
                            Text("Aceptar", color = RedAccent)
                        }
                    }
                }
            }

            // --- Categorías ---
            Text(
                text = "Selecciona una categoría",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.padding(bottom = 10.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                items(viewModel.categories) { category ->
                    val isSelected = selectedCategory == category
                    Surface(
                        onClick = { viewModel.onCategoryChange(category) },
                        shape = RoundedCornerShape(24.dp),
                        color = if (isSelected) Color(0xFFE8D5F5) else Color.White,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF9C27B0) else Color(0xFFDDDDDD)
                        )
                    ) {
                        Text(
                            text = category,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) Color(0xFF7B1FA2) else Color(0xFF555555)
                        )
                    }
                }
            }

            // --- Descripción ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "Descripción",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color(0xFF1A1A1A),
                    modifier = Modifier.weight(1f)
                )
                if (isGeneratingDesc) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = RedAccent)
                        Spacer(Modifier.width(6.dp))
                        Text("Generando...", fontSize = 12.sp, color = RedAccent)
                    }
                } else {
                    Surface(
                        onClick = {
                            if (title.length > 3) {
                                scope.launch {
                                    isGeneratingDesc = true
                                    val generated = com.turistgo.app.data.GeminiService.generatePostDescription(title, selectedCategory)
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

            OutlinedTextField(
                value = description,
                onValueChange = { viewModel.onDescriptionChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(16.dp),
                placeholder = { Text("Descripción", color = Color(0xFF999999)) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedIndicatorColor = Color(0xFFDDDDDD),
                    focusedIndicatorColor = RedAccent
                ),
                maxLines = 6
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Ubicación ---
            OutlinedTextField(
                value = location,
                onValueChange = { viewModel.onLocationChange(it) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                placeholder = { Text("Ubicación exacta o zona", color = Color(0xFF999999)) },
                leadingIcon = {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF555555))
                },
                trailingIcon = {
                    IconButton(onClick = onNavigateToMapPicker) {
                        Icon(Icons.Default.Map, contentDescription = "Mapa", tint = RedAccent)
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

            Spacer(modifier = Modifier.height(24.dp))

            // --- Agregar foto ---
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
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Overlay icon to change
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
                        Text("Agregar fotos", color = RedAccent, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // --- Botón Publicar ---
            Button(
                onClick = { viewModel.savePost { onBack() } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedAccent),
                enabled = !isUploading && title.isNotEmpty()
            ) {
                if (isUploading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Publicar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Text(
                text = "Tu publicación será enviada a moderación antes de ser visible.",
                fontSize = 11.sp,
                color = Color(0xFF999999),
                modifier = Modifier.padding(top = 10.dp, bottom = 24.dp),
                textAlign = TextAlign.Center,
                lineHeight = 15.sp
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
