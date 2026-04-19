package com.turistgo.app.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.turistgo.app.R
import com.turistgo.app.features.moderator.AnalyticsCard
import com.turistgo.app.features.moderator.ComparisonBar
import com.turistgo.app.features.moderator.LineChart
import com.turistgo.app.features.moderator.StatsGridCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserStatsScreen(onBack: () -> Unit = {}) {
    val scrollState = rememberScrollState()
    val warmBg = Color(0xFFFBFAF5)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.stats_title), fontWeight = FontWeight.Bold) },
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
                .verticalScroll(scrollState)
                .padding(24.dp)
        ) {
            Text(
                text = "Tu Impacto en TuristGo",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            Text(
                text = "Resumen de tus contribuciones y nivel",
                fontSize = 14.sp,
                color = Color(0xFF666666)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // User Specific Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatsGridCard(
                    label = "Lugares Visitados",
                    value = "24",
                    delta = "+3",
                    icon = Icons.Default.Place,
                    color = Color(0xFFE8EAF6),
                    modifier = Modifier.weight(1f)
                )
                StatsGridCard(
                    label = "Reseñas Dadas",
                    value = "12",
                    delta = "+2",
                    icon = Icons.Default.Star,
                    color = Color(0xFFFFF3E0),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Points Evolution
            AnalyticsCard(title = "Evolución de Puntos") {
                LineChart(
                    data = listOf(0.1f, 0.2f, 0.5f, 0.45f, 0.7f, 0.85f, 1.0f),
                    modifier = Modifier.fillMaxWidth().height(180.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Level Progress
            AnalyticsCard(title = "Progreso de Nivel") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ComparisonBar(label = "Explorador (Nivel 2)", percentage = 0.65f, color = Color(0xFF5C6BC0), count = "1,250 / 2,000 pts")
                    Text(
                        stringResource(R.string.points_to_next),
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
