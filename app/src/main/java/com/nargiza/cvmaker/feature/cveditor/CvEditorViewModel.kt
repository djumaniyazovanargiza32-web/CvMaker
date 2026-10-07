package com.nargiza.cvmaker.feature.cveditor

import androidx.lifecycle.viewModelScope
import com.nargiza.cvmaker.core.common.CvValidator
import com.nargiza.cvmaker.core.model.AiExperienceRequest
import com.nargiza.cvmaker.core.model.AiProjectRequest
import com.nargiza.cvmaker.core.model.AiSummaryRequest
import com.nargiza.cvmaker.core.model.Cv
import com.nargiza.cvmaker.core.model.Education
import com.nargiza.cvmaker.core.model.Experience
import com.nargiza.cvmaker.core.model.PersonalInfo
import com.nargiza.cvmaker.core.model.Project
import com.nargiza.cvmaker.core.model.Skill
import com.nargiza.cvmaker.core.ui.MviViewModel
import com.nargiza.cvmaker.domain.repository.SettingsRepository
import com.nargiza.cvmaker.domain.usecase.GeneratePdfUseCase
import com.nargiza.cvmaker.domain.usecase.GenerateSummaryUseCase
import com.nargiza.cvmaker.domain.usecase.GetCvUseCase
import com.nargiza.cvmaker.domain.usecase.ImproveExperienceUseCase
import com.nargiza.cvmaker.domain.usecase.ImproveProjectUseCase
import com.nargiza.cvmaker.domain.usecase.UpdateCvUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

