package com.turistgo.app.features.moderator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.turistgo.app.R
import androidx.hilt.navigation.compose.hiltViewModel
import com.turistgo.app.core.components.SuccessModal
import com.turistgo.app.data.repository.InMemoryRepository
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewPostScreen(
    postId: String, 
    onBack: () -> Unit,
    viewModel: ReviewPostViewModel = hiltViewModel()
) {
    val post by viewModel.post.collectAsState()
    val author by viewModel.author.collectAsState()
    val aiAnalysis by viewModel.aiAnalysis.collectAsState()
    val isAiAnalyzing by viewModel.isAiAnalyzing.collectAsState()
    
    val warmBg = Color(0xFFFBFAF5)
    val scrollState = rememberScrollState()

    LaunchedEffect(postId) {
        viewModel.loadPost(postId)
    }

    var showApprovedModal by remember { mutableStateOf(false) }

    if (showApprovedModal) {
        SuccessModal(
            title = stringResource(R.string.post_approved_title),
            message = stringResource(R.string.post_approved_msg),
            onDismiss = {
                showApprovedModal = false
                onBack()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Revisión de Contenido", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = warmBg)
            )
        },
        containerColor = warmBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            
            // --- 1. AUTHOR PROFILE CARD ---
            AuthorCard(author)

            // --- 2. POST PREVIEW IMAGE ---
            Surface(
                modifier = Modifier.fillMaxWidth().height(240.dp),
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (post?.imageUrl != null) {
                        AsyncImage(
                            model = post?.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Image, null, Modifier.size(48.dp), Color.Gray)
                            Text("Sin imagen", color = Color.Gray)
                        }
                    }
                    
                    // Category Badge
                    val firstCategory = post?.categories?.firstOrNull()
                    if (firstCategory != null) {
                        Surface(
                            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = firstCategory,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // --- 3. AI INSIGHTS PANEL ---
            AiInsightsPanel(aiAnalysis, isAiAnalyzing)

            // --- 4. TEXT CONTENT ---
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = post?.name ?: "Sin título",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Text(
                    text = post?.description ?: "Sin descripción",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    lineHeight = 24.sp
                )
            }

            // --- 5. METADATA GRID ---
            MetadataGrid(post)

            Spacer(modifier = Modifier.height(16.dp))

            // --- 6. ACTIONS ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { viewModel.approvePost { showApprovedModal = true } },
                    modifier = Modifier.weight(1.1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Aprobar")
                }
                
                OutlinedButton(
                    onClick = { viewModel.rejectPost { onBack() } },
                    modifier = Modifier.weight(0.9f).height(56.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Cancel, null, tint = Color(0xFFC62828))
                    Spacer(Modifier.width(8.dp))
                    Text("Rechazar")
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun AuthorCard(author: com.turistgo.app.domain.model.User?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF0F0F0)
            ) {
                if (author?.profilePhotoUrl != null) {
                    AsyncImage(
                        model = author.profilePhotoUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Person, null, Modifier.padding(12.dp), Color.LightGray)
                }
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text("${author?.name ?: "Cargando..."} ${author?.lastName ?: ""}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(author?.email ?: "verificando cuenta...", fontSize = 12.sp, color = Color.Gray)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${author?.followerIds?.size ?: 0}", 
                    fontWeight = FontWeight.ExtraBold, 
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("Seguidores", fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun AiInsightsPanel(analysis: String?, isLoading: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD).copy(alpha = 0.5f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF90CAF9).copy(alpha = 0.3f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, "IA", Modifier.size(18.dp), Color(0xFF1976D2))
                Spacer(Modifier.width(8.dp))
                Text("Análisis de IA (Gemini)", fontWeight = FontWeight.Bold, color = Color(0xFF1976D2), fontSize = 14.sp)
            }
            
            Spacer(Modifier.height(12.dp))
            
            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = Color(0xFF1976D2),
                    trackColor = Color.White
                )
                Spacer(Modifier.height(8.dp))
                Text("Analizando veracidad y contenido...", fontSize = 13.sp, color = Color.Gray)
            } else {
                Text(
                    text = analysis ?: "Generando reporte de seguridad...",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MetadataGrid(post: com.turistgo.app.domain.model.Post?) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        MetadataItem(Icons.Default.LocationOn, "Ubicación", "${post?.latitude}, ${post?.longitude}")
        MetadataItem(Icons.Default.Map, "Ciudad/Dpto", "${post?.city ?: "N/A"}")
        MetadataItem(Icons.Default.DateRange, "Fecha", "Reciente")
    }
}

@Composable
fun MetadataItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(16.dp), Color.Gray)
        Spacer(Modifier.width(8.dp))
        Text(label, fontSize = 13.sp, color = Color.Gray)
        Spacer(Modifier.width(4.dp))
        Text("•", color = Color.LightGray)
        Spacer(Modifier.width(4.dp))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}
