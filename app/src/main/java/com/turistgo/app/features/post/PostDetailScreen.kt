package com.turistgo.app.features.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.turistgo.app.core.components.Destination
import com.turistgo.app.core.components.DestinationCard
import com.turistgo.app.features.feed.components.Person
import com.turistgo.app.features.feed.components.PersonItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    destinationId: String?, 
    onBack: () -> Unit,
    viewModel: PostDetailViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val post by viewModel.post.collectAsState()

    LaunchedEffect(destinationId) {
        destinationId?.let { viewModel.loadPost(it) }
    }

    // Data from the fetched post or defaults
    val title = post?.name ?: "Cargando..."
    val location = post?.location ?: "Ubicación..."
    val description = post?.description ?: "Cargando descripción..."
    val imageUrl = post?.imageUrl ?: ""
    val schedule = post?.schedule ?: "Horario no disponible"
    val priceRange = post?.priceRange ?: "Precio no disponible"

    var isVisited by remember { mutableStateOf(false) }
    var isImportant by remember { mutableStateOf(false) }
    var votesCount by remember { mutableIntStateOf(42) }
    var commentText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Detalle del Destino", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                IconButton(onClick = { /* Share */ }) {
                    Icon(Icons.Default.Share, contentDescription = "Compartir")
                }
                IconButton(onClick = { /* Save */ }) {
                    Icon(Icons.Default.BookmarkBorder, contentDescription = "Guardar")
                }
            },
            windowInsets = WindowInsets(0, 0, 0, 0)
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            // Imagen principal
            item {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // Información básica y Botones de Acción
            item {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, null, Modifier.size(16.dp), MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = location, color = MaterialTheme.colorScheme.secondary, fontSize = 14.sp)
                            }
                        }
                        
                        // Badge de Verificado
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFE8F5E9),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Verified, null, Modifier.size(14.dp), Color(0xFF2E7D32))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Verificado", color = Color(0xFF2E7D32), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botones Interactivos
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { isVisited = !isVisited },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isVisited) Color(0xFF2E7D32) else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (isVisited) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Icon(if (isVisited) Icons.Default.CheckCircle else Icons.Default.AddTask, null, Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isVisited) "Visitado" else "Marcar Visitado", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { 
                                if (isImportant) votesCount-- else votesCount++
                                isImportant = !isImportant 
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (isImportant) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = if (isImportant) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
                        ) {
                            Icon(if (isImportant) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt, null, Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Es Importante ($votesCount)", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Detalles Adicionales
                    SectionHeader("Detalles del Lugar")
                    DetailRow(Icons.Default.Schedule, "Horario", schedule)
                    DetailRow(Icons.Default.AttachMoney, "Rango de Precio", priceRange)

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(text = "Descripción", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = description, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 22.sp)
                }
            }

            // Sección de Comentarios
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(text = "Comentarios (12)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Input para nuevo comentario
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Escribe tu experiencia...") },
                        trailingIcon = {
                            if (commentText.isNotEmpty()) {
                                IconButton(onClick = { commentText = "" }) {
                                    Icon(Icons.Default.Send, null, tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    CommentItem("Juan David", "Excelente lugar para desconectarse.", "Hace 2 horas")
                    CommentItem("Sofía R.", "Recomiendo llevar mucha agua y protector solar.", "Hace 5 horas")
                    
                    TextButton(onClick = { /* View more */ }) {
                        Text("Ver todos los comentarios")
                    }
                }
            }

            // Publicaciones relacionadas
            item {
                Column(modifier = Modifier.padding(vertical = 20.dp)) {
                    Text(
                        text = "Publicaciones relacionadas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val related = listOf(
                            Destination("2", "San Andrés", "Isla", "4.8", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776142341/playa_qg2ifb.jpg"),
                            Destination("6", "Cartagena", "Bolívar", "4.7", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776142341/indias_ym97lb.jpg")
                        )
                        items(related) { dest ->
                            RelatedPostCard(dest)
                        }
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(20.dp), MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}


@Composable
fun CommentItem(author: String, content: String, time: String) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = author, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = time, color = MaterialTheme.colorScheme.secondary, fontSize = 12.sp)
            }
            Text(text = content, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun RelatedPostCard(destination: Destination) {
    Card(
        modifier = Modifier.size(200.dp, 150.dp),
        shape = RoundedCornerShape(16.dp)
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
            )
            Text(
                text = destination.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)
            )
        }
    }
}
