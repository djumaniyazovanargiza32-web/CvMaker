package com.nargiza.cvmaker.domain.usecase

import com.nargiza.cvmaker.core.model.AiAchievementRequest
import com.nargiza.cvmaker.core.model.AiAchievementResponse
import com.nargiza.cvmaker.core.model.AiCvRequest
import com.nargiza.cvmaker.core.model.AiCvResponse
import com.nargiza.cvmaker.core.model.AiExperienceRequest
import com.nargiza.cvmaker.core.model.AiExperienceResponse
import com.nargiza.cvmaker.core.model.AiJobAnalysisRequest
import com.nargiza.cvmaker.core.model.AiJobAnalysisResponse
import com.nargiza.cvmaker.core.model.AiProjectRequest
import com.nargiza.cvmaker.core.model.AiProjectResponse
import com.nargiza.cvmaker.core.model.AiSummaryRequest
import com.nargiza.cvmaker.core.model.AiSummaryResponse
import com.nargiza.cvmaker.core.model.Cv
import com.nargiza.cvmaker.domain.repository.AiRepository
import com.nargiza.cvmaker.domain.repository.CvRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class CreateCvUseCase(private val repository: CvRepository) {
    suspend operator fun invoke(title: String = "My Professional CV", templateId: String = "modern"): Cv {
        val newCv = Cv(
            id = UUID.randomUUID().toString(),
            title = title,
            templateId = templateId,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        repository.saveCv(newCv)
        return newCv
    }
}

class GetCvUseCase(private val repository: CvRepository) {
    suspend operator fun invoke(id: String): Cv? = repository.getCv(id)
}

class ObserveCvsUseCase(private val repository: CvRepository) {
    operator fun invoke(): Flow<List<Cv>> = repository.observeCvs()
}

class ObserveCvUseCase(private val repository: CvRepository) {
    operator fun invoke(id: String): Flow<Cv?> = repository.observeCv(id)
}

class SearchCvsUseCase(private val repository: CvRepository) {
    operator fun invoke(query: String): Flow<List<Cv>> = repository.searchCvs(query)
}

class UpdateCvUseCase(private val repository: CvRepository) {
    suspend operator fun invoke(cv: Cv) = repository.saveCv(cv)
}

class DeleteCvUseCase(private val repository: CvRepository) {
    suspend operator fun invoke(id: String) = repository.deleteCv(id)
}

class DuplicateCvUseCase(private val repository: CvRepository) {
    suspend operator fun invoke(id: String) = repository.duplicateCv(id)
}

class GenerateSummaryUseCase(private val aiRepository: AiRepository) {
    suspend operator fun invoke(request: AiSummaryRequest): Result<AiSummaryResponse> =
        aiRepository.generateSummary(request)
}

class ImproveExperienceUseCase(private val aiRepository: AiRepository) {
    suspend operator fun invoke(request: AiExperienceRequest): Result<AiExperienceResponse> =
        aiRepository.improveExperience(request)
}

class GenerateAchievementUseCase(private val aiRepository: AiRepository) {
    suspend operator fun invoke(request: AiAchievementRequest): Result<AiAchievementResponse> =
        aiRepository.generateAchievements(request)
}

class ImproveProjectUseCase(private val aiRepository: AiRepository) {
    suspend operator fun invoke(request: AiProjectRequest): Result<AiProjectResponse> =
        aiRepository.improveProject(request)
}

class GenerateFullCvUseCase(private val aiRepository: AiRepository) {
    suspend operator fun invoke(request: AiCvRequest): Result<AiCvResponse> =
        aiRepository.generateFullCv(request)
}

class AnalyzeJobDescriptionUseCase(private val aiRepository: AiRepository) {
    suspend operator fun invoke(request: AiJobAnalysisRequest): Result<AiJobAnalysisResponse> =
        aiRepository.analyzeJob(request)
}
