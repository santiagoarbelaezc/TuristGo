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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.turistgo.app.R
import com.turistgo.app.core.components.Destination
import com.turistgo.app.core.components.DestinationCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    innerPadding: PaddingValues,
    destinationId: String?, 
    onBack: () -> Unit,
    onNavigateToUserProfile: (String) -> Unit = {},
    viewModel: PostDetailViewModel = hiltViewModel()
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
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text(stringResource(R.string.post_detail_title), fontSize = 18.sp, fontWeight = FontWeight.Bold) },
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
                    val isSaved by viewModel.isSaved.collectAsState()
                    val isLiked by viewModel.isLiked.collectAsState()
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
                            Icon(if (isVisited) Icons.Default.CheckCircle else Icons.Default.AddCircleOutline, null, Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isVisited) stringResource(R.string.visited) else stringResource(R.string.mark_visited), fontSize = 12.sp)
                        }
                        
                        IconButton(
                            onClick = { viewModel.toggleSave() },
                            modifier = Modifier
                                .background(
                                    if (isSaved) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(12.dp)
                                )
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Guardar",
                                tint = if (isSaved) Color(0xFFC62828) else MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(
                            onClick = { viewModel.toggleLike() },
                            modifier = Modifier
                                .background(
                                    if (isLiked) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(12.dp)
                                )
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Me gusta",
                                tint = if (isLiked) Color.Red else MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        IconButton(
                            onClick = { /* Share */ },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                                .size(40.dp)
                        ) {
                            Icon(Icons.Default.Share, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // SECCIÓN DE AUTOR - NUEVO
                    post?.let { p ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToUserProfile(p.authorId) },
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = p.authorPhotoUrl ?: "https://res.cloudinary.com/doxdjiyvi/image/upload/v1769405400/english-notebook/profiles/profile_69658edf82ad881040292fe6_1769405397996.jpg",
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = p.authorName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(text = "Ver perfil", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                                }
                                Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Detalles Adicionales
                    SectionHeader("Detalles del Lugar")
                    DetailRow(Icons.Default.Schedule, stringResource(R.string.schedule), schedule)
                    DetailRow(Icons.Default.AttachMoney, stringResource(R.string.price_range), priceRange)

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(text = stringResource(R.string.description), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = description, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 22.sp)
                }
            }

            // Sección de Comentarios
            item {
                val realComments by viewModel.comments.collectAsState(initial = emptyList())
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(text = stringResource(R.string.comments) + " (${realComments.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Input para nuevo comentario
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.write_experience)) },
                        trailingIcon = {
                            if (commentText.isNotEmpty()) {
                                IconButton(onClick = { 
                                    viewModel.addComment(commentText)
                                    commentText = "" 
                                }) {
                                    Icon(Icons.Default.Send, null, tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (realComments.isEmpty()) {
                        Text(
                            "Sé el primero en comentar",
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        realComments.forEach { comment ->
                             CommentItem(
                                author = comment.authorName, 
                                content = comment.content, 
                                time = "Justo ahora",
                                authorPhoto = comment.authorPhotoUrl
                            )
                        }
                    }
                    
                    if (realComments.size > 3) {
                        TextButton(onClick = { /* View more */ }) {
                            Text(stringResource(R.string.view_all_comments))
                        }
                    }
                }
            }

            // Publicaciones relacionadas
            item {
                Column(modifier = Modifier.padding(vertical = 20.dp)) {
                    Text(
                        text = stringResource(R.string.related_posts),
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
fun CommentItem(author: String, content: String, time: String, authorPhoto: String? = null) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        AsyncImage(
            model = authorPhoto ?: "https://res.cloudinary.com/doxdjiyvi/image/upload/v1769405400/english-notebook/profiles/profile_69658edf82ad881040292fe6_1769405397996.jpg",
            contentDescription = null,
            modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )
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
