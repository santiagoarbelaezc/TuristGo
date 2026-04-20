package com.turistgo.app.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.turistgo.app.R
import com.turistgo.app.core.components.SmallDestinationCard
import com.turistgo.app.core.components.Destination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicProfileScreen(
    innerPadding: PaddingValues,
    userId: String,
    onBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: PublicProfileViewModel = hiltViewModel()
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val posts by viewModel.userPosts.collectAsState()
    val stats by viewModel.profileStats.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    val profileImageUrl = userProfile?.profilePhotoUrl 
        ?: "https://res.cloudinary.com/doxdjiyvi/image/upload/v1769405400/english-notebook/profiles/profile_69658edf82ad881040292fe6_1769405397996.jpg"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.background)
    ) {
        CenterAlignedTopAppBar(
            title = { Text(userProfile?.name ?: "Perfil", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            windowInsets = WindowInsets(0, 0, 0, 0)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                AsyncImage(
                    model = profileImageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = userProfile?.name ?: "Usuario",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
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
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = if (stats.levelNumber > 0) "${stats.levelName} Nivel ${stats.levelNumber}" else stats.levelName,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(label = stringResource(R.string.stat_posts), value = stats.postsCount.toString())
                    StatItem(label = stringResource(R.string.stat_followers), value = stats.followersCount.toString())
                    StatItem(label = stringResource(R.string.stat_following), value = stats.followingCount.toString())
                }

                val isFollowing by viewModel.isFollowing.collectAsState()
                val isMe by viewModel.isMe.collectAsState()

                if (!isMe) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.requestFollow() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFollowing) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                            contentColor = if (isFollowing) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                        )
                    ) {
                        Icon(
                            imageVector = if (isFollowing) Icons.Default.Check else Icons.Default.PersonAdd,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = if (isFollowing) "Siguiendo" else "Seguir", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(
                    text = "Publicaciones",
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (posts.isEmpty()) {
                item {
                    Text(
                        text = "Este usuario no tiene publicaciones aún.",
                        modifier = Modifier.padding(24.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(posts.chunked(2)) { rowPosts ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowPosts.forEach { post ->
                            Box(modifier = Modifier.weight(1f)) {
                                SmallDestinationCard(
                                    destination = Destination(
                                        post.id, post.name, post.location, post.rating, post.imageUrl, post.commentCount
                                    ),
                                    onClick = { onNavigateToDetail(post.id) }
                                )
                            }
                        }
                        if (rowPosts.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
