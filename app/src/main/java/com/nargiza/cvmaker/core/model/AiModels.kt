package com.nargiza.cvmaker.core.model

import kotlinx.serialization.Serializable

@Serializable
data class AiSummaryRequest(
    val jobTitle: String,
    val yearsOfExperience: String,
    val industry: String = "",
    val skills: List<String> = emptyList(),
    val careerGoals: String = "",
    val existingSummary: String = ""
)

@Serializable
data class AiSummaryResponse(
    val summary: String,
    val highlights: List<String> = emptyList()
)

@Serializable
data class AiExperienceRequest(
    val position: String,
    val company: String,
    val rawDescription: String,
    val achievements: List<String> = emptyList()
)

@Serializable
data class AiExperienceResponse(
    val improvedDescription: String,
    val bulletPoints: List<String> = emptyList()
)

@Serializable
data class AiAchievementRequest(
    val roleOrContext: String,
    val rawResponsibilities: String
)

@Serializable
data class AiAchievementResponse(
    val achievements: List<String>
)

@Serializable
data class AiProjectRequest(
    val projectName: String,
    val description: String,
    val technologies: List<String>,
    val role: String = "",
    val rawAchievements: List<String> = emptyList()
)

@Serializable
data class AiProjectResponse(
    val improvedDescription: String,
    val keyBulletPoints: List<String> = emptyList()
)

@Serializable
data class AiCvRequest(
    val jobTitle: String,
    val yearsOfExperience: String,
    val industry: String = "",
    val skills: List<String> = emptyList(),
    val rawExperience: String = "",
    val education: String = "",
    val projects: String = "",
    val targetJobDescription: String = ""
)

@Serializable
data class AiExperienceItem(
    val company: String,
    val position: String,
    val description: String,
    val achievements: List<String> = emptyList()
)

@Serializable
data class AiProjectItem(
    val name: String,
    val description: String,
    val technologies: List<String> = emptyList(),
    val achievements: List<String> = emptyList()
)

@Serializable
data class AiCvResponse(
    val professionalSummary: String,
    val experiences: List<AiExperienceItem> = emptyList(),
    val skills: List<String> = emptyList(),
    val projects: List<AiProjectItem> = emptyList()
)

@Serializable
data class AiJobAnalysisRequest(
    val jobDescription: String,
    val cvSummary: String = "",
    val cvSkills: List<String> = emptyList(),
    val cvExperiences: List<String> = emptyList()
)

@Serializable
data class AiJobAnalysisResponse(
    val matchEstimatePercentage: Int = 0,
    val keyRequirements: List<String> = emptyList(),
    val detectedKeywords: List<String> = emptyList(),
    val missingKeywordsGaps: List<String> = emptyList(),
    val suggestedImprovements: List<String> = emptyList()
)
