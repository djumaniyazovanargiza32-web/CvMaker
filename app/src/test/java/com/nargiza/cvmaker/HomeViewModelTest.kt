package com.nargiza.cvmaker

import app.cash.turbine.test
import com.nargiza.cvmaker.core.model.Cv
import com.nargiza.cvmaker.core.model.PersonalInfo
import com.nargiza.cvmaker.domain.usecase.CreateCvUseCase
import com.nargiza.cvmaker.domain.usecase.DeleteCvUseCase
import com.nargiza.cvmaker.domain.usecase.DuplicateCvUseCase
import com.nargiza.cvmaker.domain.usecase.GeneratePdfUseCase
import com.nargiza.cvmaker.domain.usecase.ObserveCvsUseCase
import com.nargiza.cvmaker.domain.usecase.UpdateCvUseCase
import com.nargiza.cvmaker.feature.home.HomeEffect
import com.nargiza.cvmaker.feature.home.HomeIntent
import com.nargiza.cvmaker.feature.home.HomeViewModel
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val observeCvsUseCase: ObserveCvsUseCase = mockk()
    private val createCvUseCase: CreateCvUseCase = mockk()
    private val duplicateCvUseCase: DuplicateCvUseCase = mockk(relaxed = true)
    private val deleteCvUseCase: DeleteCvUseCase = mockk(relaxed = true)
    private val updateCvUseCase: UpdateCvUseCase = mockk(relaxed = true)
    private val generatePdfUseCase: GeneratePdfUseCase = mockk(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial load displays cvs in state`() = runTest(testDispatcher) {
        val cvs = listOf(Cv.sample())
        every { observeCvsUseCase() } returns flowOf(cvs)

        val viewModel = HomeViewModel(
            observeCvsUseCase = observeCvsUseCase,
            createCvUseCase = createCvUseCase,
            duplicateCvUseCase = duplicateCvUseCase,
            deleteCvUseCase = deleteCvUseCase,
            updateCvUseCase = updateCvUseCase,
            generatePdfUseCase = generatePdfUseCase
        )

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.cvs.size)
        assertEquals("Senior Android Developer CV", viewModel.uiState.value.cvs[0].title)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `search filters cvs by query`() = runTest(testDispatcher) {
        val cv1 = Cv.empty().copy(id = "1", title = "Android Developer")
        val cv2 = Cv.empty().copy(id = "2", title = "iOS Developer", personalInfo = PersonalInfo(professionalTitle = "Swift Specialist"))
        every { observeCvsUseCase() } returns flowOf(listOf(cv1, cv2))

        val viewModel = HomeViewModel(
            observeCvsUseCase = observeCvsUseCase,
            createCvUseCase = createCvUseCase,
            duplicateCvUseCase = duplicateCvUseCase,
            deleteCvUseCase = deleteCvUseCase,
            updateCvUseCase = updateCvUseCase,
            generatePdfUseCase = generatePdfUseCase
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(HomeIntent.UpdateSearchQuery("Android"))

        assertEquals(1, viewModel.uiState.value.filteredCvs.size)
        assertEquals("Android Developer", viewModel.uiState.value.filteredCvs[0].title)
    }

    @Test
    fun `create new cv triggers NavigateToEditor effect`() = runTest(testDispatcher) {
        every { observeCvsUseCase() } returns flowOf(emptyList())
        val newCv = Cv.empty().copy(id = "new-cv-123")
        coEvery { createCvUseCase(any(), any()) } returns newCv

        val viewModel = HomeViewModel(
            observeCvsUseCase = observeCvsUseCase,
            createCvUseCase = createCvUseCase,
            duplicateCvUseCase = duplicateCvUseCase,
            deleteCvUseCase = deleteCvUseCase,
            updateCvUseCase = updateCvUseCase,
            generatePdfUseCase = generatePdfUseCase
        )

        viewModel.uiEffect.test {
            viewModel.onIntent(HomeIntent.CreateNewCv)
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assert(effect is HomeEffect.NavigateToEditor)
            assertEquals("new-cv-123", (effect as HomeEffect.NavigateToEditor).cvId)
        }
    }
}
