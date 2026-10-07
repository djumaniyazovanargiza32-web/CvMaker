package com.nargiza.cvmaker.feature.joboptimizer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nargiza.cvmaker.core.designsystem.CvAiButton
import com.nargiza.cvmaker.core.designsystem.CvPrimaryButton
import com.nargiza.cvmaker.core.designsystem.CvTextField
import com.nargiza.cvmaker.core.model.AiJobAnalysisRequest
import com.nargiza.cvmaker.core.model.AiJobAnalysisResponse
import com.nargiza.cvmaker.core.model.Cv
import com.nargiza.cvmaker.domain.repository.AiRepository
import com.nargiza.cvmaker.domain.repository.CvRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun JobOptimizerScreen(
    cvRepository: CvRepository,
    aiRepository: AiRepository,
    initialCvId: String? = null,
    onNavigateBack: () -> Unit
) {
    val cvs by cvRepository.observeCvs().collectAsStateWithLifecycle(initialValue = emptyList())
    val selectedCv = cvs.find { it.id == initialCvId } ?: cvs.firstOrNull()

    var jobDescription by remember { mutableStateOf("") }
    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisResult by remember { mutableStateOf<AiJobAnalysisResponse?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Job Description Optimizer", fontWeight = FontWeight.Bold) },
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
            item {
                Text(
                    text = "Optimize CV for Job",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Paste a target job posting below. AI will analyze the requirements against your CV, identify keyword gaps, and suggest targeted enhancements.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (selectedCv != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Work, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Comparing Against: ${selectedCv.title}", fontWeight = FontWeight.Bold)
                                Text(selectedCv.personalInfo.professionalTitle.ifBlank { "No title" }, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                } else {
                    Text("No CV found. Please create a CV first.", color = MaterialTheme.colorScheme.error)
                }
                Spacer(modifier = Modifier.height(16.dp))

                CvTextField(
                    value = jobDescription,
                    onValueChange = { jobDescription = it },
                    label = "Job Description *",
                    placeholder = "Paste full job description requirements here...",
                    singleLine = false,
                    maxLines = 8,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                CvAiButton(
                    text = "Analyze Job Description",
                    onClick = {
                        if (selectedCv != null && jobDescription.isNotBlank()) {
                            scope.launch {
                                isAnalyzing = true
                                val req = AiJobAnalysisRequest(
                                    jobDescription = jobDescription,
                                    cvSummary = selectedCv.professionalSummary,
                                    cvSkills = selectedCv.skills.map { it.name },
                                    cvExperiences = selectedCv.experiences.map { "${it.position} at ${it.company}: ${it.description}" }
                                )
                                val res = aiRepository.analyzeJob(req)
                                isAnalyzing = false
                                res.onSuccess {
                                    analysisResult = it
                                }.onFailure { err ->
                                    snackbarHostState.showSnackbar(err.message ?: "Job analysis failed")
                                }
                            }
                        }
                    },
                    isLoading = isAnalyzing,
                    enabled = jobDescription.isNotBlank() && selectedCv != null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Results Section
            if (analysisResult != null) {
                val res = analysisResult!!
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("AI Job Analysis", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                    Text("AI-generated match estimate", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (res.matchEstimatePercentage >= 75) Color(0xFF16A34A).copy(alpha = 0.15f)
                                            else Color(0xFFD97706).copy(alpha = 0.15f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${res.matchEstimatePercentage}%",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = if (res.matchEstimatePercentage >= 75) Color(0xFF16A34A) else Color(0xFFD97706)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(18.dp))

                            // Key Requirements
                            Text("Key Job Requirements", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                res.keyRequirements.forEach { req ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    ) {
                                        Text(req, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))

                            // Detected Keywords
                            Text("Detected in Your CV", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = Color(0xFF16A34A))
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                res.detectedKeywords.forEach { kw ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFDCFCE7),
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    ) {
                                        Text(kw, color = Color(0xFF15803D), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))

                            // Potential Missing Gaps
                            if (res.missingKeywordsGaps.isNotEmpty()) {
                                Text("Potential Gaps / Missing Keywords", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = Color(0xFFDC2626))
                                Spacer(modifier = Modifier.height(6.dp))
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    res.missingKeywordsGaps.forEach { gap ->
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFFFEE2E2),
                                            modifier = Modifier.padding(bottom = 6.dp)
                                        ) {
                                            Text(gap, color = Color(0xFFB91C1C), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                            }

                            // Suggested Improvements
                            Text("Suggested Improvements", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            Spacer(modifier = Modifier.height(8.dp))
                            res.suggestedImprovements.forEach { suggestion ->
                                Row(modifier = Modifier.padding(bottom = 6.dp)) {
                                    Text("• ", color = MaterialTheme.colorScheme.primary)
                                    Text(suggestion, style = MaterialTheme.typography.bodyMedium, lineHeight = 20.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
