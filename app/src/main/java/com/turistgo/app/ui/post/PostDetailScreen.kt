package com.turistgo.app.ui.post

import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.turistgo.app.ui.components.Destination
import com.turistgo.app.ui.components.DestinationCard
import com.turistgo.app.ui.feed.components.Person
import com.turistgo.app.ui.feed.components.PersonItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(destinationId: String?, onBack: () -> Unit) {
    // Mock data for the detailed view
    val title = "Parque Tayrona"
    val location = "Santa Marta, Magdalena"
    val description = "El Parque Nacional Natural Tayrona es uno de los parques naturales más importantes de Colombia. Es hábitat de una gran cantidad de especies que se distribuyen en regiones con diferentes pisos térmicos que van desde el nivel del mar hasta los 900 metros de altura."
    val imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1771996096/visitar-parque-tayrona-13_bwybj6.webp"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Detalle del Destino", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                IconButton(onClick = { /* Share */ }) {
                    Icon(Icons.Default.Share, contentDescription = "Compartir")
                }
                IconButton(onClick = { /* Save */ }) {
                    Icon(Icons.Default.BookmarkBorder, contentDescription = "Guardar")
                }
            },
            windowInsets = WindowInsets(0, 0, 0, 0)
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            // Imagen principal
            item {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // Información básica
            item {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = location, color = MaterialTheme.colorScheme.secondary)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Descripción", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = description, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 22.sp)
                }
            }

            // Sección de Comentarios
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(text = "Comentarios (12)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    CommentItem("Juan David", "Excelente lugar para desconectarse.", "Hace 2 horas")
                    CommentItem("Sofía R.", "Recomiendo llevar mucha agua y protector solar.", "Hace 5 horas")
                    
                    TextButton(onClick = { /* View more */ }) {
                        Text("Ver todos los comentarios")
                    }
                }
            }

            // Publicaciones relacionadas
            item {
                Column(modifier = Modifier.padding(vertical = 20.dp)) {
                    Text(
                        text = "Publicaciones relacionadas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val related = listOf(
                            Destination("San Andrés", "Isla", "4.8", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1772036015/destinos-naturales-en-colombia-sin-turismo-masivo_ei0akp.jpg"),
                            Destination("Cartagena", "Bolívar", "4.7", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1771996090/iStock-1165965255_copia_tvooih.jpg")
                        )
                        items(related) { dest ->
                            RelatedPostCard(dest)
                        }
                    }
                }
            }

            // Personas por descubrir
            item {
                Column(modifier = Modifier.padding(bottom = 30.dp)) {
                    Text(
                        text = "Personas por descubrir",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val people = listOf(
                            Person("Ana María", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1772039020/engin_akyurt-woman-4605248_640_ehaplz.jpg"),
                            Person("Carlos Ruiz", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1772039016/istockphoto-1550589735-612x612_lgfnwy.jpg"),
                            Person("Laura G.", "https://res.cloudinary.com/doxdjiyvi/image/upload/v1772039017/818376-woman-657753_640_wv958x.jpg")
                        )
                        items(people) { person ->
                            PersonItem(person)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommentItem(author: String, content: String, time: String) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = author, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = time, color = MaterialTheme.colorScheme.secondary, fontSize = 12.sp)
            }
            Text(text = content, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun RelatedPostCard(destination: Destination) {
    Card(
        modifier = Modifier.size(200.dp, 150.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box {
            AsyncImage(
                model = destination.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )
            Text(
                text = destination.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)
            )
        }
    }
}
