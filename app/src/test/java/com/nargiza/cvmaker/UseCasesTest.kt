package com.nargiza.cvmaker

import app.cash.turbine.test
import com.nargiza.cvmaker.core.model.Cv
import com.nargiza.cvmaker.domain.repository.CvRepository
import com.nargiza.cvmaker.domain.usecase.CreateCvUseCase
import com.nargiza.cvmaker.domain.usecase.DeleteCvUseCase
import com.nargiza.cvmaker.domain.usecase.DuplicateCvUseCase
import com.nargiza.cvmaker.domain.usecase.ObserveCvsUseCase
import com.nargiza.cvmaker.domain.usecase.UpdateCvUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class UseCasesTest {

    private val cvRepository: CvRepository = mockk(relaxed = true)

    @Test
    fun `createCvUseCase saves new Cv and returns it`() = runTest {
        val useCase = CreateCvUseCase(cvRepository)
        val created = useCase(title = "My Resume", templateId = "classic")

        assertEquals("My Resume", created.title)
        assertEquals("classic", created.templateId)
        assertNotNull(created.id)
        coVerify(exactly = 1) { cvRepository.saveCv(any()) }
    }

    @Test
    fun `observeCvsUseCase emits list of cvs via Flow`() = runTest {
        val sampleList = listOf(Cv.sample())
        every { cvRepository.observeCvs() } returns flowOf(sampleList)

        val useCase = ObserveCvsUseCase(cvRepository)
        useCase().test {
            val item = awaitItem()
            assertEquals(1, item.size)
            assertEquals("Alex Johnson", item[0].personalInfo.fullName)
            awaitComplete()
        }
    }

    @Test
    fun `deleteCvUseCase invokes repository deleteCv`() = runTest {
        val useCase = DeleteCvUseCase(cvRepository)
        useCase("test-id-123")
        coVerify(exactly = 1) { cvRepository.deleteCv("test-id-123") }
    }

    @Test
    fun `duplicateCvUseCase invokes repository duplicateCv`() = runTest {
        val useCase = DuplicateCvUseCase(cvRepository)
        useCase("test-id-123")
        coVerify(exactly = 1) { cvRepository.duplicateCv("test-id-123") }
    }
}
