package com.turistgo.app.ui.feed.components

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

@Composable
fun SearchContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Personas por descubrir
        item {
            Column {
                SectionHeader("Personas por descubrir")
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    val people = listOf(
                        Person("Ana María", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1772039020/engin_akyurt-woman-4605248_640_ehaplz.jpg"),
                        Person("Carlos Ruiz", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1772039016/istockphoto-1550589735-612x612_lgfnwy.jpg"),
                        Person("Juan Pérez", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1772039018/photo-1599566150163-29194dcaad36_ql1jmh.avif"),
                        Person("Laura G.", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1772039017/818376-woman-657753_640_wv958x.jpg")
                    )
                    items(people) { person ->
                        PersonItem(person)
                    }
                }
            }
        }

        // Destinos Populares (Grid-like)
        item {
            Column {
                SectionHeader("Destinos Sugeridos")
                Spacer(modifier = Modifier.height(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val destinations = listOf("Playas de Santa Marta", "Eje Cafetero", "Desierto de la Tatacoa", "Caño Cristales")
                    destinations.forEach { dest ->
                        SearchSuggestionItem(dest, "Colombia")
                    }
                }
            }
        }

        // Eventos próximos
        item {
            Column {
                SectionHeader("Eventos Próximos")
                Spacer(modifier = Modifier.height(16.dp))
                EventCard("Feria de las Flores", "Medellín, Agosto 2026")
                Spacer(modifier = Modifier.height(12.dp))
                EventCard("Carnaval de Barranquilla", "Barranquilla, Feb 2026")
            }
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
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(text = "Ver todo", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
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
                    .size(70.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(24.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                border = BorderStroke(2.dp, Color.White)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.padding(4.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = person.name, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SearchSuggestionItem(title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(text = subtitle, color = MaterialTheme.colorScheme.secondary, fontSize = 12.sp)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
    }
}

@Composable
fun EventCard(name: String, date: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(50.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = date, fontSize = 13.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}
