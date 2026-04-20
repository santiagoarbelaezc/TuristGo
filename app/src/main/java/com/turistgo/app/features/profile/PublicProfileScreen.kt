// Declara el paquete donde se encuentra esta pantalla dentro de la estructura de la app.
package com.turistgo.app.features.profile

// Importaciones de Jetpack Compose para la UI
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
import androidx.compose.runtime.collectAsState // Para observar flujos de datos
import androidx.hilt.navigation.compose.hiltViewModel // Para inyectar ViewModel con Hilt
import coil.compose.AsyncImage // Para cargar imágenes desde URL
import com.turistgo.app.R
import com.turistgo.app.core.components.SmallDestinationCard // Componente reutilizable de tarjeta
import com.turistgo.app.core.components.Destination // Modelo de datos para destinos

// Marca que se usan APIs experimentales de Material 3
@OptIn(ExperimentalMaterial3Api::class)
// Declara la función composable principal de la pantalla de perfil público
@Composable
fun PublicProfileScreen(
    innerPadding: PaddingValues, // Padding de la navegación superior (status bar, etc.)
    userId: String, // ID del usuario cuyo perfil se está visualizando
    onBack: () -> Unit, // Callback para volver a la pantalla anterior
    onNavigateToDetail: (String) -> Unit, // Callback para navegar al detalle de un post (recibe ID)
    viewModel: PublicProfileViewModel = hiltViewModel() // ViewModel inyectado por Hilt
) {
    // Observa los estados del ViewModel (perfil del usuario, sus posts, estadísticas)
    val userProfile by viewModel.userProfile.collectAsState()
    val posts by viewModel.userPosts.collectAsState()
    val stats by viewModel.profileStats.collectAsState()

    // Efecto que se ejecuta cuando cambia el userId para cargar los datos del usuario
    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    // URL de la foto de perfil (prioriza la del perfil, o usa una por defecto)
    val profileImageUrl = userProfile?.profilePhotoUrl 
        ?: "https://res.cloudinary.com/doxdjiyvi/image/upload/v1769405400/english-notebook/profiles/profile_69658edf82ad881040292fe6_1769405397996.jpg"

    // Columna principal que ocupa toda la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding) // Aplica el padding de navegación
            .background(MaterialTheme.colorScheme.background) // Fondo del tema actual
    ) {
        // Barra superior centrada con el nombre del usuario y botón de retroceso
        CenterAlignedTopAppBar(
            title = { Text(userProfile?.name ?: "Perfil", fontWeight = FontWeight.Bold) }, // Nombre o "Perfil" por defecto
            navigationIcon = {
                IconButton(onClick = onBack) { // Botón de flecha hacia atrás
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background // Mismo color que el fondo
            ),
            windowInsets = WindowInsets(0, 0, 0, 0) // Sin insets adicionales
        )

        // LazyColumn para scroll vertical de todo el contenido
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally, // Centra horizontalmente los items
            contentPadding = PaddingValues(bottom = 24.dp) // Padding inferior
        ) {
            // Primer item: cabecera del perfil público
            item {
                Spacer(modifier = Modifier.height(16.dp)) // Espaciado superior
                
                // Foto de perfil del usuario visitado
                AsyncImage(
                    model = profileImageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape) // Forma circular
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop // Ajusta la imagen recortando
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Nombre del usuario
                Text(
                    text = userProfile?.name ?: "Usuario",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                // Username (si existe)
                if (userProfile?.username?.isNotEmpty() == true) {
                    Text(
                        text = "@${userProfile?.username}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                // Nivel del usuario (solo visual, sin interacción)
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

                // Fila de estadísticas: posts, seguidores, seguidos
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(label = stringResource(R.string.stat_posts), value = stats.postsCount.toString())
                    StatItem(label = stringResource(R.string.stat_followers), value = stats.followersCount.toString())
                    StatItem(label = stringResource(R.string.stat_following), value = stats.followingCount.toString())
                }

                // Estados de la relación entre el usuario actual y el perfil visitado
                val isFollowing by viewModel.isFollowing.collectAsState() // ¿Lo sigue?
                val isMutual by viewModel.isMutualFollow.collectAsState() // ¿Es mutuo (amigos)?
                val isPending by viewModel.isPendingRequest.collectAsState() // ¿He recibido una solicitud de él?
                val isSent by viewModel.isSentRequest.collectAsState() // ¿Le he enviado una solicitud?
                val isMe by viewModel.isMe.collectAsState() // ¿Es el propio usuario?

                // Botón de interacción (solo si no es el propio usuario)
                if (!isMe) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (isPending) {
                        // CASO: Hemos recibido una solicitud de este usuario
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { viewModel.acceptFollowRequest() },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // Verde éxito
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Aceptar", fontWeight = FontWeight.Bold)
                            }
                            
                            OutlinedButton(
                                onClick = { viewModel.declineFollowRequest() },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.error))
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Rechazar", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // CASO: Proceso normal (Seguir, Siguiendo, Amigos, Solicitado)
                        Button(
                            onClick = { if (!isFollowing && !isSent) viewModel.requestFollow() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .height(48.dp),
                            enabled = !isFollowing && !isSent,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when {
                                    isMutual -> Color(0xFF00BFA5).copy(alpha = 0.2f)
                                    isFollowing -> MaterialTheme.colorScheme.surfaceVariant
                                    isSent -> Color.LightGray.copy(alpha = 0.5f)
                                    else -> MaterialTheme.colorScheme.primary
                                },
                                contentColor = when {
                                    isMutual -> Color(0xFF00BFA5)
                                    isFollowing -> MaterialTheme.colorScheme.onSurfaceVariant
                                    isSent -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    else -> Color.White
                                }
                            )
                        ) {
                            val icon = when {
                                isMutual -> Icons.Default.Group
                                isFollowing -> Icons.Default.Check
                                isSent -> Icons.Default.HourglassEmpty
                                else -> Icons.Default.PersonAdd
                            }
                            val text = when {
                                isMutual -> "Amigos"
                                isFollowing -> "Siguiendo"
                                isSent -> "Solicitado"
                                else -> "Seguir"
                            }
                            
                            Icon(imageVector = icon, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = text, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                // Divisor horizontal antes de la sección de publicaciones
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Item: título de la sección de publicaciones
            item {
                Text(
                    text = "Publicaciones",
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Si no hay publicaciones, muestra un mensaje
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
                // Muestra las publicaciones en un grid de 2 columnas (usando chunked)
                items(posts.chunked(2)) { rowPosts -> // Divide la lista en grupos de 2
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre tarjetas
                    ) {
                        // Por cada post en la fila, muestra una SmallDestinationCard
                        rowPosts.forEach { post ->
                            Box(modifier = Modifier.weight(1f)) { // Cada tarjeta ocupa el mismo ancho
                                SmallDestinationCard(
                                    destination = Destination(
                                        post.id, post.name, post.location, post.rating, post.imageUrl, post.commentCount
                                    ),
                                    onClick = { onNavigateToDetail(post.id) } // Navega al detalle del post
                                )
                            }
                        }
                        // Si la fila tiene solo 1 elemento, agrega un spacer para mantener el grid balanceado
                        if (rowPosts.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
