package com.nargiza.cvmaker.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ---------- Groq API Payload Models ----------
@Serializable
data class GroqMessage(
    val role: String,
    val content: String
)

@Serializable
data class GroqChatRequest(
    val model: String,
    val messages: List<GroqMessage>,
    val temperature: Double = 0.3,
    @SerialName("response_format")
    val responseFormat: GroqResponseFormat? = GroqResponseFormat(type = "json_object")
)

@Serializable
data class GroqResponseFormat(
    val type: String
)

@Serializable
data class GroqChatChoice(
    val index: Int,
    val message: GroqMessage,
    @SerialName("finish_reason")
    val finishReason: String? = null
)

@Serializable
data class GroqChatResponse(
    val id: String = "",
    val choices: List<GroqChatChoice> = emptyList()
)

@Serializable
data class HealthResponseDto(
    val status: String,
    val groqConfigured: Boolean,
    val model: String
)

// ---------- Client Request/Response DTOs ----------
@Serializable
data class SummaryRequestDto(
    val jobTitle: String,
    val yearsOfExperience: String = "",
    val industry: String = "",
    val skills: List<String> = emptyList(),
    val careerGoals: String = "",
    val existingSummary: String = ""
)

@Serializable
data class SummaryResponseDto(
    val summary: String,
    val highlights: List<String> = emptyList()
)

@Serializable
data class ExperienceRequestDto(
    val position: String,
    val company: String,
    val rawDescription: String,
    val achievements: List<String> = emptyList()
)

@Serializable
data class ExperienceResponseDto(
    val improvedDescription: String,
    val bulletPoints: List<String> = emptyList()
)

@Serializable
data class AchievementRequestDto(
    val roleOrContext: String,
    val rawResponsibilities: String
)

@Serializable
data class AchievementResponseDto(
    val achievements: List<String>
)

@Serializable
data class ProjectRequestDto(
    val projectName: String,
    val description: String,
    val technologies: List<String>,
    val role: String = "",
    val rawAchievements: List<String> = emptyList()
)

@Serializable
data class ProjectResponseDto(
    val improvedDescription: String,
    val keyBulletPoints: List<String> = emptyList()
)

@Serializable
data class FullCvRequestDto(
    val jobTitle: String,
    val yearsOfExperience: String = "",
    val industry: String = "",
    val skills: List<String> = emptyList(),
    val rawExperience: String = "",
    val education: String = "",
    val projects: String = "",
    val targetJobDescription: String = ""
)

@Serializable
data class FullCvExperienceDto(
    val company: String,
    val position: String,
    val description: String,
    val achievements: List<String> = emptyList()
)

@Serializable
data class FullCvProjectDto(
    val name: String,
    val description: String,
    val technologies: List<String> = emptyList(),
    val achievements: List<String> = emptyList()
)

@Serializable
data class FullCvResponseDto(
    val professionalSummary: String,
    val experiences: List<FullCvExperienceDto> = emptyList(),
    val skills: List<String> = emptyList(),
    val projects: List<FullCvProjectDto> = emptyList()
)

@Serializable
data class JobAnalysisRequestDto(
    val jobDescription: String,
    val cvSummary: String = "",
    val cvSkills: List<String> = emptyList(),
    val cvExperiences: List<String> = emptyList()
)

@Serializable
data class JobAnalysisResponseDto(
    val matchEstimatePercentage: Int = 0,
    val keyRequirements: List<String> = emptyList(),
    val detectedKeywords: List<String> = emptyList(),
    val missingKeywordsGaps: List<String> = emptyList(),
    val suggestedImprovements: List<String> = emptyList()
)
