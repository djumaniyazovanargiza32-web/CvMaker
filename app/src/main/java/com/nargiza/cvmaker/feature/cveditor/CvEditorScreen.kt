package com.nargiza.cvmaker.feature.cveditor

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nargiza.cvmaker.core.designsystem.CvAiButton
import com.nargiza.cvmaker.core.designsystem.CvChip
import com.nargiza.cvmaker.core.designsystem.CvDropdown
import com.nargiza.cvmaker.core.designsystem.CvEducationCard
import com.nargiza.cvmaker.core.designsystem.CvExperienceCard
import com.nargiza.cvmaker.core.designsystem.CvLoadingState
import com.nargiza.cvmaker.core.designsystem.CvPrimaryButton
import com.nargiza.cvmaker.core.designsystem.CvProjectCard
import com.nargiza.cvmaker.core.designsystem.CvSecondaryButton
import com.nargiza.cvmaker.core.designsystem.CvSkillChip
import com.nargiza.cvmaker.core.designsystem.CvTemplateCard
import com.nargiza.cvmaker.core.designsystem.CvTextField
import com.nargiza.cvmaker.core.model.Certification
import com.nargiza.cvmaker.core.model.Education
import com.nargiza.cvmaker.core.model.Experience
import com.nargiza.cvmaker.core.model.Language
import com.nargiza.cvmaker.core.model.Project
import com.nargiza.cvmaker.core.model.Skill
import com.nargiza.cvmaker.core.model.SkillLevel
import com.nargiza.cvmaker.core.model.TemplateType
import com.nargiza.cvmaker.core.ui.CvDocumentPaper
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CvEditorScreen(
    cvId: String,
    initialStep: Int = 1,
    viewModel: CvEditorViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPreview: (cvId: String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(cvId) {
        viewModel.onIntent(CvEditorIntent.LoadCv(cvId))
        if (initialStep in 1..10) {
            viewModel.onIntent(CvEditorIntent.SetStep(initialStep))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is CvEditorEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                is CvEditorEffect.NavigateBack -> onNavigateBack()
                is CvEditorEffect.NavigateToPreview -> onNavigateToPreview(effect.cvId)
                else -> Unit
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Step ${state.currentStep} of ${state.totalSteps}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = getStepTitle(state.currentStep),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onIntent(CvEditorIntent.SaveCv) }) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = "Save CV")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Column {
                    LinearProgressIndicator(
                        progress = { state.currentStep.toFloat() / state.totalSteps.toFloat() },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.outlineVariant
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CvSecondaryButton(
                            text = if (state.currentStep == 1) "Exit" else "Back",
                            onClick = { viewModel.onIntent(CvEditorIntent.PreviousStep) },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        CvPrimaryButton(
                            text = if (state.currentStep == state.totalSteps) "Finish & Preview" else "Next",
                            onClick = {
                                if (state.currentStep == state.totalSteps) {
                                    onNavigateToPreview(state.cv.id)
                                } else {
                                    viewModel.onIntent(CvEditorIntent.NextStep)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (state.isLoading) {
            CvLoadingState(message = "Loading your CV...", modifier = Modifier.padding(padding))
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                AnimatedContent(
                    targetState = state.currentStep,
                    label = "step_animation"
                ) { step ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp)
                    ) {
                        when (step) {
                            1 -> StepPersonalInfo(state = state, viewModel = viewModel)
                            2 -> StepSummary(state = state, viewModel = viewModel)
                            3 -> StepExperience(state = state, viewModel = viewModel)
                            4 -> StepEducation(state = state, viewModel = viewModel)
                            5 -> StepSkills(state = state, viewModel = viewModel)
                            6 -> StepProjects(state = state, viewModel = viewModel)
                            7 -> StepCertifications(state = state, viewModel = viewModel)
                            8 -> StepLanguages(state = state, viewModel = viewModel)
                            9 -> StepTemplate(state = state, viewModel = viewModel)
                            10 -> StepPreview(state = state, viewModel = viewModel)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    // AI Privacy Consent Dialog (Requirement 70)
    if (state.showAiPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onAiConsentDismissed() },
            title = { Text("AI Processing Notice") },
            text = {
                Text(
                    "The information you submit may be sent to our AI service to generate or improve your CV. " +
                            "No personal data is logged or permanently stored by the AI service."
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onAiConsentConfirmed() }) {
                    Text("Continue")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onAiConsentDismissed() }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Review Before Apply Dialog (Requirement 98)
    if (state.aiReviewSuggestion != null && state.aiReviewTargetField != null) {
        var editedSuggestion by remember(state.aiReviewSuggestion) {
            mutableStateOf(state.aiReviewSuggestion.orEmpty())
        }

        AlertDialog(
            onDismissRequest = { viewModel.dismissAiReview() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Apply AI Improvement?")
                }
            },
            text = {
                Column {
                    Text(
                        "Review and edit the AI-generated text before replacing your current content:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = editedSuggestion,
                        onValueChange = { editedSuggestion = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onIntent(
                            CvEditorIntent.ConfirmApplyAiText(
                                newText = editedSuggestion,
                                targetField = state.aiReviewTargetField.orEmpty()
                            )
                        )
                    }
                ) {
                    Text("Apply Changes", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissAiReview() }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun getStepTitle(step: Int): String {
    return when (step) {
        1 -> "Personal Information"
        2 -> "Professional Summary"
        3 -> "Work Experience"
        4 -> "Education"
        5 -> "Skills"
        6 -> "Projects"
        7 -> "Certifications"
        8 -> "Languages"
        9 -> "CV Template"
        10 -> "Review & Export"
        else -> "CV Editor"
    }
}

// ----------------- STEP 1: PERSONAL INFORMATION -----------------
@Composable
private fun StepPersonalInfo(
    state: CvEditorState,
    viewModel: CvEditorViewModel
) {
    val info = state.cv.personalInfo

    Text(
        text = "Contact & Header Details",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "This appears at the top of your CV and is essential for recruiters to reach you.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(18.dp))

    CvTextField(
        value = info.fullName,
        onValueChange = { viewModel.onIntent(CvEditorIntent.UpdatePersonalInfo(info.copy(fullName = it))) },
        label = "Full Name *",
        placeholder = "e.g., Alex Johnson",
        leadingIcon = Icons.Default.Person,
        error = state.validationErrors["fullName"]
    )
    Spacer(modifier = Modifier.height(12.dp))

    CvTextField(
        value = info.professionalTitle,
        onValueChange = { viewModel.onIntent(CvEditorIntent.UpdatePersonalInfo(info.copy(professionalTitle = it))) },
        label = "Professional Title *",
        placeholder = "e.g., Senior Android Engineer",
        leadingIcon = Icons.Default.Work,
        error = state.validationErrors["professionalTitle"]
    )
    Spacer(modifier = Modifier.height(12.dp))

    CvTextField(
        value = info.email,
        onValueChange = { viewModel.onIntent(CvEditorIntent.UpdatePersonalInfo(info.copy(email = it))) },
        label = "Email Address *",
        placeholder = "alex@example.com",
        leadingIcon = Icons.Default.Email,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        error = state.validationErrors["email"]
    )
    Spacer(modifier = Modifier.height(12.dp))

    CvTextField(
        value = info.phone,
        onValueChange = { viewModel.onIntent(CvEditorIntent.UpdatePersonalInfo(info.copy(phone = it))) },
        label = "Phone Number",
        placeholder = "+1 (555) 234-5678",
        leadingIcon = Icons.Default.Phone,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        error = state.validationErrors["phone"]
    )
    Spacer(modifier = Modifier.height(12.dp))

    CvTextField(
        value = info.location,
        onValueChange = { viewModel.onIntent(CvEditorIntent.UpdatePersonalInfo(info.copy(location = it))) },
        label = "Location",
        placeholder = "City, Country (e.g. San Francisco, CA)",
        leadingIcon = Icons.Default.LocationOn
    )
    Spacer(modifier = Modifier.height(12.dp))

    CvTextField(
        value = info.website.orEmpty(),
        onValueChange = { viewModel.onIntent(CvEditorIntent.UpdatePersonalInfo(info.copy(website = it))) },
        label = "Portfolio / Website",
        placeholder = "https://alexjohnson.dev",
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
        error = state.validationErrors["website"]
    )
    Spacer(modifier = Modifier.height(12.dp))

    CvTextField(
        value = info.linkedIn.orEmpty(),
        onValueChange = { viewModel.onIntent(CvEditorIntent.UpdatePersonalInfo(info.copy(linkedIn = it))) },
        label = "LinkedIn Profile",
        placeholder = "linkedin.com/in/alexjohnson",
        error = state.validationErrors["linkedIn"]
    )
    Spacer(modifier = Modifier.height(12.dp))

    CvTextField(
        value = info.github.orEmpty(),
        onValueChange = { viewModel.onIntent(CvEditorIntent.UpdatePersonalInfo(info.copy(github = it))) },
        label = "GitHub Profile",
        placeholder = "github.com/alexjohnson",
        error = state.validationErrors["github"]
    )
}

// ----------------- STEP 2: SUMMARY -----------------
@Composable
private fun StepSummary(
    state: CvEditorState,
    viewModel: CvEditorViewModel
) {
    Text(
        text = "Professional Summary",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "A concise 3-5 sentence elevator pitch highlighting your key strengths and accomplishments.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(16.dp))

    CvAiButton(
        text = "Generate / Polish with AI",
        onClick = { viewModel.onIntent(CvEditorIntent.RequestGenerateSummary) },
        isLoading = state.isGeneratingAi,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))

    CvTextField(
        value = state.cv.professionalSummary,
        onValueChange = { viewModel.onIntent(CvEditorIntent.UpdateSummary(it)) },
        label = "Summary",
        placeholder = "Write your summary or tap 'Generate / Polish with AI' above...",
        singleLine = false,
        maxLines = 8,
        modifier = Modifier.fillMaxWidth()
    )
}

// ----------------- STEP 3: EXPERIENCE -----------------
@Composable
private fun StepExperience(
    state: CvEditorState,
    viewModel: CvEditorViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var editingExp by remember { mutableStateOf<Experience?>(null) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Work Experience",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "List roles in reverse chronological order.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    Spacer(modifier = Modifier.height(14.dp))

    CvSecondaryButton(
        text = "+ Add Experience",
        onClick = {
            editingExp = Experience(id = UUID.randomUUID().toString())
            showDialog = true
        },
        icon = Icons.Default.Add,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))

    if (state.cv.experiences.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Text(
                text = "No work experience added yet. Tap '+ Add Experience' to include roles.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        }
    } else {
        state.cv.experiences.forEach { exp ->
            CvExperienceCard(
                experience = exp,
                onEdit = {
                    editingExp = exp
                    showDialog = true
                },
                onDelete = { viewModel.onIntent(CvEditorIntent.DeleteExperience(exp.id)) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    if (showDialog && editingExp != null) {
        ExperienceDialog(
            initial = editingExp!!,
            isGeneratingAi = state.isGeneratingAi,
            onImproveWithAi = { exp -> viewModel.onIntent(CvEditorIntent.RequestImproveExperience(exp)) },
            onDismiss = { showDialog = false },
            onSave = { updated ->
                if (state.cv.experiences.any { it.id == updated.id }) {
                    viewModel.onIntent(CvEditorIntent.UpdateExperience(updated))
                } else {
                    viewModel.onIntent(CvEditorIntent.AddExperience(updated))
                }
                showDialog = false
            }
        )
    }
}

@Composable
private fun ExperienceDialog(
    initial: Experience,
    isGeneratingAi: Boolean,
    onImproveWithAi: (Experience) -> Unit,
    onDismiss: () -> Unit,
    onSave: (Experience) -> Unit
) {
    var position by remember { mutableStateOf(initial.position) }
    var company by remember { mutableStateOf(initial.company) }
    var location by remember { mutableStateOf(initial.location) }
    var startDate by remember { mutableStateOf(initial.startDate) }
    var endDate by remember { mutableStateOf(initial.endDate.orEmpty()) }
    var isCurrent by remember { mutableStateOf(initial.isCurrent) }
    var description by remember { mutableStateOf(initial.description) }
    var rawAchievements by remember { mutableStateOf(initial.achievements.joinToString("\n")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial.position.isEmpty()) "Add Experience" else "Edit Experience") },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                CvTextField(
                    value = position,
                    onValueChange = { position = it },
                    label = "Job Title / Position *"
                )
                Spacer(modifier = Modifier.height(8.dp))
                CvTextField(
                    value = company,
                    onValueChange = { company = it },
                    label = "Company Name *"
                )
                Spacer(modifier = Modifier.height(8.dp))
                CvTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = "Location"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    CvTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = "Start Date",
                        placeholder = "2021",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (!isCurrent) {
                        CvTextField(
                            value = endDate,
                            onValueChange = { endDate = it },
                            label = "End Date",
                            placeholder = "2024",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Checkbox(checked = isCurrent, onCheckedChange = { isCurrent = it })
                    Text("Currently Working Here", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(8.dp))
                CvAiButton(
                    text = "Improve Description with AI",
                    onClick = {
                        onImproveWithAi(
                            initial.copy(
                                position = position,
                                company = company,
                                description = description
                            )
                        )
                    },
                    isLoading = isGeneratingAi,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                CvTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Role Description",
                    singleLine = false,
                    maxLines = 4
                )
                Spacer(modifier = Modifier.height(8.dp))
                CvTextField(
                    value = rawAchievements,
                    onValueChange = { rawAchievements = it },
                    label = "Key Achievements (one per line)",
                    placeholder = "Reduced startup time by 30%...\nArchitected core feature...",
                    singleLine = false,
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val achievementsList = rawAchievements.lines().map { it.trim() }.filter { it.isNotEmpty() }
                    onSave(
                        initial.copy(
                            position = position,
                            company = company,
                            location = location,
                            startDate = startDate,
                            endDate = if (isCurrent) null else endDate,
                            isCurrent = isCurrent,
                            description = description,
                            achievements = achievementsList
                        )
                    )
                },
                enabled = position.isNotBlank() && company.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// ----------------- STEP 4: EDUCATION -----------------
@Composable
private fun StepEducation(
    state: CvEditorState,
    viewModel: CvEditorViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var editingEdu by remember { mutableStateOf<Education?>(null) }

    Text(
        text = "Education",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Degrees, colleges, universities, and academic accomplishments.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(14.dp))

    CvSecondaryButton(
        text = "+ Add Education",
        onClick = {
            editingEdu = Education(id = UUID.randomUUID().toString())
            showDialog = true
        },
        icon = Icons.Default.Add,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))

    if (state.cv.education.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Text(
                text = "No education added yet. Tap '+ Add Education' to add degrees.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        }
    } else {
        state.cv.education.forEach { edu ->
            CvEducationCard(
                education = edu,
                onEdit = {
                    editingEdu = edu
                    showDialog = true
                },
                onDelete = { viewModel.onIntent(CvEditorIntent.DeleteEducation(edu.id)) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    if (showDialog && editingEdu != null) {
        var inst by remember { mutableStateOf(editingEdu!!.institution) }
        var degree by remember { mutableStateOf(editingEdu!!.degree) }
        var field by remember { mutableStateOf(editingEdu!!.fieldOfStudy) }
        var start by remember { mutableStateOf(editingEdu!!.startDate) }
        var end by remember { mutableStateOf(editingEdu!!.endDate.orEmpty()) }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Education Details") },
            text = {
                Column {
                    CvTextField(value = inst, onValueChange = { inst = it }, label = "Institution / University *")
                    Spacer(modifier = Modifier.height(8.dp))
                    CvTextField(value = degree, onValueChange = { degree = it }, label = "Degree (e.g. B.S., M.S.) *")
                    Spacer(modifier = Modifier.height(8.dp))
                    CvTextField(value = field, onValueChange = { field = it }, label = "Field of Study (e.g. Computer Science)")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        CvTextField(value = start, onValueChange = { start = it }, label = "Start Year", modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        CvTextField(value = end, onValueChange = { end = it }, label = "End Year", modifier = Modifier.weight(1f))
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val updated = editingEdu!!.copy(
                            institution = inst,
                            degree = degree,
                            fieldOfStudy = field,
                            startDate = start,
                            endDate = end
                        )
                        if (state.cv.education.any { it.id == updated.id }) {
                            viewModel.onIntent(CvEditorIntent.UpdateEducation(updated))
                        } else {
                            viewModel.onIntent(CvEditorIntent.AddEducation(updated))
                        }
                        showDialog = false
                    },
                    enabled = inst.isNotBlank() && degree.isNotBlank()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// ----------------- STEP 5: SKILLS -----------------
@Composable
private fun StepSkills(
    state: CvEditorState,
    viewModel: CvEditorViewModel
) {
    var skillName by remember { mutableStateOf("") }
    val categories = listOf("Programming", "Frameworks", "Databases", "Cloud", "DevOps", "Tools", "Soft Skills", "Languages", "Other")
    var selectedCatIdx by remember { mutableStateOf(0) }
    var selectedLevel by remember { mutableStateOf(SkillLevel.ADVANCED) }

    Text(
        text = "Key Skills & Proficiencies",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Highlight core technologies, frameworks, and domain expertise.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(16.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            CvTextField(
                value = skillName,
                onValueChange = { skillName = it },
                label = "Skill Name",
                placeholder = "e.g., Kotlin, Docker, GraphQL"
            )
            Spacer(modifier = Modifier.height(10.dp))
            CvDropdown(
                label = "Category",
                options = categories,
                selectedIndex = selectedCatIdx,
                onSelectIndex = { selectedCatIdx = it }
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text("Proficiency Level", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SkillLevel.entries.forEach { level ->
                    CvChip(
                        text = level.displayName,
                        isSelected = selectedLevel == level,
                        onClick = { selectedLevel = level }
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            CvPrimaryButton(
                text = "+ Add Skill",
                onClick = {
                    if (skillName.isNotBlank()) {
                        viewModel.onIntent(
                            CvEditorIntent.AddSkill(
                                Skill(
                                    id = UUID.randomUUID().toString(),
                                    name = skillName.trim(),
                                    category = categories[selectedCatIdx],
                                    level = selectedLevel
                                )
                            )
                        )
                        skillName = ""
                    }
                },
                enabled = skillName.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text("Added Skills (${state.cv.skills.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(10.dp))

    val grouped = state.cv.skills.groupBy { it.category }
    grouped.forEach { (cat, skills) ->
        Text(cat, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        skills.forEach { skill ->
            CvSkillChip(
                skill = skill,
                onDelete = { viewModel.onIntent(CvEditorIntent.DeleteSkill(skill.id)) },
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

// ----------------- STEP 6: PROJECTS -----------------
@Composable
private fun StepProjects(
    state: CvEditorState,
    viewModel: CvEditorViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var editingProj by remember { mutableStateOf<Project?>(null) }

    Text(
        text = "Key Projects",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Showcase open source, personal, or impactful commercial projects.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(14.dp))

    CvSecondaryButton(
        text = "+ Add Project",
        onClick = {
            editingProj = Project(id = UUID.randomUUID().toString())
            showDialog = true
        },
        icon = Icons.Default.Add,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))

    if (state.cv.projects.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Text(
                text = "No projects added yet. Tap '+ Add Project' to highlight your work.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        }
    } else {
        state.cv.projects.forEach { proj ->
            CvProjectCard(
                project = proj,
                onEdit = {
                    editingProj = proj
                    showDialog = true
                },
                onDelete = { viewModel.onIntent(CvEditorIntent.DeleteProject(proj.id)) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    if (showDialog && editingProj != null) {
        var name by remember { mutableStateOf(editingProj!!.name) }
        var role by remember { mutableStateOf(editingProj!!.role.orEmpty()) }
        var techStr by remember { mutableStateOf(editingProj!!.technologies.joinToString(", ")) }
        var desc by remember { mutableStateOf(editingProj!!.description) }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Project Details") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    CvTextField(value = name, onValueChange = { name = it }, label = "Project Name *")
                    Spacer(modifier = Modifier.height(8.dp))
                    CvTextField(value = role, onValueChange = { role = it }, label = "Role (e.g. Lead Architect)")
                    Spacer(modifier = Modifier.height(8.dp))
                    CvTextField(value = techStr, onValueChange = { techStr = it }, label = "Technologies (comma separated)", placeholder = "Kotlin, Compose, Room")
                    Spacer(modifier = Modifier.height(8.dp))
                    CvAiButton(
                        text = "Improve Description with AI",
                        onClick = {
                            viewModel.onIntent(
                                CvEditorIntent.RequestImproveProject(
                                    editingProj!!.copy(
                                        name = name,
                                        description = desc,
                                        technologies = techStr.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                        role = role
                                    )
                                )
                            )
                        },
                        isLoading = state.isGeneratingAi,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CvTextField(value = desc, onValueChange = { desc = it }, label = "Description", singleLine = false, maxLines = 4)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val techs = techStr.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        val updated = editingProj!!.copy(
                            name = name,
                            role = role,
                            technologies = techs,
                            description = desc
                        )
                        if (state.cv.projects.any { it.id == updated.id }) {
                            viewModel.onIntent(CvEditorIntent.UpdateProject(updated))
                        } else {
                            viewModel.onIntent(CvEditorIntent.AddProject(updated))
                        }
                        showDialog = false
                    },
                    enabled = name.isNotBlank()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// ----------------- STEP 7: CERTIFICATIONS -----------------
@Composable
private fun StepCertifications(
    state: CvEditorState,
    viewModel: CvEditorViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var issuer by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    Text("Certifications", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(4.dp))
    Text("Professional certifications, licenses, and verified credentials.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(modifier = Modifier.height(14.dp))

    CvSecondaryButton(
        text = "+ Add Certification",
        onClick = { showDialog = true },
        icon = Icons.Default.Add,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))

    state.cv.certifications.forEach { cert ->
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(cert.name, fontWeight = FontWeight.Bold)
                    Text("${cert.issuer} • ${cert.issueDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = { viewModel.onIntent(CvEditorIntent.DeleteCertification(cert.id)) }) {
                    Icon(Icons.Default.Close, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Certification") },
            text = {
                Column {
                    CvTextField(value = name, onValueChange = { name = it }, label = "Certification Name *")
                    Spacer(modifier = Modifier.height(8.dp))
                    CvTextField(value = issuer, onValueChange = { issuer = it }, label = "Issuer (e.g. Google, AWS) *")
                    Spacer(modifier = Modifier.height(8.dp))
                    CvTextField(value = date, onValueChange = { date = it }, label = "Issue Date / Year")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onIntent(
                            CvEditorIntent.AddCertification(
                                Certification(
                                    id = UUID.randomUUID().toString(),
                                    name = name,
                                    issuer = issuer,
                                    issueDate = date
                                )
                            )
                        )
                        name = ""
                        issuer = ""
                        date = ""
                        showDialog = false
                    },
                    enabled = name.isNotBlank() && issuer.isNotBlank()
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// ----------------- STEP 8: LANGUAGES -----------------
@Composable
private fun StepLanguages(
    state: CvEditorState,
    viewModel: CvEditorViewModel
) {
    var langName by remember { mutableStateOf("") }
    val proficiencies = listOf("Native / Fluent", "Professional Working", "Conversational", "Elementary")
    var profIdx by remember { mutableStateOf(0) }

    Text("Languages", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(4.dp))
    Text("Languages you speak and write.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(modifier = Modifier.height(14.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            CvTextField(value = langName, onValueChange = { langName = it }, label = "Language", placeholder = "e.g. English, German, Uzbek")
            Spacer(modifier = Modifier.height(8.dp))
            CvDropdown(label = "Proficiency", options = proficiencies, selectedIndex = profIdx, onSelectIndex = { profIdx = it })
            Spacer(modifier = Modifier.height(12.dp))
            CvPrimaryButton(
                text = "+ Add Language",
                onClick = {
                    if (langName.isNotBlank()) {
                        viewModel.onIntent(
                            CvEditorIntent.AddLanguage(
                                Language(
                                    id = UUID.randomUUID().toString(),
                                    name = langName.trim(),
                                    proficiency = proficiencies[profIdx]
                                )
                            )
                        )
                        langName = ""
                    }
                },
                enabled = langName.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))

    state.cv.languages.forEach { lang ->
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${lang.name} — ${lang.proficiency}", fontWeight = FontWeight.SemiBold)
                IconButton(onClick = { viewModel.onIntent(CvEditorIntent.DeleteLanguage(lang.id)) }) {
                    Icon(Icons.Default.Close, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
    }
}

// ----------------- STEP 9: TEMPLATE -----------------
@Composable
private fun StepTemplate(
    state: CvEditorState,
    viewModel: CvEditorViewModel
) {
    Text("Choose CV Template", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(4.dp))
    Text("Select a tailored layout. All templates maintain strict ATS readability and export to A4 PDF.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(modifier = Modifier.height(16.dp))

    TemplateType.entries.forEach { template ->
        CvTemplateCard(
            template = template,
            isSelected = state.cv.templateId == template.id,
            onSelect = { viewModel.onIntent(CvEditorIntent.SelectTemplate(template.id)) }
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

// ----------------- STEP 10: PREVIEW & EXPORT -----------------
@Composable
private fun StepPreview(
    state: CvEditorState,
    viewModel: CvEditorViewModel
) {
    Text("Document Preview & PDF Export", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(4.dp))
    Text("Review your formatted CV below. Tap 'Download PDF' to generate the official document.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CvPrimaryButton(
            text = "Download PDF",
            onClick = { viewModel.onIntent(CvEditorIntent.ExportPdf) },
            isLoading = state.isExportingPdf,
            icon = Icons.Default.Download,
            modifier = Modifier.weight(1f)
        )
        CvSecondaryButton(
            text = "Share PDF",
            onClick = { viewModel.onIntent(CvEditorIntent.SharePdf) },
            icon = Icons.Default.Share,
            modifier = Modifier.weight(1f)
        )
    }
    Spacer(modifier = Modifier.height(20.dp))

    // Real Paper Document Render
    CvDocumentPaper(
        cv = state.cv,
        modifier = Modifier.fillMaxWidth()
    )
}
