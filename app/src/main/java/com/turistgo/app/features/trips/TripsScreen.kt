package com.turistgo.app.features.trips

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.turistgo.app.R
import com.turistgo.app.domain.model.ChatMessage
import com.turistgo.app.domain.model.Post
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(
    innerPadding: PaddingValues,
    onNavigateToDetail: (String) -> Unit,
    viewModel: TripsViewModel = hiltViewModel()
) {
    val messages = viewModel.messages
    val isAiTyping by viewModel.isLoading
    val scrollState = rememberLazyListState()
    val logoUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1771997914/logo-turist_x5xgsq.png"
    var showClearDialog by remember { mutableStateOf(false) }
    var showLogoDialog by remember { mutableStateOf(false) }
    
    // Auto-scroll when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = innerPadding.calculateBottomPadding())
            .background(Color(0xFFFEF7F3))
            .statusBarsPadding()
    ) {
        // Pinned Top Bar
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showLogoDialog = true }
                ) {
                    AsyncImage(
                        model = logoUrl,
                        contentDescription = "Logo TuristGo",
                        modifier = Modifier.size(45.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "TuristGo AI",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = Color(0xFFC62828)
                        )
                        Text(
                            stringResource(R.string.trips_ai_subtitle),
                            fontSize = 11.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            },
            actions = {
                // Botón nuevo chat
                Box(
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFDE8E8))
                        .clickable(enabled = !isAiTyping) { showClearDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AddComment,
                        contentDescription = "Nuevo chat",
                        tint = Color(0xFFC62828),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF3E5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFFC62828),
                        modifier = Modifier.size(18.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFFEF7F3)
            ),
            windowInsets = WindowInsets(0, 0, 0, 0)
        )

        // Diálogo de confirmación para limpiar el chat
        if (showClearDialog) {
            AlertDialog(
                onDismissRequest = { showClearDialog = false },
                icon = {
                    Icon(
                        Icons.Default.AddComment,
                        contentDescription = null,
                        tint = Color(0xFFC62828),
                        modifier = Modifier.size(32.dp)
                    )
                },
                title = {
                    Text(
                        "Nuevo chat",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Text(
                        "¿Quieres empezar una nueva conversación? El historial actual se borrará.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.clearChat()
                            showClearDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFC62828)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Nuevo chat", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDialog = false }) {
                        Text("Cancelar", color = Color.Gray)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
        }

        // Diálogo de logo expandido al tocar el águila
        if (showLogoDialog) {
            Dialog(
                onDismissRequest = { showLogoDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f))
                        .clickable { showLogoDialog = false },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        AsyncImage(
                            model = logoUrl,
                            contentDescription = "TuristGo Logo",
                            modifier = Modifier.size(220.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            "TuristGo AI",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Tu asistente de viajes inteligente",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Surface(
                            onClick = { showLogoDialog = false },
                            color = Color(0xFFC62828),
                            shape = RoundedCornerShape(50.dp)
                        ) {
                            Text(
                                "Empezar a planear 🚀",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                modifier = Modifier.padding(horizontal = 32.dp, vertical = 14.dp)
                            )
                        }
                    }
                }
            }
        }

        // Main Content Area
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Show welcome header only if there's no conversation yet (only initial message)
            if (messages.size <= 1) {
                item {
                    TripsWelcomeHeader(
                        logoUrl = logoUrl,
                        onQuickPlan = { viewModel.sendMessage(it) }
                    )
                }
            }
            
            items(messages) { message ->
                ChatBubble(message, onNavigateToDetail)
            }
            
            if (isAiTyping) {
                item {
                    TypingIndicator()
                }
            }
        }

        // Floating Chat Input (Removed navigationBarsPadding as it's double padding)
        ChatInput(
            onSendMessage = { viewModel.sendMessage(it) },
            isLoading = isAiTyping
        )
    }
}

