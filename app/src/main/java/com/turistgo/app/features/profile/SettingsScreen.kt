package com.turistgo.app.features.profile

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
import com.turistgo.app.core.locale.AppLanguage
import com.turistgo.app.core.locale.AppStrings
import com.turistgo.app.core.locale.LanguageState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit, onLogout: () -> Unit) {
    val lang by LanguageState.current
    val s = AppStrings.get(lang)
    val RedAccent = MaterialTheme.colorScheme.primary   // alias → BrandRed via theme

    var showLanguagePicker by remember { mutableStateOf(false) }

    // Language picker dialog
    if (showLanguagePicker) {
        AlertDialog(
            onDismissRequest = { showLanguagePicker = false },
            title = { Text(s.selectLanguage, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    AppLanguage.values().forEach { language ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = lang == language,
                                onClick = {
                                    LanguageState.current.value = language
                                    showLanguagePicker = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = RedAccent)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(language.displayName, fontSize = 15.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguagePicker = false }) {
                    Text(s.cancel, color = RedAccent)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar — same pattern as ProfileScreen
        TopAppBar(
            title = {
                Text(
                    text = s.settingsTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onBackground)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            windowInsets = WindowInsets(0, 0, 0, 0)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // ─── Preferencias ──────────────────────────────────────
                SettingsSectionLabel(s.preferences)

                SettingsCard {
                    SettingsRow(
                        icon = Icons.Default.Language,
                        title = s.language,
                        subtitle = lang.displayName,
                        onClick = { showLanguagePicker = true }
                    )
                    SettingsDivider()
                    SettingsRow(
                        icon = Icons.Default.Notifications,
                        title = s.notificationsSettings,
                        subtitle = s.configureAlerts,
                        onClick = {}
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ─── Información Legal ──────────────────────────────────
                SettingsSectionLabel(s.legalInfo)

                SettingsCard {
                    SettingsRow(
                        icon = Icons.Default.Policy,
                        title = s.usagePolicy,
                        onClick = {}
                    )
                    SettingsDivider()
                    SettingsRow(
                        icon = Icons.Default.PrivacyTip,
                        title = s.privacy,
                        onClick = {}
                    )
                    SettingsDivider()
                    SettingsRow(
                        icon = Icons.Default.Info,
                        title = s.aboutApp,
                        subtitle = s.version,
                        onClick = {}
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ─── Cuenta ─────────────────────────────────────────────
                SettingsSectionLabel(s.account)


                SettingsCard {
                    SettingsRow(
                        icon = Icons.Default.Logout,
                        title = s.closeSession,
                        onClick = onLogout
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SettingsSectionLabel(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp, top = 4.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(content = content)
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 20.dp),
        color = MaterialTheme.colorScheme.outline
    )
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    titleColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onBackground,
    iconTint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = titleColor
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
