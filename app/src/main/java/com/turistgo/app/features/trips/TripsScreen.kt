package com.turistgo.app.features.trips

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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.turistgo.app.R
import com.turistgo.app.domain.model.ChatMessage
import com.turistgo.app.domain.model.Post
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: TripsViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val isAiTyping by viewModel.isAiTyping.collectAsState()
    val scrollState = rememberLazyListState()
    
    // Auto-scroll when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(R.string.nav_trips), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(stringResource(R.string.trips_ai_subtitle), fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFFEF7F3)
                )
            )
        },
        containerColor = Color(0xFFFEF7F3)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (messages.isEmpty()) {
                    item {
                        TripsWelcomeHeader(
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

            ChatInput(
                onSendMessage = { viewModel.sendMessage(it) },
                isLoading = isAiTyping
            )
        }
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
                            trimmedLine.contains(Regex("^\\p{So}|\\p{Sk}|\\p{Sm}")) || trimmedLine.contains(Regex("\\d{2}:\\d{2}")) -> {
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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Destinos Sugeridos", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
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
        modifier = Modifier.size(160.dp, 200.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box {
            AsyncImage(
                model = post.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f))
            )
            Column(
                modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)
            ) {
                Text(post.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                Text(post.location, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, maxLines = 1)
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier.padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) {
            Box(
                modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Gray.copy(alpha = 0.5f))
            )
        }
    }
}

@Composable
fun TripsWelcomeHeader(onQuickPlan: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = Color.White
        ) {
            Icon(
                Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color(0xFFC62828),
                modifier = Modifier.padding(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            stringResource(R.string.trips_welcome),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            stringResource(R.string.trips_welcome_body),
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickPlanCard(stringResource(R.string.trips_quick_plan_1), Icons.Default.Favorite, Modifier.weight(1f)) { onQuickPlan(it) }
            QuickPlanCard(stringResource(R.string.trips_quick_plan_2), Icons.Default.Groups, Modifier.weight(1f)) { onQuickPlan(it) }
        }
    }
}

@Composable
fun QuickPlanCard(label: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: (String) -> Unit) {
    Card(
        modifier = modifier.clickable { onClick(label) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = Color(0xFFC62828))
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun ChatInput(onSendMessage: (String) -> Unit, isLoading: Boolean) {
    var text by remember { mutableStateOf("") }
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text(stringResource(R.string.trips_input_placeholder), fontSize = 14.sp) },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    containerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
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
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(20.dp))
            }
        }
    }
}
