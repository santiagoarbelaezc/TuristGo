package com.turistgo.app.features.trips

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.turistgo.app.core.locale.AppStrings
import com.turistgo.app.core.locale.LanguageState
import com.turistgo.app.domain.model.ChatMessage
import com.turistgo.app.domain.model.Post
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: TripsViewModel = hiltViewModel()
) {
    var textInput by remember { mutableStateOf("") }
    val messages = viewModel.messages
    val isLoading by viewModel.isLoading
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val lang by LanguageState.current
    val s = AppStrings.get(lang)

    val mascotUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1771977314/turistgo-logo_evi36h.png"

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Same structure as ProfileScreen: Column → TopAppBar(windowInsets=0) → content
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF7F3))
    ) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = mascotUrl,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp).clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("TuristGo AI", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFFC62828))
                        Text(s.tripsAiSubtitle, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            },
            actions = {
                IconButton(onClick = { }) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFC62828))
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            windowInsets = WindowInsets(0, 0, 0, 0)
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (messages.size <= 1) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = mascotUrl,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(s.tripsWelcome, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            s.tripsWelcomeBody,
                            textAlign = TextAlign.Center,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 40.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            QuickActionChip(s.tripsQuickPlan1) {
                                viewModel.onQuickPlanSelected("plan con mi novia")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            QuickActionChip(s.tripsQuickPlan2) {
                                viewModel.onQuickPlanSelected("aventura en familia")
                            }
                        }
                    }
                }
            }

            items(messages) { message ->
                ChatBubble(message, onNavigateToDetail)
            }

            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    }
                }
            }
        }

        // Input Area
        Surface(
            tonalElevation = 2.dp,
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(s.tripsInputPlaceholder) },
                    shape = CircleShape,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color(0xFFC62828)
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                FloatingActionButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            viewModel.sendMessage(textInput)
                            textInput = ""
                        }
                    },
                    containerColor = Color(0xFFC62828),
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Enviar")
                }
            }
        }
    }
}

@Composable
fun QuickActionChip(label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFE1BEE7).copy(alpha = 0.5f), // Matching screenshot color
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF9C27B0).copy(alpha = 0.3f))
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ChatBubble(message: ChatMessage, onNavigateToDetail: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isFromUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (message.isFromUser) Color(0xFFC62828) else Color.White,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isFromUser) 16.dp else 4.dp,
                bottomEnd = if (message.isFromUser) 4.dp else 16.dp
            ),
            tonalElevation = if (message.isFromUser) 0.dp else 1.dp
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (!message.isFromUser) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("TuristGo AI", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                
                // Parse and render content with special styling for "Día X" and Itinerary items
                val lines = message.content.split("\n")
                Column {
                    lines.forEach { line ->
                        val trimmedLine = line.trim()
                        when {
                            trimmedLine.startsWith("Día ", ignoreCase = true) -> {
                                Spacer(modifier = Modifier.height(8.dp))
                                DayLabel(trimmedLine)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            trimmedLine.contains(Regex("^\\p{So}|\\p{Sk}|\\p{Sm}") ) || trimmedLine.contains(Regex("\\d{2}:\\d{2}")) -> {
                                // Line with emoji or time
                                Text(
                                    text = line,
                                    color = if (message.isFromUser) Color.White else Color.Black,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                            trimmedLine.startsWith("🗓️") -> {
                                Text(
                                    text = line,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                            else -> {
                                Text(
                                    text = line,
                                    color = if (message.isFromUser) Color.White else Color.Black,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
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
        color = Color(0xFFF3E5F5), // Light purple/pink matching screenshot
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            color = Color(0xFFAD1457), // Content color
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun PlanResponseView(destinations: List<Post>, onNavigateToDetail: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.TravelExplore, contentDescription = null, tint = Color(0xFFC62828))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Plan romántico para dos 💖", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("📍 Lugares recomendados", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(destinations) { destination ->
                    DestinationMiniCard(destination) {
                        onNavigateToDetail(destination.id)
                    }
                }
            }
        }
    }
}

@Composable
fun DestinationMiniCard(destination: Post, onClick: () -> Unit) {
    Card(
        modifier = Modifier.size(200.dp, 120.dp).clip(RoundedCornerShape(16.dp)),
        onClick = onClick
    ) {
        Box {
            AsyncImage(
                model = destination.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .padding(8.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(destination.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}
