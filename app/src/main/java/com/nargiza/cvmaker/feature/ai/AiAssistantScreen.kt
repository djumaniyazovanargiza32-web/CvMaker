package com.nargiza.cvmaker.feature.ai

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nargiza.cvmaker.core.designsystem.CvAiButton
import com.nargiza.cvmaker.core.designsystem.CvPrimaryButton
import com.nargiza.cvmaker.core.designsystem.CvTextField
import com.nargiza.cvmaker.core.model.AiAchievementRequest
import com.nargiza.cvmaker.core.model.AiCvRequest
import com.nargiza.cvmaker.core.model.AiSummaryRequest
import com.nargiza.cvmaker.core.model.Cv
import com.nargiza.cvmaker.core.model.Experience
import com.nargiza.cvmaker.core.model.PersonalInfo
import com.nargiza.cvmaker.core.model.Project
import com.nargiza.cvmaker.core.model.Skill
import com.nargiza.cvmaker.domain.repository.AiRepository
import com.nargiza.cvmaker.domain.repository.CvRepository
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(
    cvRepository: CvRepository,
    aiRepository: AiRepository,
    initialCvId: String? = null,
    onNavigateBack: () -> Unit,
    onNavigateToEditor: (cvId: String) -> Unit
) {
    val cvs by cvRepository.observeCvs().collectAsStateWithLifecycle(initialValue = emptyList())
    val selectedCv = cvs.find { it.id == initialCvId } ?: cvs.firstOrNull()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Assistant Prompts, 1: Full CV Wizard
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Assistant", fontWeight = FontWeight.Bold)
                    }
                },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("CV Assistant", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Generate Full CV", fontWeight = FontWeight.SemiBold) }
                )
            }

            if (selectedTab == 0) {
                AiPromptsTab(
                    selectedCv = selectedCv,
                    aiRepository = aiRepository,
                    cvRepository = cvRepository,
                    onNavigateToEditor = onNavigateToEditor,
                    showSnackbar = { scope.launch { snackbarHostState.showSnackbar(it) } }
                )
            } else {
                FullCvWizardTab(
                    aiRepository = aiRepository,
                    cvRepository = cvRepository,
                    onCvCreated = onNavigateToEditor,
                    showSnackbar = { scope.launch { snackbarHostState.showSnackbar(it) } }
                )
            }
        }
    }
}

