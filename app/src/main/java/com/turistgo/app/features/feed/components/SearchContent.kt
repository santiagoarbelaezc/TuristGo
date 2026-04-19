package com.turistgo.app.features.feed.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.turistgo.app.domain.model.Post

@Composable
fun SearchContent(
    results: List<Post>,
    onNavigateToDetail: (String) -> Unit
) {
    if (results.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.LocationOn, null, Modifier.size(64.dp), Color.LightGray)
                Text("No encontramos resultados para tu búsqueda", color = Color.Gray, modifier = Modifier.padding(top = 16.dp))
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Personas por descubrir (Always show as recommendation if search is just starting)
        item {
            Column {
                SectionHeader("Suguerencias para ti")
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    val people = listOf(
                        Person("Ana María", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1772039020/engin_akyurt-woman-4605248_640_ehaplz.jpg"),
                        Person("Carlos Ruiz", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1772039016/istockphoto-1550589735-612x612_lgfnwy.jpg"),
                        Person("Juan Pérez", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1772039018/photo-1599566150163-29194dcaad36_ql1jmh.avif"),
                        Person("Laura G.", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1772039017/818376-woman-657753_640_wv958x.jpg"),
                        Person("Sofia Castro", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776531326310.jpg"),
                        Person("Andrés M.", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1776531017303.jpg"),
                        Person("Valentina V.", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=256&q=80"),
                        Person("Diego F.", "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=256&q=80"),
                        Person("Camila R.", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=256&q=80"),
                        Person("Mateo T.", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=256&q=80"),
                        Person("Isabella G.", "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=256&q=80"),
                        Person("Sebastián L.", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=256&q=80")
                    )
                    items(people) { person ->
                        PersonItem(person)
                    }
                }
            }
        }

        // Search Results
        item {
            SectionHeader("Resultados encontrados (${results.size})")
        }

        items(results) { post ->
            SearchResultItem(post) { onNavigateToDetail(post.id) }
        }
    }
}

@Composable
fun SearchResultItem(post: Post, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = post.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val icon = when {
                        post.categories.contains("Events") || post.categories.contains("Eventos") -> Icons.Default.Event
                        post.categories.contains("Concerts") || post.categories.contains("Conciertos") -> Icons.Default.MusicNote
                        post.categories.contains("Sports") || post.categories.contains("Deportes") -> Icons.Default.SportsSoccer
                        else -> Icons.Default.LocationOn
                    }
                    Icon(icon, null, Modifier.size(14.dp), MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = post.categories.firstOrNull() ?: "General",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(text = post.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = post.location, fontSize = 13.sp, color = Color.Gray)
            }
            
            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

data class Person(val name: String, val avatar: String)

@Composable
fun PersonItem(person: Person) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            AsyncImage(
                model = person.avatar,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(20.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                border = BorderStroke(1.dp, Color.White)
            ) {
                Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.padding(3.dp))
            }
        }
        Text(text = person.name, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
    }
}
