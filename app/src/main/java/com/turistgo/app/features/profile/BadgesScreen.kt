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
fun BadgesScreen(onBack: () -> Unit = {}) {
    val warmBg = Color(0xFFFBFAF5)
    
    val badges = listOf(
        BadgeData("Primer Explorador", "Publicaste tu primer destino", Icons.Default.Public, true),
        BadgeData("Crítico Experto", "Diste 10 reseñas verificadas", Icons.Default.Star, true),
        BadgeData("Verificador", "Ayudaste a verificar 5 lugares", Icons.Default.Verified, true),
        BadgeData("Socialite", "Conectaste con 20 viajeros", Icons.Default.Group, false, 0.45f),
        BadgeData("Reportero", "Informaste sobre 3 eventos", Icons.Default.Event, false, 0.66f),
        BadgeData("Aventurero", "Visitaste 5 departamentos", Icons.Default.Explore, false, 0.2f),
        BadgeData("Gourmet", "Reseñaste 5 restaurantes", Icons.Default.Restaurant, true),
        BadgeData("Búho Nocturno", "Visitaste 3 lugares de noche", Icons.Default.NightsStay, false, 0.33f)
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
