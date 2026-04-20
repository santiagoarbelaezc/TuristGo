// Declara el paquete donde se encuentra esta pantalla dentro de la estructura de la app.
package com.turistgo.app.features.profile

// Importaciones de Jetpack Compose para la UI
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Importaciones para gestión de idioma y tema
import com.turistgo.app.core.locale.AppLanguage
import com.turistgo.app.core.locale.LanguageState
import androidx.compose.ui.res.stringResource
import com.turistgo.app.R
import com.turistgo.app.core.theme.ThemeState
import androidx.hilt.navigation.compose.hiltViewModel // Para inyectar ViewModel con Hilt

// Marca que se usan APIs experimentales de Material 3
@OptIn(ExperimentalMaterial3Api::class)
// Declara la función composable principal de la pantalla de configuración
@Composable
fun SettingsScreen(
    innerPadding: PaddingValues, // Padding de la navegación superior (status bar, etc.)
    onBack: () -> Unit, // Callback para volver a la pantalla anterior
    onLogout: () -> Unit, // Callback para cerrar sesión (ejecuta la navegación)
    onNavigateToPrivacy: () -> Unit, // Callback para ir a políticas de privacidad
    onNavigateToTerms: () -> Unit, // Callback para ir a términos de uso
    onNavigateToSupport: () -> Unit, // Callback para ir a ayuda/soporte
    viewModel: SettingsViewModel = hiltViewModel() // ViewModel inyectado por Hilt
) {
    // Obtiene el idioma actual del estado global
    val lang by LanguageState.current
    // Obtiene el tema actual (modo oscuro) del estado global
    val currentTheme by ThemeState.isDarkMode
    
    // Estado para mostrar/ocultar el selector de idioma
    var showLanguagePicker by remember { mutableStateOf(false) }
    // Estado para mostrar/ocultar el diálogo de confirmación de eliminación de cuenta
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Diálogo de confirmación para eliminar cuenta
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false }, // Cierra al tocar fuera
            title = { Text(stringResource(R.string.confirm_delete_title), fontWeight = FontWeight.Bold) }, // "Confirmar eliminación"
            text = { Text(stringResource(R.string.confirm_delete_msg)) }, // Mensaje de advertencia
            confirmButton = {
                Button(
                    onClick = { 
                        viewModel.deleteAccount(onLogout) // Elimina la cuenta y ejecuta logout
                        showDeleteConfirm = false // Cierra el diálogo
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error) // Botón rojo de error
                ) {
                    Text(stringResource(R.string.accept)) // "Aceptar"
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(stringResource(R.string.cancel)) // "Cancelar"
                }
            }
        )
    }

    // Diálogo selector de idioma
    if (showLanguagePicker) {
        AlertDialog(
            onDismissRequest = { showLanguagePicker = false },
            title = { Text(stringResource(R.string.select_language), fontWeight = FontWeight.Bold) }, // "Seleccionar idioma"
            text = {
                Column {
                    // Itera sobre todos los idiomas disponibles (AppLanguage es un enum)
                    AppLanguage.entries.forEach { language ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Radio button para seleccionar el idioma
                            RadioButton(
                                selected = lang == language, // Marca el idioma actual
                                onClick = {
                                    viewModel.setLanguage(language) // Guarda la preferencia de idioma
                                    showLanguagePicker = false // Cierra el diálogo
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(language.displayName, fontSize = 15.sp) // Nombre del idioma (ej: "Español")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguagePicker = false }) {
                    Text(stringResource(R.string.cancel)) // "Cancelar"
                }
            }
        )
    }

    // Columna principal que ocupa toda la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding) // Aplica el padding de navegación
            .background(MaterialTheme.colorScheme.background) // Fondo del tema actual
    ) {
        // Barra superior con título y botón de retroceso
        TopAppBar(
            title = { Text(text = stringResource(R.string.settings_title), fontWeight = FontWeight.Bold) }, // "Configuración"
            navigationIcon = {
                IconButton(onClick = onBack) { // Botón de flecha hacia atrás
                    Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.cancel))
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background) // Mismo color que fondo
        )

        // LazyColumn para scroll vertical del contenido
        LazyColumn(
            modifier = Modifier.weight(1f), // Ocupa el espacio restante
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp) // Padding interno
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Sección "Preferencias"
                SettingsSectionLabel(stringResource(R.string.preferences))
                SettingsCard { // Tarjeta contenedora
                    // Fila: Idioma (con selector)
                    SettingsRow(
                        icon = Icons.Default.Language,
                        title = stringResource(R.string.language),
                        subtitle = lang.displayName, // Muestra el idioma actual
                        onClick = { showLanguagePicker = true } // Abre el selector de idioma
                    )
                    SettingsDivider() // Línea divisoria
                    // Fila: Modo oscuro (con switch)
                    SettingsRowWithSwitch(
                        icon = Icons.Default.DarkMode,
                        title = stringResource(R.string.dark_mode),
                        checked = currentTheme == true, // True si el tema oscuro está activado
                        onCheckedChange = { viewModel.setTheme(if (it) true else null) } // null = seguir al sistema
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sección "Información legal"
                SettingsSectionLabel(stringResource(R.string.legal_info))
                SettingsCard {
                    SettingsRow(
                        icon = Icons.Default.Policy,
                        title = stringResource(R.string.usage_policy), // "Política de uso"
                        onClick = onNavigateToTerms
                    )
                    SettingsDivider()
                    SettingsRow(
                        icon = Icons.Default.PrivacyTip,
                        title = stringResource(R.string.privacy), // "Privacidad"
                        onClick = onNavigateToPrivacy
                    )
                    SettingsDivider()
                    SettingsRow(
                        icon = Icons.Default.Help,
                        title = stringResource(R.string.help_support), // "Ayuda y soporte"
                        onClick = onNavigateToSupport
                    )
                    SettingsDivider()
                    SettingsRow(
                        icon = Icons.Default.Info,
                        title = stringResource(R.string.about_app), // "Acerca de"
                        subtitle = stringResource(R.string.version), // Muestra la versión de la app
                        onClick = {} // Sin acción, solo información
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sección "Cuenta"
                SettingsSectionLabel(stringResource(R.string.account))
                SettingsCard {
                    SettingsRow(
                        icon = Icons.Default.Logout,
                        title = stringResource(R.string.close_session), // "Cerrar sesión"
                        onClick = { viewModel.logout(onLogout) } // Ejecuta logout
                    )
                    SettingsDivider()
                    SettingsRow(
                        icon = Icons.Default.DeleteForever,
                        title = stringResource(R.string.delete_account), // "Eliminar cuenta"
                        titleColor = MaterialTheme.colorScheme.error, // Texto en rojo
                        iconTint = MaterialTheme.colorScheme.error, // Ícono en rojo
                        onClick = { showDeleteConfirm = true } // Muestra diálogo de confirmación
                    )
                }

                Spacer(modifier = Modifier.height(32.dp)) // Espaciado final
            }
        }
    }
}

// Componente para mostrar diálogos informativos (actualmente no se usa en la pantalla principal)
@Composable
private fun InfoDialog(title: String, content: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = { Text(content) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

// Componente que muestra el título de una sección (ej: "Preferencias", "Cuenta")
@Composable
private fun SettingsSectionLabel(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary, // Color primario para destacar
        modifier = Modifier.padding(bottom = 8.dp, top = 4.dp)
    )
}

// Componente que envuelve el contenido en una tarjeta con bordes redondeados
@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp), // Bordes redondeados
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), // Color de superficie semitransparente
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(content = content) // Contenido en columna
    }
}

