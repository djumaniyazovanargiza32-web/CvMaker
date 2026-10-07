package com.nargiza.cvmaker.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nargiza.cvmaker.core.database.preferences.ThemeMode
import com.nargiza.cvmaker.core.database.preferences.UserPreferences
import com.nargiza.cvmaker.core.designsystem.CvTextField
import com.nargiza.cvmaker.core.model.TemplateType
import com.nargiza.cvmaker.domain.repository.SettingsRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsRepository: SettingsRepository,
    onNavigateBack: () -> Unit,
    onNavigateToTemplates: () -> Unit
) {
    val prefs by settingsRepository.observePreferences().collectAsStateWithLifecycle(initialValue = UserPreferences())
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showBackendUrlDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var backendUrlInput by remember(prefs.backendUrl) { mutableStateOf(prefs.backendUrl) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp)
        ) {
            // Appearance Section
            item {
                SettingsSectionHeader("Appearance")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.padding(4.dp))
                            Text("Theme Mode", fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        ThemeMode.entries.forEach { mode ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        scope.launch { settingsRepository.setThemeMode(mode) }
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = prefs.themeMode == mode,
                                    onClick = { scope.launch { settingsRepository.setThemeMode(mode) } }
                                )
                                Text(
                                    text = when (mode) {
                                        ThemeMode.SYSTEM -> "System Default"
                                        ThemeMode.LIGHT -> "Light Theme"
                                        ThemeMode.DARK -> "Dark Theme"
                                    },
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Templates Section
            item {
                SettingsSectionHeader("CV Preferences")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsRow(
                            title = "Default Template",
                            subtitle = TemplateType.fromId(prefs.defaultTemplateId).title,
                            icon = Icons.Default.Style,
                            onClick = onNavigateToTemplates
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // AI & Backend Config
            item {
                SettingsSectionHeader("AI & Cloud Services")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsRow(
                            title = "AI Backend Endpoint",
                            subtitle = prefs.backendUrl,
                            icon = Icons.Default.Public,
                            onClick = { showBackendUrlDialog = true }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("AI Processing Consent", fontWeight = FontWeight.SemiBold)
                                Text("Allow sending CV snippets to secure backend for enhancement", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = prefs.aiConsentGiven,
                                onCheckedChange = { scope.launch { settingsRepository.setAiConsent(it) } }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Privacy & Data
            item {
                SettingsSectionHeader("Privacy & Storage")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsRow(
                            title = "Privacy Policy",
                            subtitle = "Learn how your CV and personal data are kept private",
                            icon = Icons.Default.Lock,
                            onClick = { showPrivacyDialog = true }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        SettingsRow(
                            title = "Delete All Data",
                            subtitle = "Permanently remove all local CVs and preferences",
                            icon = Icons.Default.DeleteForever,
                            titleColor = MaterialTheme.colorScheme.error,
                            onClick = { showDeleteConfirm = true }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // About Section
            item {
                SettingsSectionHeader("About")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("AI CV Maker", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("Version 1.0.0 (Production Build)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Engineered with Jetpack Compose, Clean Architecture, MVI, Room, Koin, Ktor, and on-device PDF generation.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // Backend URL Dialog
    if (showBackendUrlDialog) {
        AlertDialog(
            onDismissRequest = { showBackendUrlDialog = false },
            title = { Text("Configure AI Backend Endpoint") },
            text = {
                Column {
                    Text("Enter the URL of your deployed or local Ktor backend server (default: http://10.0.2.2:8080):", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(10.dp))
                    CvTextField(
                        value = backendUrlInput,
                        onValueChange = { backendUrlInput = it },
                        label = "Backend URL",
                        placeholder = "http://10.0.2.2:8080"
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            settingsRepository.setBackendUrl(backendUrlInput.trim())
                            showBackendUrlDialog = false
                            snackbarHostState.showSnackbar("Backend URL updated")
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBackendUrlDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Privacy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Policy & AI Safety") },
            text = {
                Text(
                    "AI CV Maker is built with an offline-first architecture.\n\n" +
                            "• All CV drafts, personal information, and PDF documents are stored locally on your device in an encrypted Room SQLite database.\n\n" +
                            "• AI enhancements are only triggered with your explicit consent.\n\n" +
                            "• The AI backend acts strictly as a stateless transformer and never persists, indexes, or sells your personal resume data."
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) { Text("Close") }
            }
        )
    }

    // Delete Confirmation
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete All Data?", color = MaterialTheme.colorScheme.error) },
            text = {
                Text("Are you sure you want to delete all CVs and reset preferences? This will permanently erase your local database.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            settingsRepository.deleteAllData()
                            showDeleteConfirm = false
                            snackbarHostState.showSnackbar("All data deleted")
                        }
                    }
                ) {
                    Text("Delete Forever", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    titleColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = titleColor)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = titleColor)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
