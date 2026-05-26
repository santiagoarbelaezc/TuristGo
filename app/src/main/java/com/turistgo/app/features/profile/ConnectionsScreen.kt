package com.turistgo.app.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.turistgo.app.domain.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionsScreen(
    userId: String,
    initialTab: Int,
    onBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    viewModel: ConnectionsViewModel = hiltViewModel()
) {
    val targetUser by viewModel.targetUser.collectAsState()
    val followers by viewModel.followers.collectAsState()
    val following by viewModel.following.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(initialTab) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = targetUser?.name ?: "Conexiones",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.background,
            divider = {}
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("Seguidores", fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.secondary
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Siguiendo", fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.secondary
            )
        }

        val listToShow = if (selectedTabIndex == 0) followers else following

        if (listToShow.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (selectedTabIndex == 0) "Aún no tiene seguidores" else "Aún no sigue a nadie",
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(listToShow) { user ->
                    ConnectionUserItem(user = user, onClick = { onNavigateToProfile(user.id) })
                }
            }
        }
    }
}

@Composable
fun ConnectionUserItem(user: User, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.profilePhotoUrl ?: "https://res.cloudinary.com/doxdjiyvi/image/upload/v1769405400/english-notebook/profiles/profile_69658edf82ad881040292fe6_1769405397996.jpg",
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            if (!user.username.isNullOrEmpty()) {
                Text(text = "@${user.username}", color = MaterialTheme.colorScheme.secondary, fontSize = 14.sp)
            }
        }
    }
}
