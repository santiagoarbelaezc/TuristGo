package com.turistgo.app.features.moderator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.res.stringResource
import com.turistgo.app.R
import com.turistgo.app.domain.model.Post
import com.turistgo.app.domain.model.PostStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeratorDashboard(
    innerPadding: PaddingValues,
    onLogout: () -> Unit,
    onReviewPost: (String) -> Unit,
    viewModel: ModeratorViewModel = hiltViewModel()
) {
    val posts by viewModel.posts.collectAsState()
    val pendingCount = posts.count { it.status == PostStatus.PENDING }
    val verifiedCount = posts.count { it.status == PostStatus.APPROVED }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar matches Feed/Profile style
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Panel",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Moderador",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            IconButton(
                onClick = onLogout,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = stringResource(R.string.logout),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = 24.dp,
                vertical = 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Lavender/Purple Card for "Pendientes"
                    StatCardRedesigned(
                        label = stringResource(R.string.pending_posts),
                        count = pendingCount.toString(),
                        backgroundColor = Color(0xFFEDE7F6), // Light Purple
                        iconColor = Color(0xFF512DA8),
                        icon = Icons.Default.AssignmentLate,
                        modifier = Modifier.weight(1f)
                    )
                    // Mint/Green Card for "Verificadas"
                    StatCardRedesigned(
                        label = stringResource(R.string.resolved_posts),
                        count = verifiedCount.toString(),
                        backgroundColor = Color(0xFFE8F5E9), // Light Green
                        iconColor = Color(0xFF2E7D32),
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Publicaciones Pendientes",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
            }

            if (pendingCount == 0) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(40.dp), 
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hay publicaciones pendientes", color = Color.Gray)
                    }
                }
            } else {
                items(posts.filter { it.status == PostStatus.PENDING }) { post ->
                    ModeratorPostCardRedesigned(post) {
                        onReviewPost(post.id)
                    }
                }
            }
        }
    }
}

@Composable
fun StatCardRedesigned(
    label: String, 
    count: String, 
    backgroundColor: Color, 
    iconColor: Color, 
    icon: androidx.compose.ui.graphics.vector.ImageVector, 
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Icon(
                imageVector = icon, 
                contentDescription = null, 
                tint = iconColor, 
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = count, 
                fontSize = 28.sp, 
                fontWeight = FontWeight.ExtraBold, 
                color = Color(0xFF1A1A1A)
            )
            Text(
                text = label, 
                fontSize = 14.sp, 
                color = Color(0xFF555555).copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ModeratorPostCardRedesigned(post: Post, onClick: () -> Unit) {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateString = if (post.createdAt > 0) sdf.format(Date(post.createdAt)) else "Pendiente de fecha"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = post.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = post.name, 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 17.sp, 
                    color = Color(0xFF1A1A1A),
                    maxLines = 1
                )
                Text(
                    text = "Por: ${post.authorName}", 
                    fontSize = 14.sp, 
                    color = Color.Gray
                )
                Text(
                    text = dateString, 
                    fontSize = 13.sp, 
                    color = Color.LightGray.copy(alpha = 0.9f)
                )
                
                // AI Badge matching the screenshot (Lavender tone)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF3E5F5), // Very light lavender
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome, 
                            contentDescription = null, 
                            modifier = Modifier.size(14.dp), 
                            tint = Color(0xFF9C27B0)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI: Categoría validada", 
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = Color(0xFF9C27B0)
                        )
                    }
                }
            }
            Icon(
                imageVector = Icons.Default.HourglassEmpty,
                contentDescription = null,
                tint = Color(0xFFE57373), // Soft red/orange
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
