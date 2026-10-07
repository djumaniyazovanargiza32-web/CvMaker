package com.nargiza.cvmaker.feature.home

import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nargiza.cvmaker.core.designsystem.CvEmptyState
import com.nargiza.cvmaker.core.designsystem.CvLoadingState
import com.nargiza.cvmaker.core.designsystem.CvPrimaryButton
import com.nargiza.cvmaker.core.designsystem.CvSecondaryButton
import com.nargiza.cvmaker.core.designsystem.CvTextField
import com.nargiza.cvmaker.core.model.Cv
import com.nargiza.cvmaker.core.model.TemplateType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToEditor: (cvId: String, step: Int) -> Unit,
    onNavigateToPreview: (cvId: String) -> Unit,
    onNavigateToAiAssistant: () -> Unit,
    onNavigateToJobOptimizer: (cvId: String?) -> Unit,
    onNavigateToTemplates: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var cvToDelete by remember { mutableStateOf<Cv?>(null) }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToEditor -> onNavigateToEditor(effect.cvId, effect.step)
                is HomeEffect.NavigateToPreview -> onNavigateToPreview(effect.cvId)
                is HomeEffect.NavigateToAiAssistant -> onNavigateToAiAssistant()
                is HomeEffect.NavigateToJobOptimizer -> onNavigateToJobOptimizer(effect.cvId)
                is HomeEffect.NavigateToTemplates -> onNavigateToTemplates()
                is HomeEffect.NavigateToSettings -> onNavigateToSettings()
                is HomeEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                else -> Unit
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AI CV Maker",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onIntent(HomeIntent.CreateNewCv) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create New CV")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Header Banner
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Create your next opportunity.",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Craft high-impact, ATS-optimized CVs with AI-powered intelligence.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Quick Category Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickNavCard(
                        title = "AI Assistant",
                        icon = Icons.Default.AutoAwesome,
                        gradient = listOf(Color(0xFF7C3AED), Color(0xFF4C1D95)),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToAiAssistant
                    )
                    QuickNavCard(
                        title = "Job Optimizer",
                        icon = Icons.Default.Work,
                        gradient = listOf(Color(0xFF0E7490), Color(0xFF075985)),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToJobOptimizer(state.cvs.firstOrNull()?.id) }
                    )
                    QuickNavCard(
                        title = "Templates",
                        icon = Icons.Default.Style,
                        gradient = listOf(Color(0xFF1E40AF), Color(0xFF1E3A8A)),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToTemplates
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Search Bar (if CVs exist)
            if (state.cvs.isNotEmpty()) {
                item {
                    CvTextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.onIntent(HomeIntent.UpdateSearchQuery(it)) },
                        label = "Search CVs",
                        placeholder = "Search by title, role, or company...",
                        leadingIcon = Icons.Default.Search,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "My CVs (${state.filteredCvs.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Loading or Empty or CV List
            if (state.isLoading) {
                item {
                    CvLoadingState(message = "Loading your CVs...")
                }
            } else if (state.cvs.isEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    CvEmptyState(
                        title = "Create your first CV",
                        description = "Build a professional CV with AI assistance and export it as a polished PDF.",
                        primaryActionText = "+ Create New CV",
                        onPrimaryAction = { viewModel.onIntent(HomeIntent.CreateNewCv) },
                        secondaryActionText = "Load Sample Tech CV",
                        onSecondaryAction = { viewModel.onIntent(HomeIntent.CreateSampleCv) }
                    )
                }
            } else {
                items(state.filteredCvs, key = { it.id }) { cv ->
                    CvListItemCard(
                        cv = cv,
                        onOpen = { viewModel.onIntent(HomeIntent.OpenCv(cv.id)) },
                        onPreview = { viewModel.onIntent(HomeIntent.PreviewCv(cv.id)) },
                        onDuplicate = { viewModel.onIntent(HomeIntent.DuplicateCv(cv.id)) },
                        onExport = { viewModel.onIntent(HomeIntent.ExportPdf(cv)) },
                        onShare = { viewModel.onIntent(HomeIntent.ShareCv(cv)) },
                        onDelete = { cvToDelete = cv }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (cvToDelete != null) {
        AlertDialog(
            onDismissRequest = { cvToDelete = null },
            title = { Text("Delete CV") },
            text = { Text("Are you sure you want to delete '${cvToDelete?.title}'? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        cvToDelete?.let { viewModel.onIntent(HomeIntent.DeleteCv(it.id)) }
                        cvToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { cvToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun QuickNavCard(
    title: String,
    icon: ImageVector,
    gradient: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(Brush.linearGradient(gradient))
                .padding(14.dp)
                .fillMaxWidth()
        ) {
            Column {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun CvListItemCard(
    cv: Cv,
    onOpen: () -> Unit,
    onPreview: () -> Unit,
    onDuplicate: () -> Unit,
    onExport: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val relativeTime = DateUtils.getRelativeTimeSpanString(cv.updatedAt).toString()
    val template = TemplateType.fromId(cv.templateId)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = cv.title.ifBlank { "Untitled CV" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (cv.personalInfo.professionalTitle.isNotBlank())
                            "${cv.personalInfo.professionalTitle} • Updated $relativeTime"
                        else
                            "Updated $relativeTime",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Options")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit CV") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onOpen()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Preview") },
                            leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onPreview()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Export PDF") },
                            leadingIcon = { Icon(Icons.Default.PictureAsPdf, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onExport()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Share PDF") },
                            leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onShare()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Duplicate") },
                            leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onDuplicate()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                            leadingIcon = { Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "${template.title} Template",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row {
                    TextButton(onClick = onPreview) {
                        Text("Preview", style = MaterialTheme.typography.labelMedium)
                    }
                    TextButton(onClick = onExport) {
                        Text("Export PDF", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}
