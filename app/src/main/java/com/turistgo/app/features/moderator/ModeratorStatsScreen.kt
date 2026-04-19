package com.turistgo.app.features.moderator

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.turistgo.app.R

@Composable
fun ModeratorStatsScreen(
    innerPadding: PaddingValues = PaddingValues(),
    viewModel: ModeratorStatsViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val stats by viewModel.stats.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        // Header matches Feed/Dashboard style
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Análisis",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = stringResource(R.string.stats_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Rendimiento y crecimiento de la plataforma",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
            )
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {

        // Hero Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatsGridCard(
                label = "Usuarios",
                value = stats.totalUsers.toString(),
                delta = if (stats.totalUsers > 0) "+1" else "0", // Simulación simple de delta
                icon = Icons.Default.Group,
                color = Color(0xFFE8EAF6),
                modifier = Modifier.weight(1f)
            )
            StatsGridCard(
                label = "Publicaciones",
                value = stats.totalPosts.toString(),
                delta = if (stats.totalPosts > 0) "+1" else "0",
                icon = Icons.Default.PostAdd,
                color = Color(0xFFE0F2F1),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Analytics Chart (User Growth)
        AnalyticsCard(title = "Crecimiento de Usuarios") {
            LineChart(
                data = listOf(0.2f, 0.4f, 0.3f, 0.7f, 0.6f, 0.9f, 0.8f),
                modifier = Modifier.fillMaxWidth().height(180.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Publication Status (Approved vs Pending)
        AnalyticsCard(title = "Estado de Publicaciones") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ComparisonBar(label = "Aprobadas", percentage = stats.approvedPercentage, color = Color(0xFF4CAF50), count = stats.approvedPosts.toString())
                ComparisonBar(label = "Pendientes", percentage = stats.pendingPercentage, color = Color(0xFFFB8C00), count = stats.pendingPosts.toString())
                ComparisonBar(label = "Rechazadas", percentage = stats.rejectedPercentage, color = Color(0xFFE53935), count = stats.rejectedPosts.toString())
            }
        }
    }
}
}

@Composable
fun StatsGridCard(label: String, value: String, delta: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, null, Modifier.size(24.dp), Color(0xFF333333))
            Spacer(Modifier.height(12.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            Text(label, fontSize = 12.sp, color = Color(0xFF555555))
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ArrowUpward, null, Modifier.size(12.dp), Color(0xFF4CAF50))
                Text(delta, fontSize = 10.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AnalyticsCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF333333))
            Spacer(Modifier.height(20.dp))
            content()
        }
    }
}

@Composable
fun LineChart(data: List<Float>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val spacing = width / (data.size - 1)
        
        val points = data.mapIndexed { index, value ->
            Offset(index * spacing, height * (1 - value))
        }
        
        val path = Path().apply {
            moveTo(points[0].x, points[0].y)
            for (i in 1 until points.size) {
                val p0 = points[i-1]
                val p1 = points[i]
                cubicTo(
                    (p0.x + p1.x) / 2, p0.y,
                    (p0.x + p1.x) / 2, p1.y,
                    p1.x, p1.y
                )
            }
        }
        
        drawPath(
            path = path,
            color = Color(0xFF5C6BC0),
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )
        
        // Draw points
        points.forEach { point ->
            drawCircle(Color(0xFF5C6BC0), radius = 4.dp.toPx(), center = point)
            drawCircle(Color.White, radius = 2.dp.toPx(), center = point)
        }
    }
}

@Composable
fun ComparisonBar(label: String, percentage: Float, color: Color, count: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 13.sp, color = Color(0xFF444444))
            Text(count, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(Color(0xFFF0F0F0))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
