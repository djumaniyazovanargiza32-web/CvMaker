package com.nargiza.cvmaker

import app.cash.turbine.test
import com.nargiza.cvmaker.core.database.preferences.UserPreferences
import com.nargiza.cvmaker.core.model.Cv
import com.nargiza.cvmaker.core.model.PersonalInfo
import com.nargiza.cvmaker.domain.repository.SettingsRepository
import com.nargiza.cvmaker.domain.usecase.GeneratePdfUseCase
import com.nargiza.cvmaker.domain.usecase.GenerateSummaryUseCase
import com.nargiza.cvmaker.domain.usecase.GetCvUseCase
import com.nargiza.cvmaker.domain.usecase.ImproveExperienceUseCase
import com.nargiza.cvmaker.domain.usecase.ImproveProjectUseCase
import com.nargiza.cvmaker.domain.usecase.UpdateCvUseCase
import com.nargiza.cvmaker.feature.cveditor.CvEditorEffect
import com.nargiza.cvmaker.feature.cveditor.CvEditorIntent
import com.nargiza.cvmaker.feature.cveditor.CvEditorViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CvEditorViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getCvUseCase: GetCvUseCase = mockk()
    private val updateCvUseCase: UpdateCvUseCase = mockk(relaxed = true)
    private val generateSummaryUseCase: GenerateSummaryUseCase = mockk()
    private val improveExperienceUseCase: ImproveExperienceUseCase = mockk()
    private val improveProjectUseCase: ImproveProjectUseCase = mockk()
    private val generatePdfUseCase: GeneratePdfUseCase = mockk(relaxed = true)
    private val settingsRepository: SettingsRepository = mockk()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { settingsRepository.observePreferences() } returns flowOf(UserPreferences(aiConsentGiven = true))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadCv updates state with loaded cv`() = runTest(testDispatcher) {
        val sampleCv = Cv.sample()
        coEvery { getCvUseCase("sample-1") } returns sampleCv

        val viewModel = CvEditorViewModel(
            getCvUseCase = getCvUseCase,
            updateCvUseCase = updateCvUseCase,
            generateSummaryUseCase = generateSummaryUseCase,
            improveExperienceUseCase = improveExperienceUseCase,
            improveProjectUseCase = improveProjectUseCase,
            generatePdfUseCase = generatePdfUseCase,
            settingsRepository = settingsRepository
        )

        viewModel.onIntent(CvEditorIntent.LoadCv("sample-1"))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(sampleCv.title, viewModel.uiState.value.cv.title)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `step 1 validation blocks NextStep when required fields are empty`() = runTest(testDispatcher) {
        val invalidCv = Cv.empty().copy(personalInfo = PersonalInfo(fullName = "", email = "", professionalTitle = ""))
        coEvery { getCvUseCase("invalid-1") } returns invalidCv

        val viewModel = CvEditorViewModel(
            getCvUseCase = getCvUseCase,
            updateCvUseCase = updateCvUseCase,
            generateSummaryUseCase = generateSummaryUseCase,
            improveExperienceUseCase = improveExperienceUseCase,
            improveProjectUseCase = improveProjectUseCase,
            generatePdfUseCase = generatePdfUseCase,
            settingsRepository = settingsRepository
        )

        viewModel.onIntent(CvEditorIntent.LoadCv("invalid-1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiEffect.test {
            viewModel.onIntent(CvEditorIntent.NextStep)
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is CvEditorEffect.ShowMessage)
            assertEquals(1, viewModel.uiState.value.currentStep)
            assertTrue(viewModel.uiState.value.validationErrors.isNotEmpty())
        }
    }

    @Test
    fun `step 1 allows NextStep when required fields are valid`() = runTest(testDispatcher) {
        val validCv = Cv.sample()
        coEvery { getCvUseCase("valid-1") } returns validCv

        val viewModel = CvEditorViewModel(
            getCvUseCase = getCvUseCase,
            updateCvUseCase = updateCvUseCase,
            generateSummaryUseCase = generateSummaryUseCase,
            improveExperienceUseCase = improveExperienceUseCase,
            improveProjectUseCase = improveProjectUseCase,
            generatePdfUseCase = generatePdfUseCase,
            settingsRepository = settingsRepository
        )

        viewModel.onIntent(CvEditorIntent.LoadCv("valid-1"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(CvEditorIntent.NextStep)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.currentStep)
    }
}
