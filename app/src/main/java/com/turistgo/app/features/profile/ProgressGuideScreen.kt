package com.turistgo.app.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.turistgo.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressGuideScreen(onBack: () -> Unit) {
    val warmBg = Color(0xFFFBFAF5)
    var selectedTab by remember { mutableIntStateOf(0) }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.progress_guide_title), fontWeight = FontWeight.Bold) },
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
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = warmBg,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(stringResource(R.string.tab_levels), fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(stringResource(R.string.tab_reputation), fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text(stringResource(R.string.tab_badges), fontWeight = FontWeight.Bold) }
                )
            }

            Box(modifier = Modifier.weight(1f).padding(24.dp)) {
                when (selectedTab) {
                    0 -> LevelsGuide()
                    1 -> ReputationGuide()
                    2 -> BadgesGuide()
                }
            }
        }
    }
}

@Composable
fun LevelsGuide() {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text(stringResource(R.string.level_guide_title), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(stringResource(R.string.level_guide_desc), fontSize = 14.sp, color = Color.Gray)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        LevelStep(
            level = stringResource(R.string.level_0_name),
            requirement = stringResource(R.string.level_0_req),
            description = stringResource(R.string.level_0_desc),
            icon = Icons.Default.PersonOutline,
            color = Color.Gray
        )
        LevelStep(
            level = stringResource(R.string.level_1_name),
            requirement = stringResource(R.string.level_1_req),
            description = stringResource(R.string.level_1_desc),
            icon = Icons.Default.Explore,
            color = Color(0xFF4CAF50)
        )
        LevelStep(
            level = stringResource(R.string.level_2_name),
            requirement = stringResource(R.string.level_2_req),
            description = stringResource(R.string.level_2_desc),
            icon = Icons.Default.AirplanemodeActive,
            color = Color(0xFF2196F3)
        )
        LevelStep(
            level = stringResource(R.string.level_3_name),
            requirement = stringResource(R.string.level_3_req),
            description = stringResource(R.string.level_3_desc),
            icon = Icons.Default.MilitaryTech,
            color = Color(0xFFFF9800)
        )
    }
}

@Composable
fun ReputationGuide() {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("Sistema de Reputación", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Gana puntos por cada acción positiva que realices.", fontSize = 14.sp, color = Color.Gray)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        ReputationCard(
            action = stringResource(R.string.action_publish_dest),
            points = "+100 pts",
            icon = Icons.Default.AddPhotoAlternate
        )
        ReputationCard(
            action = stringResource(R.string.action_save_place),
            points = "+10 pts",
            icon = Icons.Default.Bookmark
        )
        ReputationCard(
            action = stringResource(R.string.action_give_like),
            points = "+10 pts",
            icon = Icons.Default.Favorite
        )
        ReputationCard(
            action = "Recibir verificación",
            points = "+50 pts",
            icon = Icons.Default.CheckCircle,
            isComingSoon = true
        )
    }
}

@Composable
fun BadgesGuide() {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("Manual de Insignias", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Logros especiales por hitos específicos.", fontSize = 14.sp, color = Color.Gray)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        BadgeRowGuide(
            name = stringResource(R.string.badge_first_step),
            requirement = stringResource(R.string.badge_first_step_req),
            icon = Icons.Default.Public
        )
        BadgeRowGuide(
            name = stringResource(R.string.badge_curator),
            requirement = stringResource(R.string.badge_curator_req),
            icon = Icons.Default.Bookmark
        )
        BadgeRowGuide(
            name = stringResource(R.string.badge_enthusiast),
            requirement = stringResource(R.string.badge_enthusiast_req),
            icon = Icons.Default.Favorite
        )
    }
}

@Composable
fun LevelStep(level: String, requirement: String, description: String, icon: ImageVector, color: Color) {
    Row(modifier = Modifier.padding(bottom = 24.dp)) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = color.copy(alpha = 0.1f)
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.padding(12.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(level, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(requirement, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(description, fontSize = 13.sp, color = Color.DarkGray)
        }
    }
}

@Composable
fun ReputationCard(action: String, points: String, icon: ImageVector, isComingSoon: Boolean = false) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(action, fontWeight = FontWeight.Bold)
                if (isComingSoon) Text(stringResource(R.string.coming_soon), fontSize = 10.sp, color = Color.Gray)
            }
            Text(points, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun BadgeRowGuide(name: String, requirement: String, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(16.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(requirement, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
