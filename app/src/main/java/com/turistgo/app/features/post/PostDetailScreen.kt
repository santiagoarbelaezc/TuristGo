package com.turistgo.app.features.post

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.turistgo.app.R
import com.turistgo.app.core.components.Destination
import com.turistgo.app.core.components.DestinationCard
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    innerPadding: PaddingValues,
    destinationId: String?, 
    onBack: () -> Unit,
    onNavigateToUserProfile: (String) -> Unit = {},
    onNavigateToPostDetail: (String) -> Unit = {},
    viewModel: PostDetailViewModel = hiltViewModel()
) {
    val post by viewModel.post.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    val isLiked by viewModel.isLiked.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val relatedPosts by viewModel.relatedPosts.collectAsState()
    val userSuggestions by viewModel.userSuggestions.collectAsState()
    val moderationAlert by viewModel.moderationAlert.collectAsState()
    val pointsEarned by viewModel.pointsEarned.collectAsState()
    val isSubmittingComment by viewModel.isSubmittingComment.collectAsState()

    if (pointsEarned != null) {
        com.turistgo.app.core.components.PointsEarnedModal(
            points = pointsEarned!!,
            reason = "Por escribir un comentario e inspirar a la comunidad",
            onDismiss = { viewModel.clearPointsEarned() }
        )
    }

    var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri
    }

    LaunchedEffect(destinationId) {
        destinationId?.let { viewModel.loadPost(it) }
    }

    // --- DIÁLOGO DE MODERACIÓN POR IA (PREMIUM) ---
    com.turistgo.app.core.components.TuristGoDialog(
        state = moderationAlert,
        onDismiss = { viewModel.dismissModerationAlert() }
    )

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
    
    val sharePost = {
        post?.let { p ->
            val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(android.content.Intent.EXTRA_TEXT, "¡Mira este lugar en TuristGo! https://turistgo.app/post/${p.id}")
            }
            context.startActivity(android.content.Intent.createChooser(shareIntent, "Compartir destino"))
        }
    }

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
                IconButton(onClick = { sharePost() }) {
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
                            onClick = { sharePost() },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                                .size(40.dp)
                        ) {
                            Icon(Icons.Default.Share, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // SECCIÓN DE AUTOR - NUEVO
                    if (post != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToUserProfile(post!!.authorId) },
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = post!!.authorPhotoUrl ?: com.turistgo.app.R.raw.usuario,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = post!!.authorName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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

                    // Mapa de ubicación interactivo con botón de trazado de ruta
                    if (post?.latitude != null && post?.longitude != null) {
                        Spacer(modifier = Modifier.height(24.dp))
                        SectionHeader("Ubicación en el Mapa")
                        Spacer(modifier = Modifier.height(8.dp))
                        val postLocation = LatLng(post!!.latitude!!, post!!.longitude!!)
                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(postLocation, 15f)
                        }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState,
                                uiSettings = MapUiSettings(
                                    zoomControlsEnabled = false,
                                    mapToolbarEnabled = true,
                                    myLocationButtonEnabled = false
                                )
                            ) {
                                Marker(
                                    state = MarkerState(position = postLocation),
                                    title = title
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Botón premium para abrir en Google Maps con trazado de ruta
                        Button(
                            onClick = {
                                try {
                                    val intentUri = android.net.Uri.parse("google.navigation:q=${post!!.latitude},${post!!.longitude}")
                                    val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, intentUri).apply {
                                        setPackage("com.google.android.apps.maps")
                                    }
                                    context.startActivity(mapIntent)
                                } catch (e: Exception) {
                                    // Fallback: Abrir en navegador usando direcciones de Google Maps
                                    val mapWebUri = android.net.Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${post!!.latitude},${post!!.longitude}")
                                    val browserIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, mapWebUri)
                                    context.startActivity(browserIntent)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Trazar ruta",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Abrir en Google Maps",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }


            // Sección de Comentarios
            item {
                val realComments by viewModel.comments.collectAsState(initial = emptyList())
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(text = stringResource(R.string.comments) + " (${realComments.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    // Input para nuevo comentario
                    if (selectedImageUri != null) {
                        Box(modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth().height(150.dp).clip(RoundedCornerShape(12.dp))) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { selectedImageUri = null },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                    .size(30.dp)
                            ) {
                                Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { if (!isSubmittingComment) commentText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.write_experience)) },
                        enabled = !isSubmittingComment,
                        leadingIcon = {
                            IconButton(
                                onClick = { 
                                    photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) 
                                },
                                enabled = !isSubmittingComment
                            ) {
                                Icon(Icons.Default.Image, "Adjuntar foto", tint = MaterialTheme.colorScheme.primary)
                            }
                        },
                        trailingIcon = {
                            if (isSubmittingComment) {
                                // Indicador de carga mientras la IA valida
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp).padding(end = 4.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else if (commentText.isNotEmpty() || selectedImageUri != null) {
                                IconButton(onClick = { 
                                    viewModel.addComment(
                                        context,
                                        commentText,
                                        selectedImageUri?.toString(),
                                        onSuccess = {
                                            commentText = ""
                                            selectedImageUri = null
                                        }
                                    )
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
                                time = formatCommentTime(comment.timestamp),
                                authorPhoto = comment.authorPhotoUrl,
                                imageUrl = comment.imageUrl,
                                onProfileClick = { onNavigateToUserProfile(comment.authorId) }
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
            if (relatedPosts.isNotEmpty()) {
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
                            items(relatedPosts) { p ->
                                val dest = Destination(p.id, p.name, p.location, p.rating, p.imageUrl, p.commentCount)
                                RelatedPostCard(destination = dest, onClick = { onNavigateToPostDetail(p.id) })
                            }
                        }
                    }
                }
            }

            // Sugerencias de amistad
            if (userSuggestions.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(bottom = 20.dp)) {
                        Text(
                            text = "Sugerencias de amistad",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(userSuggestions) { user ->
                                com.turistgo.app.features.feed.components.UserSuggestionItem(
                                    user = user, 
                                    onClick = { onNavigateToUserProfile(user.id) }
                                )
                            }
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
fun CommentItem(
    author: String, 
    content: String, 
    time: String, 
    authorPhoto: String? = null,
    imageUrl: String? = null,
    onProfileClick: () -> Unit = {}
) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        AsyncImage(
            model = authorPhoto ?: com.turistgo.app.R.raw.usuario,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { onProfileClick() },
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = author, 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onProfileClick() }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = time, color = MaterialTheme.colorScheme.secondary, fontSize = 12.sp)
            }
            if (content.isNotEmpty()) {
                Text(text = content, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (imageUrl != null) {
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Imagen adjunta",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun RelatedPostCard(destination: Destination, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.size(200.dp, 150.dp).clickable { onClick() },
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

/** Convierte un timestamp en texto relativo legible en español. */
fun formatCommentTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60_000L              -> "Justo ahora"
        diff < 3_600_000L           -> "${diff / 60_000}m"
        diff < 86_400_000L          -> "${diff / 3_600_000}h"
        diff < 7 * 86_400_000L      -> "${diff / 86_400_000}d"
        else -> {
            val sdf = java.text.SimpleDateFormat("dd MMM", java.util.Locale("es", "CO"))
            sdf.format(java.util.Date(timestamp))
        }
    }
}
