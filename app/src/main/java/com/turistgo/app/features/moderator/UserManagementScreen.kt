package com.turistgo.app.features.moderator

import androidx.compose.foundation.background
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun UserManagementScreen(
    navController: NavController,
    viewModel: UserManagementViewModel = viewModel()
) {
    val users = viewModel.users
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    val warmBg = Color(0xFFFBFAF5)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = warmBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            PaddingValues(horizontal = 24.dp).let {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Gestión de Usuarios",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(it),
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = "Administra los permisos y acceso a la plataforma",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(it)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(users) { user ->
                    UserCardRedesigned(
                        user = user,
                        onVerify = {
                            viewModel.verifyUser(user.id)
                            scope.launch {
                                snackbarHostState.showSnackbar("Usuario ${user.name} verificado")
                            }
                        },
                        onDelete = {
                            viewModel.deleteUser(user.id)
                            scope.launch {
                                snackbarHostState.showSnackbar("Usuario ${user.name} eliminado")
                            }
                        }
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun UserCardRedesigned(user: ModeratorUser, onVerify: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
