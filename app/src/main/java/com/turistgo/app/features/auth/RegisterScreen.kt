// Paquete donde se encuentra esta pantalla de registro
package com.turistgo.app.features.auth

// Importaciones de animaciones de Compose
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

// Importaciones de fundamentos de UI
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll

// Importaciones de íconos Material
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

// Importaciones de Material Design 3
import androidx.compose.material3.*

// Importaciones de runtime y estado
import androidx.compose.runtime.*

// Importaciones de UI core
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Importaciones de ViewModel y navegación
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

// Importaciones del proyecto
import com.turistgo.app.core.navigation.MainRoutes
import coil.compose.AsyncImage
import com.turistgo.app.core.components.LoadingOverlay
import com.turistgo.app.core.components.SocialLoginCard

/**
 * RegisterScreen - Pantalla de registro de nuevos usuarios
 * 
 * Permite a los usuarios crear una cuenta en TuristGo mediante áreas bien organizadas.
 */
@Composable
fun RegisterScreen(
    onNavigateToCompleteProfile: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    // Estados globales y UI del Scaffold
    val isLoading by viewModel.isLoading
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    // Manejo de notificaciones con Snackbar
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbarMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RegisterHeader()
                
                Spacer(modifier = Modifier.height(16.dp))
                
                PersonalDataSection(viewModel = viewModel, isLoading = isLoading)
                Spacer(modifier = Modifier.height(12.dp))
                
                LocationSection(viewModel = viewModel, isLoading = isLoading)
                Spacer(modifier = Modifier.height(12.dp))
                
                ContactSection(viewModel = viewModel, isLoading = isLoading)
                Spacer(modifier = Modifier.height(12.dp))
                
                PasswordSection(viewModel = viewModel, isLoading = isLoading)
                Spacer(modifier = Modifier.height(16.dp))
                
                RegisterActions(
                    isLoading = isLoading,
                    onRegisterClick = {
                        viewModel.register { userId ->
                            onNavigateToCompleteProfile(userId)
                        }
                    },
                    onBackClick = onBack
                )
            }

            LoadingOverlay(isLoading = isLoading, text = "Creando cuenta...")
        }
    }
}