@Composable
fun ChatBubble(message: ChatMessage, onNavigateToDetail: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = if (message.isFromUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Avatar de la IA (solo en mensajes de IA)
            if (!message.isFromUser) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFDE8E8)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFFC62828),
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            // Burbuja del mensaje
            Surface(
                color = if (message.isFromUser) Color(0xFFC62828) else Color.White,
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = if (message.isFromUser) 18.dp else 4.dp,
                    bottomEnd = if (message.isFromUser) 4.dp else 18.dp
                ),
                shadowElevation = if (message.isFromUser) 0.dp else 2.dp,
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    // Label "TuristGo AI" solo en mensajes de IA
                    if (!message.isFromUser) {
                        Text(
                            "TuristGo AI",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC62828),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    val lines = message.content.split("\n")
                    Column {
                        lines.forEach { line ->
                            val trimmedLine = line.trim()
                            when {
                                trimmedLine.startsWith("Día ", ignoreCase = true) ||
                                trimmedLine.startsWith("📅") -> {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    DayLabel(trimmedLine)
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                trimmedLine.startsWith("🗓️") -> {
                                    Text(
                                        text = line,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = if (message.isFromUser) Color.White else Color(0xFF1A1A1A),
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                                else -> {
                                    Text(
                                        text = line,
                                        color = if (message.isFromUser) Color.White else Color(0xFF1A1A1A),
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Avatar del usuario (solo en mensajes de usuario)
            if (message.isFromUser) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1A1A1A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tú", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (message.isPlanResponse && message.suggestedDestinations.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            PlanResponseView(message.suggestedDestinations, onNavigateToDetail)
        }
    }
}


@Composable
fun DayLabel(text: String) {
    Surface(
        color = Color(0xFFF3E5F5),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            color = Color(0xFFAD1457),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun PlanResponseView(destinations: List<Post>, onNavigateToDetail: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8F6)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Place,
                    contentDescription = null,
                    tint = Color(0xFFC62828),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Destinos en tu plan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1A1A1A)
                )
            }
            Text(
                "Toca un lugar para ver todos los detalles",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(destinations) { post ->
                    SuggestedDestinationItem(post) { onNavigateToDetail(post.id) }
                }
            }
        }
    }
}

@Composable
fun SuggestedDestinationItem(post: Post, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(170.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(120.dp)) {
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = post.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                            )
                        )
                )
                if (post.categories.isNotEmpty()) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        color = Color(0xFFC62828),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = post.categories.first(),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    post.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    post.location,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    onClick = onClick,
                    color = Color(0xFFC62828),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Ver destino →",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier
            .padding(start = 16.dp, top = 4.dp, bottom = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = Color(0xFFC62828),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            "TuristGo AI está pensando",
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.width(4.dp))
        repeat(3) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFC62828).copy(alpha = 0.5f))
            )
        }
    }
}

@Composable
fun TripsWelcomeHeader(logoUrl: String, onQuickPlan: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = logoUrl,
            contentDescription = null,
            modifier = Modifier.size(180.dp) // Increased size
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            stringResource(R.string.trips_welcome),
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        
        Text(
            stringResource(R.string.trips_welcome_body),
            fontSize = 15.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 48.dp, vertical = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickPlanCard(stringResource(R.string.trips_quick_plan_1), Modifier.weight(1f)) { onQuickPlan(it) }
            QuickPlanCard(stringResource(R.string.trips_quick_plan_2), Modifier.weight(1f)) { onQuickPlan(it) }
        }
    }
}

@Composable
fun QuickPlanCard(label: String, modifier: Modifier = Modifier, onClick: (String) -> Unit) {
    Surface(
        onClick = { onClick(label) },
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFEADDFF),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF21005D).copy(alpha = 0.1f))
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label, 
                fontSize = 14.sp, 
                fontWeight = FontWeight.SemiBold, 
                color = Color(0xFF21005D),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ChatInput(onSendMessage: (String) -> Unit, isLoading: Boolean) {
    var text by remember { mutableStateOf("") }
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp), // Reduced vertical padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text(stringResource(R.string.trips_input_placeholder), fontSize = 15.sp, color = Color.Gray) },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F7),
                    unfocusedContainerColor = Color(0xFFF5F5F7),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            FloatingActionButton(
                onClick = {
                    if (text.isNotBlank() && !isLoading) {
                        onSendMessage(text)
                        text = ""
                    }
                },
                containerColor = Color(0xFFC62828),
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(56.dp),
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(24.dp))
            }
        }
    }
}
