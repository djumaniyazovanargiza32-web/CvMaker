package com.nargiza.cvmaker.feature.cveditor

import com.nargiza.cvmaker.core.model.Achievement
import com.nargiza.cvmaker.core.model.Certification
import com.nargiza.cvmaker.core.model.Cv
import com.nargiza.cvmaker.core.model.Education
import com.nargiza.cvmaker.core.model.Experience
import com.nargiza.cvmaker.core.model.Language
import com.nargiza.cvmaker.core.model.PersonalInfo
import com.nargiza.cvmaker.core.model.Project
import com.nargiza.cvmaker.core.model.Skill
import com.nargiza.cvmaker.core.ui.ViewEffect
import com.nargiza.cvmaker.core.ui.ViewIntent
import com.nargiza.cvmaker.core.ui.ViewState
import java.io.File

sealed interface CvEditorIntent : ViewIntent {
    data class LoadCv(val cvId: String) : CvEditorIntent
    data class SetStep(val step: Int) : CvEditorIntent
    data object NextStep : CvEditorIntent
    data object PreviousStep : CvEditorIntent
    data class UpdateTitle(val title: String) : CvEditorIntent

    // Personal Info
    data class UpdatePersonalInfo(val info: PersonalInfo) : CvEditorIntent

    // Summary
    data class UpdateSummary(val summary: String) : CvEditorIntent

    // Experience
    data class AddExperience(val experience: Experience) : CvEditorIntent
    data class UpdateExperience(val experience: Experience) : CvEditorIntent
    data class DeleteExperience(val experienceId: String) : CvEditorIntent

    // Education
    data class AddEducation(val education: Education) : CvEditorIntent
    data class UpdateEducation(val education: Education) : CvEditorIntent
    data class DeleteEducation(val educationId: String) : CvEditorIntent

    // Skills
    data class AddSkill(val skill: Skill) : CvEditorIntent
    data class DeleteSkill(val skillId: String) : CvEditorIntent

    // Projects
    data class AddProject(val project: Project) : CvEditorIntent
    data class UpdateProject(val project: Project) : CvEditorIntent
    data class DeleteProject(val projectId: String) : CvEditorIntent

    // Certifications
    data class AddCertification(val cert: Certification) : CvEditorIntent
    data class DeleteCertification(val certId: String) : CvEditorIntent

    // Languages
    data class AddLanguage(val lang: Language) : CvEditorIntent
    data class DeleteLanguage(val langId: String) : CvEditorIntent

    // Template
    data class SelectTemplate(val templateId: String) : CvEditorIntent

    // AI Actions
    data object RequestGenerateSummary : CvEditorIntent
    data class RequestImproveExperience(val experience: Experience) : CvEditorIntent
    data class RequestImproveProject(val project: Project) : CvEditorIntent
    data class ConfirmApplyAiText(val newText: String, val targetField: String) : CvEditorIntent

    // Save & PDF Export
    data object SaveCv : CvEditorIntent
    data object ExportPdf : CvEditorIntent
    data object SharePdf : CvEditorIntent
}

data class CvEditorState(
    val cv: Cv = Cv.empty(),
    val currentStep: Int = 1,
    val totalSteps: Int = 10,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isGeneratingAi: Boolean = false,
    val isExportingPdf: Boolean = false,
    val exportedPdfFile: File? = null,
    val validationErrors: Map<String, String> = emptyMap(),
    val error: String? = null,
    val aiConsentGiven: Boolean = false,
    val showAiPrivacyDialog: Boolean = false,
    val pendingAiAction: (() -> Unit)? = null,
    val aiReviewSuggestion: String? = null,
    val aiReviewTargetField: String? = null
) : ViewState

sealed interface CvEditorEffect : ViewEffect {
    data class ShowMessage(val message: String) : CvEditorEffect
    data class OpenPdf(val file: File) : CvEditorEffect
    data class SharePdf(val file: File) : CvEditorEffect
    data object NavigateBack : CvEditorEffect
    data class NavigateToPreview(val cvId: String) : CvEditorEffect
}
