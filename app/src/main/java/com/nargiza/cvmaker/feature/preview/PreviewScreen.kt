package com.nargiza.cvmaker.feature.preview

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nargiza.cvmaker.core.designsystem.CvChip
import com.nargiza.cvmaker.core.designsystem.CvLoadingState
import com.nargiza.cvmaker.core.designsystem.CvPrimaryButton
import com.nargiza.cvmaker.core.designsystem.CvSecondaryButton
import com.nargiza.cvmaker.core.model.Cv
import com.nargiza.cvmaker.core.model.TemplateType
import com.nargiza.cvmaker.core.ui.CvDocumentPaper
import com.nargiza.cvmaker.domain.usecase.GeneratePdfUseCase
import com.nargiza.cvmaker.domain.usecase.GetCvUseCase
import com.nargiza.cvmaker.domain.usecase.UpdateCvUseCase
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    cvId: String,
    getCvUseCase: GetCvUseCase,
    updateCvUseCase: UpdateCvUseCase,
    generatePdfUseCase: GeneratePdfUseCase,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (cvId: String) -> Unit,
    onNavigateToAi: (cvId: String) -> Unit
) {
    var cv by remember { mutableStateOf<Cv?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isExporting by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(cvId) {
        isLoading = true
        cv = getCvUseCase(cvId)
        isLoading = false
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(cv?.title ?: "CV Preview", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { cv?.let { onNavigateToEdit(it.id) } }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { cv?.let { onNavigateToAi(it.id) } }) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI")
                    }
                    IconButton(
                        onClick = {
                            cv?.let {
                                scope.launch {
                                    isExporting = true
                                    val result = generatePdfUseCase.generate(it)
                                    isExporting = false
                                    result.onSuccess { file ->
                                        generatePdfUseCase.sharePdf(file)
                                    }.onFailure { err ->
                                        snackbarHostState.showSnackbar(err.message ?: "Share failed")
                                    }
                                }
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share PDF")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            if (cv != null) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CvPrimaryButton(
                            text = "Download PDF",
                            onClick = {
                                scope.launch {
                                    isExporting = true
                                    val result = generatePdfUseCase.generate(cv!!)
                                    isExporting = false
                                    result.onSuccess { file ->
                                        snackbarHostState.showSnackbar("PDF downloaded: ${file.name}")
                                        generatePdfUseCase.openPdf(file)
                                    }.onFailure { err ->
                                        snackbarHostState.showSnackbar("PDF generation failed: ${err.message}")
                                    }
                                }
                            },
                            isLoading = isExporting,
                            icon = Icons.Default.Download,
                            modifier = Modifier.weight(1f)
                        )
                        CvSecondaryButton(
                            text = "Share PDF",
                            onClick = {
                                scope.launch {
                                    isExporting = true
                                    val result = generatePdfUseCase.generate(cv!!)
                                    isExporting = false
                                    result.onSuccess { file ->
                                        generatePdfUseCase.sharePdf(file)
                                    }
                                }
                            },
                            icon = Icons.Default.Share,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (isLoading) {
            CvLoadingState(message = "Rendering CV Document...", modifier = Modifier.padding(padding))
        } else if (cv == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("CV not found.")
            }
        } else {
            val currentCv = cv!!
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
            ) {
                // Quick Template Switcher row
                item {
                    Text(
                        text = "Template Selection",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(TemplateType.entries) { tmpl ->
                            CvChip(
                                text = tmpl.title,
                                isSelected = currentCv.templateId == tmpl.id,
                                onClick = {
                                    val updated = currentCv.copy(templateId = tmpl.id)
                                    cv = updated
                                    scope.launch { updateCvUseCase(updated) }
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Document Paper
                item {
                    CvDocumentPaper(
                        cv = currentCv,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
