package com.nargiza.cvmaker.data.repository

import com.nargiza.cvmaker.core.common.DispatchersProvider
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
import com.nargiza.cvmaker.core.network.KtorAiClient
import com.nargiza.cvmaker.domain.repository.AiRepository
import kotlinx.coroutines.withContext

class AiRepositoryImpl(
    private val client: KtorAiClient,
    private val dispatchers: DispatchersProvider
) : AiRepository {

    override suspend fun generateSummary(request: AiSummaryRequest): Result<AiSummaryResponse> =
        withContext(dispatchers.io) {
            client.generateSummary(request)
        }

    override suspend fun improveExperience(request: AiExperienceRequest): Result<AiExperienceResponse> =
        withContext(dispatchers.io) {
            client.improveExperience(request)
        }

    override suspend fun generateAchievements(request: AiAchievementRequest): Result<AiAchievementResponse> =
        withContext(dispatchers.io) {
            client.generateAchievements(request)
        }

    override suspend fun improveProject(request: AiProjectRequest): Result<AiProjectResponse> =
        withContext(dispatchers.io) {
            client.improveProject(request)
        }

    override suspend fun generateFullCv(request: AiCvRequest): Result<AiCvResponse> =
        withContext(dispatchers.io) {
            client.generateFullCv(request)
        }

    override suspend fun analyzeJob(request: AiJobAnalysisRequest): Result<AiJobAnalysisResponse> =
        withContext(dispatchers.io) {
            client.analyzeJob(request)
        }
}