class CvEditorViewModel(
    private val getCvUseCase: GetCvUseCase,
    private val updateCvUseCase: UpdateCvUseCase,
    private val generateSummaryUseCase: GenerateSummaryUseCase,
    private val improveExperienceUseCase: ImproveExperienceUseCase,
    private val improveProjectUseCase: ImproveProjectUseCase,
    private val generatePdfUseCase: GeneratePdfUseCase,
    private val settingsRepository: SettingsRepository
) : MviViewModel<CvEditorIntent, CvEditorState, CvEditorEffect>(CvEditorState(isLoading = true)) {

    override fun onIntent(intent: CvEditorIntent) {
        when (intent) {
            is CvEditorIntent.LoadCv -> loadCv(intent.cvId)
            is CvEditorIntent.SetStep -> setStep(intent.step)
            is CvEditorIntent.NextStep -> nextStep()
            is CvEditorIntent.PreviousStep -> previousStep()
            is CvEditorIntent.UpdateTitle -> updateTitle(intent.title)
            is CvEditorIntent.UpdatePersonalInfo -> updatePersonalInfo(intent.info)
            is CvEditorIntent.UpdateSummary -> updateSummary(intent.summary)
            is CvEditorIntent.AddExperience -> addExperience(intent.experience)
            is CvEditorIntent.UpdateExperience -> updateExperience(intent.experience)
            is CvEditorIntent.DeleteExperience -> deleteExperience(intent.experienceId)
            is CvEditorIntent.AddEducation -> addEducation(intent.education)
            is CvEditorIntent.UpdateEducation -> updateEducation(intent.education)
            is CvEditorIntent.DeleteEducation -> deleteEducation(intent.educationId)
            is CvEditorIntent.AddSkill -> addSkill(intent.skill)
            is CvEditorIntent.DeleteSkill -> deleteSkill(intent.skillId)
            is CvEditorIntent.AddProject -> addProject(intent.project)
            is CvEditorIntent.UpdateProject -> updateProject(intent.project)
            is CvEditorIntent.DeleteProject -> deleteProject(intent.projectId)
            is CvEditorIntent.AddCertification -> addCertification(intent.cert)
            is CvEditorIntent.DeleteCertification -> deleteCertification(intent.certId)
            is CvEditorIntent.AddLanguage -> addLanguage(intent.lang)
            is CvEditorIntent.DeleteLanguage -> deleteLanguage(intent.langId)
            is CvEditorIntent.SelectTemplate -> selectTemplate(intent.templateId)
            is CvEditorIntent.RequestGenerateSummary -> requestGenerateSummary()
            is CvEditorIntent.RequestImproveExperience -> requestImproveExperience(intent.experience)
            is CvEditorIntent.RequestImproveProject -> requestImproveProject(intent.project)
            is CvEditorIntent.ConfirmApplyAiText -> applyAiText(intent.newText, intent.targetField)
            is CvEditorIntent.SaveCv -> saveCurrentCv(showToast = true)
            is CvEditorIntent.ExportPdf -> exportPdf()
            is CvEditorIntent.SharePdf -> sharePdf()
        }
    }

    private fun loadCv(cvId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val prefs = settingsRepository.observePreferences().first()
            val cv = getCvUseCase(cvId) ?: Cv.empty().copy(id = cvId)
            setState {
                copy(
                    cv = cv,
                    isLoading = false,
                    aiConsentGiven = prefs.aiConsentGiven
                )
            }
        }
    }

    private fun setStep(step: Int) {
        if (step in 1..uiState.value.totalSteps) {
            setState { copy(currentStep = step) }
        }
    }

    private fun nextStep() {
        val current = uiState.value.currentStep
        if (current == 1) {
            val validation = CvValidator.validatePersonalInfo(uiState.value.cv.personalInfo)
            if (!validation.isValid) {
                setState { copy(validationErrors = validation.errors) }
                sendEffect(CvEditorEffect.ShowMessage("Please correct highlighted fields before continuing"))
                return
            } else {
                setState { copy(validationErrors = emptyMap()) }
            }
        }

        if (current < uiState.value.totalSteps) {
            setState { copy(currentStep = current + 1) }
            autoSave()
        }
    }

    private fun previousStep() {
        val current = uiState.value.currentStep
        if (current > 1) {
            setState { copy(currentStep = current - 1) }
        } else {
            sendEffect(CvEditorEffect.NavigateBack)
        }
    }

    private fun updateTitle(title: String) {
        updateCvAndSave { copy(title = title) }
    }

    private fun updatePersonalInfo(info: PersonalInfo) {
        val validation = CvValidator.validatePersonalInfo(info)
        setState { copy(validationErrors = validation.errors) }
        updateCvAndSave { copy(personalInfo = info) }
    }

    private fun updateSummary(summary: String) {
        updateCvAndSave { copy(professionalSummary = summary) }
    }

    private fun addExperience(exp: Experience) {
        updateCvAndSave {
            val updated = experiences.toMutableList().apply { add(exp) }
            copy(experiences = updated)
        }
    }

    private fun updateExperience(exp: Experience) {
        updateCvAndSave {
            val updated = experiences.map { if (it.id == exp.id) exp else it }
            copy(experiences = updated)
        }
    }

    private fun deleteExperience(id: String) {
        updateCvAndSave {
            copy(experiences = experiences.filterNot { it.id == id })
        }
    }

    private fun addEducation(edu: Education) {
        updateCvAndSave {
            copy(education = education + edu)
        }
    }

    private fun updateEducation(edu: Education) {
        updateCvAndSave {
            copy(education = education.map { if (it.id == edu.id) edu else it })
        }
    }

    private fun deleteEducation(id: String) {
        updateCvAndSave {
            copy(education = education.filterNot { it.id == id })
        }
    }

    private fun addSkill(skill: Skill) {
        updateCvAndSave {
            copy(skills = skills + skill)
        }
    }

    private fun deleteSkill(id: String) {
        updateCvAndSave {
            copy(skills = skills.filterNot { it.id == id })
        }
    }

    private fun addProject(project: Project) {
        updateCvAndSave {
            copy(projects = projects + project)
        }
    }

    private fun updateProject(project: Project) {
        updateCvAndSave {
            copy(projects = projects.map { if (it.id == project.id) project else it })
        }
    }

    private fun deleteProject(id: String) {
        updateCvAndSave {
            copy(projects = projects.filterNot { it.id == id })
        }
    }

    private fun addCertification(cert: com.nargiza.cvmaker.core.model.Certification) {
        updateCvAndSave {
            copy(certifications = certifications + cert)
        }
    }

    private fun deleteCertification(id: String) {
        updateCvAndSave {
            copy(certifications = certifications.filterNot { it.id == id })
        }
    }

    private fun addLanguage(lang: com.nargiza.cvmaker.core.model.Language) {
        updateCvAndSave {
            copy(languages = languages + lang)
        }
    }

    private fun deleteLanguage(id: String) {
        updateCvAndSave {
            copy(languages = languages.filterNot { it.id == id })
        }
    }

    private fun selectTemplate(templateId: String) {
        updateCvAndSave {
            copy(templateId = templateId)
        }
    }

    private fun updateCvAndSave(transform: Cv.() -> Cv) {
        val updated = uiState.value.cv.transform().copy(updatedAt = System.currentTimeMillis())
        setState { copy(cv = updated) }
        autoSave()
    }

    private fun autoSave() {
        viewModelScope.launch {
            try {
                updateCvUseCase(uiState.value.cv)
            } catch (e: Exception) {
                // Silently handle background autosave failure or log
            }
        }
    }

    private fun saveCurrentCv(showToast: Boolean = false) {
        viewModelScope.launch {
            setState { copy(isSaving = true) }
            try {
                updateCvUseCase(uiState.value.cv)
                if (showToast) {
                    sendEffect(CvEditorEffect.ShowMessage("CV saved successfully"))
                }
            } catch (e: Exception) {
                sendEffect(CvEditorEffect.ShowMessage("Failed to save CV: ${e.message}"))
            } finally {
                setState { copy(isSaving = false) }
            }
        }
    }

    private fun requestGenerateSummary() {
        val cv = uiState.value.cv
        executeWithAiConsent {
            viewModelScope.launch {
                setState { copy(isGeneratingAi = true) }
                val request = AiSummaryRequest(
                    jobTitle = cv.personalInfo.professionalTitle.ifBlank { cv.title },
                    yearsOfExperience = "3+",
                    skills = cv.skills.map { it.name },
                    existingSummary = cv.professionalSummary
                )
                val result = generateSummaryUseCase(request)
                setState { copy(isGeneratingAi = false) }
                result.onSuccess { response ->
                    setState {
                        copy(
                            aiReviewSuggestion = response.summary,
                            aiReviewTargetField = "summary"
                        )
                    }
                }.onFailure { err ->
                    sendEffect(CvEditorEffect.ShowMessage(err.message ?: "AI generation failed. Your existing CV data is safe."))
                }
            }
        }
    }

    private fun requestImproveExperience(exp: Experience) {
        executeWithAiConsent {
            viewModelScope.launch {
                setState { copy(isGeneratingAi = true) }
                val request = AiExperienceRequest(
                    position = exp.position,
                    company = exp.company,
                    rawDescription = exp.description,
                    achievements = exp.achievements
                )
                val result = improveExperienceUseCase(request)
                setState { copy(isGeneratingAi = false) }
                result.onSuccess { response ->
                    setState {
                        copy(
                            aiReviewSuggestion = response.improvedDescription,
                            aiReviewTargetField = "experience:${exp.id}"
                        )
                    }
                }.onFailure { err ->
                    sendEffect(CvEditorEffect.ShowMessage(err.message ?: "AI improvement failed. Your existing CV data is safe."))
                }
            }
        }
    }

    private fun requestImproveProject(proj: Project) {
        executeWithAiConsent {
            viewModelScope.launch {
                setState { copy(isGeneratingAi = true) }
                val request = AiProjectRequest(
                    projectName = proj.name,
                    description = proj.description,
                    technologies = proj.technologies,
                    role = proj.role.orEmpty()
                )
                val result = improveProjectUseCase(request)
                setState { copy(isGeneratingAi = false) }
                result.onSuccess { response ->
                    setState {
                        copy(
                            aiReviewSuggestion = response.improvedDescription,
                            aiReviewTargetField = "project:${proj.id}"
                        )
                    }
                }.onFailure { err ->
                    sendEffect(CvEditorEffect.ShowMessage(err.message ?: "AI improvement failed. Your existing CV data is safe."))
                }
            }
        }
    }

    private fun executeWithAiConsent(action: () -> Unit) {
        if (uiState.value.aiConsentGiven) {
            action()
        } else {
            setState {
                copy(
                    showAiPrivacyDialog = true,
                    pendingAiAction = action
                )
            }
        }
    }

    fun onAiConsentConfirmed() {
        viewModelScope.launch {
            settingsRepository.setAiConsent(true)
            val action = uiState.value.pendingAiAction
            setState {
                copy(
                    aiConsentGiven = true,
                    showAiPrivacyDialog = false,
                    pendingAiAction = null
                )
            }
            action?.invoke()
        }
    }

    fun onAiConsentDismissed() {
        setState {
            copy(
                showAiPrivacyDialog = false,
                pendingAiAction = null
            )
        }
    }

    fun dismissAiReview() {
        setState {
            copy(
                aiReviewSuggestion = null,
                aiReviewTargetField = null
            )
        }
    }

    private fun applyAiText(newText: String, targetField: String) {
        dismissAiReview()
        when {
            targetField == "summary" -> {
                updateSummary(newText)
                sendEffect(CvEditorEffect.ShowMessage("Professional summary updated!"))
            }
            targetField.startsWith("experience:") -> {
                val expId = targetField.removePrefix("experience:")
                val exp = uiState.value.cv.experiences.find { it.id == expId }
                if (exp != null) {
                    updateExperience(exp.copy(description = newText))
                    sendEffect(CvEditorEffect.ShowMessage("Experience description updated!"))
                }
            }
            targetField.startsWith("project:") -> {
                val projId = targetField.removePrefix("project:")
                val proj = uiState.value.cv.projects.find { it.id == projId }
                if (proj != null) {
                    updateProject(proj.copy(description = newText))
                    sendEffect(CvEditorEffect.ShowMessage("Project description updated!"))
                }
            }
        }
    }

    private fun exportPdf() {
        viewModelScope.launch {
            setState { copy(isExportingPdf = true) }
            val result = generatePdfUseCase.generate(uiState.value.cv)
            setState { copy(isExportingPdf = false) }
            result.onSuccess { file ->
                setState { copy(exportedPdfFile = file) }
                sendEffect(CvEditorEffect.ShowMessage("PDF created: ${file.name}"))
                generatePdfUseCase.openPdf(file)
            }.onFailure { err ->
                sendEffect(CvEditorEffect.ShowMessage("PDF generation failed: ${err.message}"))
            }
        }
    }

    private fun sharePdf() {
        viewModelScope.launch {
            val file = uiState.value.exportedPdfFile ?: run {
                setState { copy(isExportingPdf = true) }
                val result = generatePdfUseCase.generate(uiState.value.cv)
                setState { copy(isExportingPdf = false) }
                result.getOrNull()
            }
            if (file != null) {
                generatePdfUseCase.sharePdf(file)
            } else {
                sendEffect(CvEditorEffect.ShowMessage("Could not generate PDF for sharing"))
            }
        }
    }
}
