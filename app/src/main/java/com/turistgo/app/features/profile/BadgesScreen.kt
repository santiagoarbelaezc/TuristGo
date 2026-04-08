package com.turistgo.app.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgesScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Logros e Insignias", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Sección de Niveles
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tu Progreso de Nivel",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                LevelProgression()
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = MaterialTheme.colorScheme.surfaceVariant)

            // Galería de Insignias
            Text(
                text = "Galería de Insignias",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(20.dp)
            )

            val badges = listOf(
                BadgeData("Primer Paso", "Crea tu primera publicación", Icons.Default.Add, true),
                BadgeData("Verificado", "10 publicaciones verificadas", Icons.Default.Check, true),
                BadgeData("Voz Local", "Recibe 20 votos positivos", Icons.Default.ThumbUp, true),
                BadgeData("Explorador", "Descubre 5 categorías diferentes", Icons.Default.Explore, false),
                BadgeData("社交 (Social)", "Comenta en 15 publicaciones", Icons.Default.Chat, false),
                BadgeData("Top del Mes", "Publicación más destacada", Icons.Default.EmojiEvents, false)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(badges) { badge ->
                    BadgeGalleryItem(badge)
                }
            }
        }
    }
}

@Composable
fun LevelProgression() {
    val levels = listOf("Turista", "Explorador", "Aventurero", "Embajador")
    val currentLevelIndex = 1 // Explorador

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        levels.forEachIndexed { index, level ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    modifier = Modifier.size(if (index <= currentLevelIndex) 40.dp else 32.dp),
                    shape = CircleShape,
                    color = if (index <= currentLevelIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = (index + 1).toString(),
                            color = if (index <= currentLevelIndex) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = level,
                    fontSize = 10.sp,
                    fontWeight = if (index == currentLevelIndex) FontWeight.Bold else FontWeight.Normal,
                    color = if (index == currentLevelIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            }
            if (index < levels.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(bottom = 12.dp)
                        .background(if (index < currentLevelIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        }
    }
}

data class BadgeData(val title: String, val desc: String, val icon: ImageVector, val unlocked: Boolean)

@Composable
fun BadgeGalleryItem(badge: BadgeData) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Surface(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape),
            color = if (badge.unlocked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Icon(
                imageVector = badge.icon,
                contentDescription = null,
                modifier = Modifier.padding(18.dp),
                tint = if (badge.unlocked) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = badge.title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = if (badge.unlocked) "¡Logrado!" else "Bloqueado",
            fontSize = 10.sp,
            color = if (badge.unlocked) Color(0xFF2E7D32) else Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}
