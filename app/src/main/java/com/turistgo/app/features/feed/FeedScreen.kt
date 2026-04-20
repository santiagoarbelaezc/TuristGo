package com.turistgo.app.features.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turistgo.app.core.components.Destination
import com.turistgo.app.core.components.DestinationCard
import com.turistgo.app.features.feed.components.FeedSearchBar
import com.turistgo.app.features.feed.components.SearchContent
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.turistgo.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    innerPadding: PaddingValues,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToUserProfile: (String) -> Unit,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val userSession by viewModel.userSession.collectAsState(initial = null)
    val suggestedUsers by viewModel.suggestedUsers.collectAsState()
    
    // Search & Filter State from ViewModel
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.searchCategory.collectAsState()
    val filteredPosts by viewModel.filteredPosts.collectAsState()

    // UI Local State
    var isSearchActive by remember { mutableStateOf(false) }
    var isMapView by remember { mutableStateOf(false) }
    // Categories depend on search mode
    val feedCategories = listOf(
        stringResource(R.string.cat_all), stringResource(R.string.cat_mountain), stringResource(R.string.cat_beach),
        stringResource(R.string.cat_gastronomy), stringResource(R.string.cat_culture), stringResource(R.string.cat_adventure)
    )
    
    val searchFilters = listOf(
        stringResource(R.string.filter_all), stringResource(R.string.filter_events), stringResource(R.string.filter_places),
        stringResource(R.string.filter_concerts), stringResource(R.string.filter_sports)
    )

    val displayedCategories = if (isSearchActive) searchFilters else feedCategories

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = innerPadding.calculateBottomPadding())
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // ... (TopAppBar content remains the same)
        TopAppBar(
            title = {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(6.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary
                        ) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.welcome_msg, userSession?.name ?: stringResource(R.string.default_user)),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Text(
                        text = stringResource(R.string.explore_world),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = (-0.5).sp
                    )
                }
            },
            actions = {
                Surface(
                    onClick = { isMapView = !isMapView },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isMapView) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.height(40.dp).padding(end = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isMapView) Icons.AutoMirrored.Filled.List else Icons.Default.Map,
                            contentDescription = stringResource(R.string.change_view),
                            tint = if (isMapView) Color.White else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isMapView) "Lista" else "Mapa",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isMapView) Color.White else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                scrolledContainerColor = MaterialTheme.colorScheme.background
            ),
            windowInsets = WindowInsets(0, 0, 0, 0)
        )

        // Search bar
        val context = LocalContext.current
        FeedSearchBar(
            query = searchQuery,
            onQueryChange = { 
                viewModel.updateSearchQuery(it)
                if (it.isEmpty() && !isSearchActive) {
                    // Stay inactive if empty and not focused
                } else {
                    isSearchActive = true
                }
            },
            onFocusChange = { active ->
                isSearchActive = active || searchQuery.isNotEmpty()
                if (!isSearchActive) {
                    viewModel.updateSearchCategory(context.getString(R.string.cat_all))
                }
            }
        )

        // Filter chips (Search vs Feed)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(displayedCategories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { viewModel.updateSearchCategory(category) },
                    label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            if (isSearchActive) {
                SearchContent(
                    results = filteredPosts,
                    suggestedUsers = suggestedUsers,
                    onNavigateToDetail = onNavigateToDetail,
                    onNavigateToProfile = onNavigateToUserProfile
                )
            } else if (isMapView) {
                val destinations = filteredPosts.map { 
                    Destination(it.id, it.name, it.location, it.rating, it.imageUrl)
                }
                MapPlaceholder(destinations)
            } else {
                val savedPostIds by viewModel.savedPostIds.collectAsState()
                val likedPostIds by viewModel.likedPostIds.collectAsState()
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = stringResource(R.string.popular_destinations),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(filteredPosts) { post ->
                        val destination = Destination(
                            id = post.id, 
                            name = post.name, 
                            location = post.location, 
                            rating = post.rating, 
                            imageUrl = post.imageUrl,
                            commentCount = post.commentCount,
                            createdAt = post.createdAt
                        )
                        DestinationCard(
                            destination = destination,
                            isSaved = savedPostIds.contains(post.id),
                            isLiked = likedPostIds.contains(post.id),
                            onSaveToggle = { viewModel.toggleSave(post.id) },
                            onLikeToggle = { viewModel.toggleLike(post.id) },
                            onClick = { onNavigateToDetail(post.id) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                }
            }
        }
    }
}

// MapPlaceholder and other private Composables remain the same (MapPlaceholder implementation omitted for brevity as per existing file structure)
@Composable
fun MapPlaceholder(destinations: List<com.turistgo.app.core.components.Destination>) {
    // Existing implementation...
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        Text("Mapa Interactivo cargado con ${destinations.size} destinos")
    }
}
