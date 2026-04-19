package com.turistgo.app.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.turistgo.app.R

data class BadgeData(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val isUnlocked: Boolean = false,
    val progress: Float = 0f
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgesScreen(
    onBack: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val warmBg = Color(0xFFFBFAF5)
    val stats by viewModel.profileStats.collectAsState()
    
    val badges = listOf(
        BadgeData(
            title = "Primer Paso", 
            description = "Publicaste tu primer destino", 
            icon = Icons.Default.Public, 
            isUnlocked = stats.postsCount >= 1,
            progress = if (stats.postsCount >= 1) 1f else 0f
        ),
        BadgeData(
            title = "Curador", 
            description = "Guardaste 5 destinos favoritos", 
            icon = Icons.Default.Bookmark, 
            isUnlocked = stats.savedCount >= 5,
            progress = (stats.savedCount.toFloat() / 5f).coerceAtMost(1f)
        ),
        BadgeData(
            title = "Entusiasta", 
            description = "Diste 10 'Me gusta' a otros", 
            icon = Icons.Default.Favorite, 
            isUnlocked = stats.likedCount >= 10,
            progress = (stats.likedCount.toFloat() / 10f).coerceAtMost(1f)
        ),
        BadgeData(
            title = "Crítico Experto", 
            description = "Diste 10 reseñas verificadas", 
            icon = Icons.Default.Star, 
            isUnlocked = false, 
            progress = 0f
        ),
        BadgeData(
            title = "Verificador", 
            description = "Ayudaste a verificar 5 lugares", 
            icon = Icons.Default.Verified, 
            isUnlocked = false, 
            progress = 0f
        ),
        BadgeData(
            title = "Socialite", 
            description = "Conectaste con 20 viajeros", 
            icon = Icons.Default.Group, 
            isUnlocked = false, 
            progress = 0.1f
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.badges_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = warmBg)
            )
        },
        containerColor = warmBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Tu Colección de Logros",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            Text(
                text = "Desbloquea insignias explorando el mundo",
                fontSize = 14.sp,
                color = Color(0xFF666666)
            )

            Spacer(modifier = Modifier.height(24.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(badges) { badge ->
                    BadgeCard(badge)
                }
            }
        }
    }
}

@Composable
fun BadgeCard(badge: BadgeData) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (badge.isUnlocked) Color.White else Color.White.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (badge.isUnlocked) 2.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                color = if (badge.isUnlocked) MaterialTheme.colorScheme.primaryContainer else Color(0xFFEEEEEE)
            ) {
                Icon(
                    imageVector = badge.icon,
                    contentDescription = null,
                    tint = if (badge.isUnlocked) MaterialTheme.colorScheme.primary else Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = badge.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = if (badge.isUnlocked) Color.Black else Color.Gray
            )
            
            Text(
                text = badge.description,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                color = if (badge.isUnlocked) Color.Gray else Color.LightGray,
                lineHeight = 14.sp,
                modifier = Modifier.height(28.dp)
            )
            
            if (!badge.isUnlocked) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { badge.progress },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    trackColor = Color(0xFFEEEEEE)
                )
            }
        }
    }
}