// Componente que muestra un divisor horizontal dentro de la tarjeta
@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 20.dp), // Padding lateral
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f) // Color de contorno semitransparente
    )
}

// Componente para una fila de configuración con ícono, título, subtítulo opcional y flecha de navegación
@Composable
private fun SettingsRow(
    icon: ImageVector, // Ícono de la opción
    title: String, // Título principal
    subtitle: String? = null, // Subtítulo opcional (ej: idioma seleccionado)
    titleColor: Color = MaterialTheme.colorScheme.onBackground, // Color del título
    iconTint: Color = MaterialTheme.colorScheme.primary, // Color del ícono
    onClick: () -> Unit // Acción al hacer clic
) {
    Surface(
        onClick = onClick, // Hace que toda la fila sea clickeable
        color = Color.Transparent, // Fondo transparente para heredar el de la tarjeta
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp), // Padding interno
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(24.dp)) // Ícono
            Spacer(modifier = Modifier.width(16.dp)) // Espacio entre ícono y texto
            Column(modifier = Modifier.weight(1f)) { // Columna que ocupa el espacio restante
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = titleColor)
                if (subtitle != null) {
                    Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                }
            }
            // Flecha a la derecha indicando que se puede navegar
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(20.dp))
        }
    }
}

// Componente para una fila de configuración con switch (para modo oscuro)
@Composable
private fun SettingsRowWithSwitch(
    icon: ImageVector, // Ícono de la opción
    title: String, // Título
    checked: Boolean, // Estado del switch
    onCheckedChange: (Boolean) -> Unit // Callback cuando cambia el switch
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp), // Padding interno
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp)) // Ícono
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, modifier = Modifier.weight(1f), fontSize = 16.sp, fontWeight = FontWeight.Medium) // Título ocupa espacio restante
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary) // Color del thumb cuando está activado
        )
    }
}
