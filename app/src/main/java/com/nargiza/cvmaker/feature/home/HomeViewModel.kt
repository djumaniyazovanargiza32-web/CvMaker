package com.nargiza.cvmaker.feature.home

import androidx.lifecycle.viewModelScope
import com.nargiza.cvmaker.core.model.Cv
import com.nargiza.cvmaker.core.ui.MviViewModel
import com.nargiza.cvmaker.domain.usecase.CreateCvUseCase
import com.nargiza.cvmaker.domain.usecase.DeleteCvUseCase
import com.nargiza.cvmaker.domain.usecase.DuplicateCvUseCase
import com.nargiza.cvmaker.domain.usecase.GeneratePdfUseCase
import com.nargiza.cvmaker.domain.usecase.ObserveCvsUseCase
import com.nargiza.cvmaker.domain.usecase.UpdateCvUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(
    private val observeCvsUseCase: ObserveCvsUseCase,
    private val createCvUseCase: CreateCvUseCase,
    private val duplicateCvUseCase: DuplicateCvUseCase,
    private val deleteCvUseCase: DeleteCvUseCase,
    private val updateCvUseCase: UpdateCvUseCase,
    private val generatePdfUseCase: GeneratePdfUseCase
) : MviViewModel<HomeIntent, HomeState, HomeEffect>(HomeState(isLoading = true)) {

    init {
        loadCvs()
    }

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadCvs -> loadCvs()
            is HomeIntent.UpdateSearchQuery -> updateSearch(intent.query)
            is HomeIntent.CreateNewCv -> createCv()
            is HomeIntent.CreateWithAi -> sendEffect(HomeEffect.NavigateToAiAssistant)
            is HomeIntent.CreateSampleCv -> createSampleCv()
            is HomeIntent.DuplicateCv -> duplicateCv(intent.id)
            is HomeIntent.DeleteCv -> deleteCv(intent.id)
            is HomeIntent.ExportPdf -> exportPdf(intent.cv)
            is HomeIntent.ShareCv -> shareCv(intent.cv)
            is HomeIntent.OpenCv -> sendEffect(HomeEffect.NavigateToEditor(intent.id))
            is HomeIntent.PreviewCv -> sendEffect(HomeEffect.NavigateToPreview(intent.id))
        }
    }

    private fun loadCvs() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            observeCvsUseCase()
                .catch { e ->
                    setState { copy(isLoading = false, error = e.message ?: "Failed to load CVs") }
                }
                .collect { cvList ->
                    setState {
                        copy(
                            cvs = cvList,
                            filteredCvs = filterList(cvList, searchQuery),
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun updateSearch(query: String) {
        setState {
            copy(
                searchQuery = query,
                filteredCvs = filterList(cvs, query)
            )
        }
    }

    private fun filterList(list: List<Cv>, query: String): List<Cv> {
        if (query.isBlank()) return list
        val q = query.trim().lowercase()
        return list.filter { cv ->
            cv.title.lowercase().contains(q) ||
                    cv.personalInfo.fullName.lowercase().contains(q) ||
                    cv.personalInfo.professionalTitle.lowercase().contains(q) ||
                    cv.experiences.any { it.company.lowercase().contains(q) || it.position.lowercase().contains(q) }
        }
    }

    private fun createCv() {
        viewModelScope.launch {
            try {
                val cv = createCvUseCase(title = "Untitled CV")
                sendEffect(HomeEffect.NavigateToEditor(cv.id, step = 1))
            } catch (e: Exception) {
                sendEffect(HomeEffect.ShowMessage("Failed to create CV: ${e.message}"))
            }
        }
    }

    private fun createSampleCv() {
        viewModelScope.launch {
            try {
                val sample = Cv.sample()
                updateCvUseCase(sample)
                sendEffect(HomeEffect.ShowMessage("Demo CV created!"))
                sendEffect(HomeEffect.NavigateToEditor(sample.id, step = 1))
            } catch (e: Exception) {
                sendEffect(HomeEffect.ShowMessage("Could not create sample CV: ${e.message}"))
            }
        }
    }

    private fun duplicateCv(id: String) {
        viewModelScope.launch {
            try {
                duplicateCvUseCase(id)
                sendEffect(HomeEffect.ShowMessage("CV duplicated successfully"))
            } catch (e: Exception) {
                sendEffect(HomeEffect.ShowMessage("Could not duplicate CV: ${e.message}"))
            }
        }
    }

    private fun deleteCv(id: String) {
        viewModelScope.launch {
            try {
                deleteCvUseCase(id)
                sendEffect(HomeEffect.ShowMessage("CV deleted"))
            } catch (e: Exception) {
                sendEffect(HomeEffect.ShowMessage("Could not delete CV: ${e.message}"))
            }
        }
    }

    private fun exportPdf(cv: Cv) {
        viewModelScope.launch {
            setState { copy(isExporting = true) }
            val result = generatePdfUseCase.generate(cv)
            setState { copy(isExporting = false) }
            result.onSuccess { file ->
                sendEffect(HomeEffect.ShowMessage("PDF exported to ${file.name}"))
                generatePdfUseCase.openPdf(file)
            }.onFailure { err ->
                sendEffect(HomeEffect.ShowMessage(err.message ?: "Failed to generate PDF"))
            }
        }
    }

    private fun shareCv(cv: Cv) {
        viewModelScope.launch {
            setState { copy(isExporting = true) }
            val result = generatePdfUseCase.generate(cv)
            setState { copy(isExporting = false) }
            result.onSuccess { file ->
                generatePdfUseCase.sharePdf(file)
            }.onFailure { err ->
                sendEffect(HomeEffect.ShowMessage(err.message ?: "Failed to generate PDF for sharing"))
            }
        }
    }
}