@Composable
private fun RegisterHeader() {
    val imageUrl = "https://res.cloudinary.com/doxdjiyvi/image/upload/v1771997914/logo-turist_x5xgsq.png"
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Logo de TuristGo",
            modifier = Modifier.size(100.dp),
            contentScale = ContentScale.Fit
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Crear Cuenta",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Únete a la comunidad TuristGo",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun PersonalDataSection(viewModel: RegisterViewModel, isLoading: Boolean) {
    val name by viewModel.name
    val lastName by viewModel.lastName

    OutlinedTextField(
        value = name,
        onValueChange = { viewModel.onNameChange(it) },
        label = { Text("Nombre") },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        singleLine = true,
        enabled = !isLoading
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = lastName,
        onValueChange = { viewModel.onLastNameChange(it) },
        label = { Text("Apellidos") },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        singleLine = true,
        enabled = !isLoading
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationSection(viewModel: RegisterViewModel, isLoading: Boolean) {
    val age by viewModel.age
    val country by viewModel.country
    val city by viewModel.city
    val availableCities by viewModel.availableCities
    
    var countryExpanded by remember { mutableStateOf(false) }
    var cityExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        OutlinedTextField(
            value = age,
            onValueChange = { viewModel.onAgeChange(it) },
            label = { Text("Edad") },
            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            modifier = Modifier.weight(0.4f),
            shape = MaterialTheme.shapes.medium,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            enabled = !isLoading
        )
        
        ExposedDropdownMenuBox(
            expanded = countryExpanded,
            onExpandedChange = { if (!isLoading) countryExpanded = !countryExpanded },
            modifier = Modifier.weight(0.6f)
        ) {
            OutlinedTextField(
                value = country,
                onValueChange = {},
                readOnly = true,
                label = { Text("País") },
                leadingIcon = { Icon(Icons.Default.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = countryExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                enabled = !isLoading,
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )
            ExposedDropdownMenu(
                expanded = countryExpanded,
                onDismissRequest = { countryExpanded = false }
            ) {
                viewModel.countries.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            viewModel.onCountryChange(selectionOption)
                            countryExpanded = false
                        }
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    ExposedDropdownMenuBox(
        expanded = cityExpanded,
        onExpandedChange = { 
            if (!isLoading && country.isNotEmpty()) cityExpanded = !cityExpanded 
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = city,
            onValueChange = {},
            readOnly = true,
            label = { Text("Ciudad") },
            leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            enabled = !isLoading && country.isNotEmpty(),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            placeholder = { 
                if (country.isEmpty()) Text("Selecciona primero un país")
            }
        )
        if (availableCities.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = cityExpanded,
                onDismissRequest = { cityExpanded = false }
            ) {
                availableCities.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            viewModel.onCityChange(selectionOption)
                            cityExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactSection(viewModel: RegisterViewModel, isLoading: Boolean) {
    val phoneExtension by viewModel.phoneExtension
    val phone by viewModel.phone
    val email by viewModel.email

    var phoneExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = phoneExpanded,
            onExpandedChange = { if (!isLoading) phoneExpanded = !phoneExpanded },
            modifier = Modifier.width(108.dp)
        ) {
            OutlinedTextField(
                value = phoneExtension,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                enabled = !isLoading,
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                singleLine = true
            )
            ExposedDropdownMenu(
                expanded = phoneExpanded,
                onDismissRequest = { phoneExpanded = false }
            ) {
                viewModel.phoneExtensions.forEach { ext ->
                    DropdownMenuItem(
                        text = { Text(ext) },
                        onClick = {
                            viewModel.onPhoneExtensionChange(ext)
                            phoneExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = phone,
            onValueChange = { viewModel.onPhoneChange(it) },
            label = { Text("Teléfono") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            modifier = Modifier.weight(1f),
            shape = MaterialTheme.shapes.medium,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            enabled = !isLoading
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = email,
        onValueChange = { viewModel.onEmailChange(it) },
        label = { Text("Correo electrónico") },
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        enabled = !isLoading
    )
}

@Composable
private fun PasswordSection(viewModel: RegisterViewModel, isLoading: Boolean) {
    val password by viewModel.password
    val confirmPassword by viewModel.confirmPassword

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = { viewModel.onPasswordChange(it) },
        label = { Text("Contraseña") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        trailingIcon = {
            val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
            IconButton(onClick = { passwordVisible = !passwordVisible }, enabled = !isLoading) {
                Icon(imageVector = image, contentDescription = null)
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        singleLine = true,
        enabled = !isLoading
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = confirmPassword,
        onValueChange = { viewModel.onConfirmPasswordChange(it) },
        label = { Text("Confirmar contraseña") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        trailingIcon = {
            val image = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }, enabled = !isLoading) {
                Icon(imageVector = image, contentDescription = null)
            }
        },
        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        singleLine = true,
        enabled = !isLoading
    )
}

@Composable
private fun RegisterActions(
    isLoading: Boolean,
    onRegisterClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Button(
        onClick = onRegisterClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = MaterialTheme.shapes.medium,
        enabled = !isLoading
    ) {
        Text("Empezar ahora", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("¿Ya tienes cuenta?", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
        TextButton(onClick = onBackClick, enabled = !isLoading) {
            Text("Inicia sesión", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))

    // --- DIVISOR "O continúa con" ---
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
        Text(
            text = "O continúa con",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        SocialLoginCard(
            iconUrl = "https://cdn-icons-png.flaticon.com/512/300/300221.png",
            contentDescription = "Google",
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.width(20.dp))
        
        SocialLoginCard(
            iconUrl = "https://cdn-icons-png.flaticon.com/512/5968/5968764.png",
            contentDescription = "Facebook",
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.width(20.dp))
        
        SocialLoginCard(
            iconUrl = "https://cdn-icons-png.flaticon.com/512/3536/3536505.png",
            contentDescription = "LinkedIn",
            enabled = !isLoading
        )
    }
}
