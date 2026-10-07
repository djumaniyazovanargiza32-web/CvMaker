package com.nargiza.cvmaker.domain.repository

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

interface AiRepository {
    suspend fun generateSummary(request: AiSummaryRequest): Result<AiSummaryResponse>
    suspend fun improveExperience(request: AiExperienceRequest): Result<AiExperienceResponse>
    suspend fun generateAchievements(request: AiAchievementRequest): Result<AiAchievementResponse>
    suspend fun improveProject(request: AiProjectRequest): Result<AiProjectResponse>
    suspend fun generateFullCv(request: AiCvRequest): Result<AiCvResponse>
    suspend fun analyzeJob(request: AiJobAnalysisRequest): Result<AiJobAnalysisResponse>
}
