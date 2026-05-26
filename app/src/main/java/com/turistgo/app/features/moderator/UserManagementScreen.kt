package com.turistgo.app.features.moderator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun UserManagementScreen(
    innerPadding: PaddingValues,
    navController: NavController,
    viewModel: UserManagementViewModel = hiltViewModel()
) {
    val users by viewModel.users.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    var userToDelete by remember { mutableStateOf<ModeratorUser?>(null) }
    var deleteConfirmationInput by remember { mutableStateOf("") }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header matches Feed/Dashboard style
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Text(
                    text = "Gestión",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Usuarios",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Administra los permisos y acceso a la plataforma",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(users) { user ->
                    UserCardRedesigned(
                        user = user,
                        onClick = {
                            navController.navigate(com.turistgo.app.core.navigation.MainRoutes.PublicProfile(user.id))
                        },
                        onVerify = {
                            viewModel.verifyUser(user.id)
                            scope.launch {
                                snackbarHostState.showSnackbar("Usuario ${user.name} verificado")
                            }
                        },
                        onDelete = {
                            userToDelete = user
                        }
                    )
                }
            }
        }
        
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
        )

        if (userToDelete != null) {
            val user = userToDelete!!
            AlertDialog(
                onDismissRequest = {
                    userToDelete = null
                    deleteConfirmationInput = ""
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Advertencia de Seguridad",
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 18.sp
                        )
                    }
                },
                text = {
                    Column {
                        Text(
                            text = "Estás a punto de eliminar permanentemente al usuario:",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${user.name} (${user.email})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "⚠️ IMPORTANTE:\nEsta acción no se puede deshacer. Se borrarán de forma definitiva todas sus publicaciones, comentarios, notificaciones, historial de actividad y sus puntos acumulados en TuristGo.",
                            color = Color.Gray,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Para confirmar la eliminación, escribe la palabra eliminar abajo:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = deleteConfirmationInput,
                            onValueChange = { deleteConfirmationInput = it },
                            placeholder = { Text("Escribe 'eliminar'") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteUser(user.id)
                            scope.launch {
                                snackbarHostState.showSnackbar("Usuario ${user.name} eliminado")
                            }
                            userToDelete = null
                            deleteConfirmationInput = ""
                        },
                        enabled = deleteConfirmationInput.trim().lowercase() == "eliminar",
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Eliminar definitivamente", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            userToDelete = null
                            deleteConfirmationInput = ""
                        }
                    ) {
                        Text("Cancelar", fontWeight = FontWeight.Medium)
                    }
                },
                shape = RoundedCornerShape(24.dp),
                containerColor = Color.White
            )
        }
    }
}

@Composable
fun UserCardRedesigned(
    user: ModeratorUser, 
    onClick: () -> Unit,
    onVerify: () -> Unit, 
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF3E5F5) // Soft Purple
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF7E57C2))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.name, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 17.sp,
                        color = Color(0xFF1A1A1A)
                    )
                    if (user.isVerified) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            Icons.Default.Verified, 
                            contentDescription = null, 
                            tint = Color(0xFF4CAF50), 
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(text = user.email, fontSize = 13.sp, color = Color.Gray)
                
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFE3F2FD),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = user.role.uppercase(), 
                        fontSize = 9.sp, 
                        color = Color(0xFF1E88E5), 
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Row {
                if (!user.isVerified) {
                    IconButton(onClick = onVerify) {
                        Icon(Icons.Default.Verified, contentDescription = "Verificar", tint = Color(0xFF4CAF50))
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFEF5350))
                }
            }
        }
    }
}
