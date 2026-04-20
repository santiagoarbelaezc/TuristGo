package com.turistgo.app.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
// NavController import omitted
import com.turistgo.app.core.navigation.MainRoutes

import androidx.hilt.navigation.compose.hiltViewModel
import com.turistgo.app.data.datastore.UserSessionManager
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.turistgo.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    innerPadding: PaddingValues,
    onNavigateToSettings: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToBadges: () -> Unit,
    onNavigateToProgressGuide: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToEditPost: (String) -> Unit,
    onNavigateToPostDetail: (String) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val userSession by viewModel.userSession.collectAsState(initial = null)
    val userProfile by viewModel.userProfile.collectAsState(initial = null)
    val stats by viewModel.profileStats.collectAsState()

    // Prioritize user profile photo, then session photo, then default placeholder
    val profileImageUrl = userProfile?.profilePhotoUrl 
        ?: userSession?.photoUrl 
        ?: "https://res.cloudinary.com/doxdjiyvi/image/upload/v1769405400/english-notebook/profiles/profile_69658edf82ad881040292fe6_1769405397996.jpg"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Custom TopBar Area
        CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.my_profile), fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                windowInsets = WindowInsets(0, 0, 0, 0)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                // Header del Perfil
                Spacer(modifier = Modifier.height(16.dp))
                
                // Foto de Perfil
                Box(contentAlignment = Alignment.BottomEnd) {
                    AsyncImage(
                        model = profileImageUrl,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop
                    )
                    // Botón para editar foto
                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        shadowElevation = 4.dp
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nombre y Nivel
                Text(
                    text = userProfile?.name ?: userSession?.name ?: "Usuario",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth()
                )
                
                if (userProfile?.username?.isNotEmpty() == true) {
                    Text(
                        text = "@${userProfile?.username}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(top = 4.dp).clickable { onNavigateToProgressGuide() }
                ) {
                    Text(
                        text = if (stats.levelNumber > 0) 
                            "${stats.levelName} Nivel ${stats.levelNumber}"
                        else 
                            stats.levelName,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Estadísticas Dashboard (Dinámicas)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(label = stringResource(R.string.stat_posts), value = stats.postsCount.toString())
                    StatItem(label = stringResource(R.string.stat_followers), value = stats.followersCount.toString())
                    StatItem(label = stringResource(R.string.stat_following), value = stats.followingCount.toString())
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Reputación e Insignias (Dinámicas)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = stringResource(R.string.reputation),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable { onNavigateToProgressGuide() },
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    if (stats.levelNumber > 0) "${stats.levelName} Nivel ${stats.levelNumber}" else stats.levelName, 
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text("${stats.points} pts", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { stats.levelProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                if (stats.levelNumber < 3) 
                                    stringResource(R.string.points_to_next_raw, stats.nextLevelPoints - stats.points)
                                else 
                                    stringResource(R.string.max_level_reached),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    if (stats.badgesCount > 0) {
                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = stringResource(R.string.my_badges),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (stats.postsCount >= 1) BadgeItem(stringResource(R.string.badge_first_step), Icons.Default.Public)
                            if (stats.savedCount >= 5) BadgeItem(stringResource(R.string.badge_curator), Icons.Default.Bookmark)
                            if (stats.likedCount >= 10) BadgeItem(stringResource(R.string.badge_enthusiast), Icons.Default.Favorite)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                var selectedTab by remember { mutableIntStateOf(0) }
                val myPosts by viewModel.myPosts.collectAsState(initial = emptyList())
                val savedPosts by viewModel.savedPosts.collectAsState(initial = emptyList())
                val likedPosts by viewModel.likedPosts.collectAsState(initial = emptyList())
                
                val tabs = listOf(
                    stringResource(R.string.tab_my_posts) to myPosts.size,
                    stringResource(R.string.favorites) to savedPosts.size,
                    stringResource(R.string.tab_likes) to likedPosts.size
                )
                
                PrimaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.background,
                    divider = {},
                    indicator = { 
                        TabRowDefaults.PrimaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(selectedTab),
                            width = 64.dp, // Volvemos al ancho original
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { 
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 12.dp)
                                ) {
                                    Text(
                                        title.first, 
                                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 14.sp,
                                        maxLines = 1
                                    )
                                }
                            },
                            unselectedContentColor = MaterialTheme.colorScheme.secondary,
                            selectedContentColor = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    if (selectedTab == 0) {
                        // Mis Publicaciones
                        val myPosts by viewModel.myPosts.collectAsState(initial = emptyList())
                        if (myPosts.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                items(myPosts) { post ->
                                    MyPostItem(post) {
                                        onNavigateToPostDetail(post.id)
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = "Aún no tienes publicaciones.",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }
                    } else if (selectedTab == 1) {
                        // Guardados (Grid 2 columnas)
                        val savedPosts by viewModel.savedPosts.collectAsState(initial = emptyList())
                        if (savedPosts.isNotEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                savedPosts.chunked(2).forEach { rowPosts ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        rowPosts.forEach { post ->
                                            Box(modifier = Modifier.weight(1f)) {
                                                com.turistgo.app.core.components.SmallDestinationCard(
                                                    destination = com.turistgo.app.core.components.Destination(
                                                        post.id, post.name, post.location, post.rating, post.imageUrl
                                                    ),
                                                    isSaved = true,
                                                    onSaveToggle = { viewModel.toggleSave(post.id) },
                                                    onClick = { /* Navigate to detail? */ }
                                                )
                                            }
                                        }
                                        // Empty spacer to fill row if it has only 1 element
                                        if (rowPosts.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = "No tienes publicaciones guardadas.",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }
                    } else {
                        // Me gusta (Grid 2 columnas)
                        val likedPosts by viewModel.likedPosts.collectAsState(initial = emptyList())
                        if (likedPosts.isNotEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                likedPosts.chunked(2).forEach { rowPosts ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        rowPosts.forEach { post ->
                                            Box(modifier = Modifier.weight(1f)) {
                                                com.turistgo.app.core.components.SmallDestinationCard(
                                                    destination = com.turistgo.app.core.components.Destination(
                                                        post.id, post.name, post.location, post.rating, post.imageUrl
                                                    ),
                                                    isLiked = true,
                                                    onLikeToggle = { viewModel.toggleLike(post.id) },
                                                    onClick = { /* Navigate to detail? */ }
                                                )
                                            }
                                        }
                                        // Empty spacer to fill row if it has only 1 element
                                        if (rowPosts.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = "Aún no tienes publicaciones favoritas.",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                
                Text(
                    text = "Opciones",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 12.dp)
                )
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Default.Person, 
                    title = stringResource(R.string.edit_profile),
                    onClick = onNavigateToEditProfile
                )
                ProfileMenuItem(
                    icon = Icons.Default.BarChart, 
                    title = stringResource(R.string.detailed_stats),
                    onClick = onNavigateToStats
                )
                ProfileMenuItem(
                    icon = Icons.Default.MilitaryTech,
                    title = stringResource(R.string.my_badges),
                    onClick = onNavigateToBadges
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(stringResource(R.string.logout), fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun MyPostItem(post: com.turistgo.app.domain.model.Post, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .size(140.dp, 180.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = post.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = post.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                val (statusText, statusColor) = when(post.status) {
                    com.turistgo.app.domain.model.PostStatus.APPROVED -> "Verificado" to Color(0xFF2E7D32)
                    com.turistgo.app.domain.model.PostStatus.PENDING -> "Pendiente" to Color(0xFFED6C02)
                    com.turistgo.app.domain.model.PostStatus.REJECTED -> "Rechazado" to Color(0xFFD32F2F)
                }
                Text(
                    text = statusText,
                    fontSize = 10.sp,
                    color = statusColor
                )
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun BadgeItem(label: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(60.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp)
            )
        }
        Text(
            text = label,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 4.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, title: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