@Composable
private fun AiPromptsTab(
    selectedCv: Cv?,
    aiRepository: AiRepository,
    cvRepository: CvRepository,
    onNavigateToEditor: (cvId: String) -> Unit,
    showSnackbar: (String) -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf<String?>(null) }
    var resultType by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val suggestedPrompts = listOf(
        "Improve my summary",
        "Make this experience more concise",
        "Rewrite this professionally",
        "Create achievement bullets",
        "Make this suitable for a Senior Engineer role",
        "What should I improve in my CV?"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        item {
            if (selectedCv != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Active Context: ${selectedCv.title}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            Text(selectedCv.personalInfo.professionalTitle.ifBlank { "Ready for enhancement" }, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            } else {
                Text(
                    text = "No CV currently selected. Create or select a CV first to run contextual AI enhancements.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text("Suggested AI Operations", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(suggestedPrompts) { prompt ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(enabled = selectedCv != null && !isLoading) {
                        if (selectedCv == null) return@clickable
                        scope.launch {
                            isLoading = true
                            when (prompt) {
                                "Improve my summary", "Rewrite this professionally", "Make this suitable for a Senior Engineer role" -> {
                                    val req = AiSummaryRequest(
                                        jobTitle = selectedCv.personalInfo.professionalTitle.ifBlank { "Senior Engineer" },
                                        yearsOfExperience = "5+",
                                        skills = selectedCv.skills.map { it.name },
                                        existingSummary = selectedCv.professionalSummary
                                    )
                                    val res = aiRepository.generateSummary(req)
                                    res.onSuccess {
                                        resultText = it.summary
                                        resultType = "summary"
                                    }.onFailure { err ->
                                        showSnackbar(err.message ?: "AI request failed")
                                    }
                                }
                                "Create achievement bullets", "Make this experience more concise" -> {
                                    val req = AiAchievementRequest(
                                        roleOrContext = selectedCv.personalInfo.professionalTitle.ifBlank { "Software Engineer" },
                                        rawResponsibilities = selectedCv.experiences.firstOrNull()?.description ?: "Developed core mobile features and fixed bugs"
                                    )
                                    val res = aiRepository.generateAchievements(req)
                                    res.onSuccess {
                                        resultText = it.achievements.joinToString("\n• ", prefix = "• ")
                                        resultType = "achievements"
                                    }.onFailure { err ->
                                        showSnackbar(err.message ?: "AI request failed")
                                    }
                                }
                                else -> {
                                    resultText = "Actionable Recommendations for '${selectedCv.title}':\n\n" +
                                            "1. Summary: Ensure your career goals explicitly target high-impact outcomes.\n" +
                                            "2. Experience: Quantify your achievements (e.g. latency reduction, architecture scalability).\n" +
                                            "3. Skills: Group related technologies into clear domains (e.g. Core, Frameworks, Architecture).\n" +
                                            "4. Projects: Add live repository or production URLs to verify credentials."
                                    resultType = "critique"
                                }
                            }
                            isLoading = false
                        }
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = prompt, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (isLoading) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.tertiary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("AI is processing your request...", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        if (resultText != null) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("AI Generated Response", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { resultText = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss")
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = resultText.orEmpty(), style = MaterialTheme.typography.bodyMedium, lineHeight = 22.sp)

                        if (selectedCv != null && resultType == "summary") {
                            Spacer(modifier = Modifier.height(16.dp))
                            CvPrimaryButton(
                                text = "Apply to Summary in CV",
                                onClick = {
                                    val updated = selectedCv.copy(professionalSummary = resultText.orEmpty())
                                    scope.launch {
                                        cvRepository.saveCv(updated)
                                        showSnackbar("Summary applied to CV!")
                                        onNavigateToEditor(selectedCv.id)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FullCvWizardTab(
    aiRepository: AiRepository,
    cvRepository: CvRepository,
    onCvCreated: (cvId: String) -> Unit,
    showSnackbar: (String) -> Unit
) {
    var jobTitle by remember { mutableStateOf("") }
    var yearsExp by remember { mutableStateOf("3+") }
    var industry by remember { mutableStateOf("Technology / Mobile") }
    var rawSkills by remember { mutableStateOf("Kotlin, Jetpack Compose, Coroutines, Room, Koin, Git") }
    var rawExperience by remember { mutableStateOf("Worked as Android Engineer building scalable mobile features.") }
    var education by remember { mutableStateOf("B.S. in Computer Science") }
    var projects by remember { mutableStateOf("Built CV Maker app and several open source Kotlin libraries.") }
    var isGenerating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        item {
            Text("Create CV with AI Wizard", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Fill out your core career details and AI will draft a complete, structured CV.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))

            CvTextField(value = jobTitle, onValueChange = { jobTitle = it }, label = "Target Job Title *", placeholder = "e.g., Senior Android Engineer")
            Spacer(modifier = Modifier.height(10.dp))
            CvTextField(value = yearsExp, onValueChange = { yearsExp = it }, label = "Years of Experience", placeholder = "5+")
            Spacer(modifier = Modifier.height(10.dp))
            CvTextField(value = industry, onValueChange = { industry = it }, label = "Industry", placeholder = "Software / Mobile")
            Spacer(modifier = Modifier.height(10.dp))
            CvTextField(value = rawSkills, onValueChange = { rawSkills = it }, label = "Key Skills (comma separated)")
            Spacer(modifier = Modifier.height(10.dp))
            CvTextField(value = rawExperience, onValueChange = { rawExperience = it }, label = "Work Experience Overview", singleLine = false, maxLines = 4)
            Spacer(modifier = Modifier.height(10.dp))
            CvTextField(value = education, onValueChange = { education = it }, label = "Education")
            Spacer(modifier = Modifier.height(10.dp))
            CvTextField(value = projects, onValueChange = { projects = it }, label = "Projects Overview", singleLine = false, maxLines = 3)
            Spacer(modifier = Modifier.height(20.dp))

            CvAiButton(
                text = "Generate Full CV",
                onClick = {
                    scope.launch {
                        isGenerating = true
                        val skillsList = rawSkills.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        val request = AiCvRequest(
                            jobTitle = jobTitle,
                            yearsOfExperience = yearsExp,
                            industry = industry,
                            skills = skillsList,
                            rawExperience = rawExperience,
                            education = education,
                            projects = projects
                        )
                        val result = aiRepository.generateFullCv(request)
                        isGenerating = false

                        result.onSuccess { response ->
                            val newCv = Cv(
                                id = UUID.randomUUID().toString(),
                                title = "$jobTitle CV",
                                personalInfo = PersonalInfo(
                                    fullName = "Your Name",
                                    professionalTitle = jobTitle,
                                    email = "contact@example.com"
                                ),
                                professionalSummary = response.professionalSummary,
                                experiences = response.experiences.map { exp ->
                                    Experience(
                                        id = UUID.randomUUID().toString(),
                                        company = exp.company,
                                        position = exp.position,
                                        description = exp.description,
                                        achievements = exp.achievements
                                    )
                                },
                                skills = response.skills.map {
                                    Skill(id = UUID.randomUUID().toString(), name = it)
                                },
                                projects = response.projects.map { proj ->
                                    Project(
                                        id = UUID.randomUUID().toString(),
                                        name = proj.name,
                                        description = proj.description,
                                        technologies = proj.technologies,
                                        achievements = proj.achievements
                                    )
                                }
                            )
                            cvRepository.saveCv(newCv)
                            showSnackbar("CV Generated successfully!")
                            onCvCreated(newCv.id)
                        }.onFailure { err ->
                            showSnackbar(err.message ?: "Could not generate full CV. Try again.")
                        }
                    }
                },
                isLoading = isGenerating,
                enabled = jobTitle.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
