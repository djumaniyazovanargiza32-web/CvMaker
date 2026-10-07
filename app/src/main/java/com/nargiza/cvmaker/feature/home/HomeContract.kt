package com.nargiza.cvmaker.feature.home

import com.nargiza.cvmaker.core.model.Cv
import com.nargiza.cvmaker.core.ui.ViewEffect
import com.nargiza.cvmaker.core.ui.ViewIntent
import com.nargiza.cvmaker.core.ui.ViewState
import java.io.File

sealed interface HomeIntent : ViewIntent {
    data object LoadCvs : HomeIntent
    data class UpdateSearchQuery(val query: String) : HomeIntent
    data object CreateNewCv : HomeIntent
    data object CreateWithAi : HomeIntent
    data object CreateSampleCv : HomeIntent
    data class DuplicateCv(val id: String) : HomeIntent
    data class DeleteCv(val id: String) : HomeIntent
    data class ExportPdf(val cv: Cv) : HomeIntent
    data class ShareCv(val cv: Cv) : HomeIntent
    data class OpenCv(val id: String) : HomeIntent
    data class PreviewCv(val id: String) : HomeIntent
}

data class HomeState(
    val cvs: List<Cv> = emptyList(),
    val filteredCvs: List<Cv> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isExporting: Boolean = false,
    val error: String? = null
) : ViewState

sealed interface HomeEffect : ViewEffect {
    data class NavigateToEditor(val cvId: String, val step: Int = 1) : HomeEffect
    data class NavigateToPreview(val cvId: String) : HomeEffect
    data object NavigateToAiAssistant : HomeEffect
    data class NavigateToJobOptimizer(val cvId: String? = null) : HomeEffect
    data object NavigateToTemplates : HomeEffect
    data object NavigateToSettings : HomeEffect
    data class ShowMessage(val message: String) : HomeEffect
    data class OpenPdfFile(val file: File) : HomeEffect
    data class SharePdfFile(val file: File) : HomeEffect
}
