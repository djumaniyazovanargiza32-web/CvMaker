package com.nargiza.cvmaker.backend.routes

import com.nargiza.cvmaker.backend.ai.GroqClient
import com.nargiza.cvmaker.backend.model.AchievementRequestDto
import com.nargiza.cvmaker.backend.model.AchievementResponseDto
import com.nargiza.cvmaker.backend.model.ExperienceRequestDto
import com.nargiza.cvmaker.backend.model.ExperienceResponseDto
import com.nargiza.cvmaker.backend.model.FullCvRequestDto
import com.nargiza.cvmaker.backend.model.FullCvResponseDto
import com.nargiza.cvmaker.backend.model.JobAnalysisRequestDto
import com.nargiza.cvmaker.backend.model.JobAnalysisResponseDto
import com.nargiza.cvmaker.backend.model.ProjectRequestDto
import com.nargiza.cvmaker.backend.model.ProjectResponseDto
import com.nargiza.cvmaker.backend.model.SummaryRequestDto
import com.nargiza.cvmaker.backend.model.SummaryResponseDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.slf4j.LoggerFactory

private const val SYSTEM_PROMPT = """
You are an expert executive resume strategist and ATS optimization engine.
CRITICAL RULES:
1. Never invent employment history, companies, education, certifications, or technologies.
2. Never invent numerical metrics or percentage statistics.
3. Improve phrasing, impact, and clarity while strictly preserving factual accuracy.
4. Always respond with pure, valid JSON matching the requested schema.
"""

fun Route.aiRoutes(groqClient: GroqClient) {
    val logger = LoggerFactory.getLogger("AiRoutes")

    route("/api/v1/ai") {

        // 1. Summary Generator
        post("/summary") {
            val req = runCatching { call.receive<SummaryRequestDto>() }
                .onFailure { logger.warn("Receive failed: ${it.message}", it) }
                .getOrNull()
            if (req == null || req.jobTitle.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Job title is required"))
                return@post
            }

            val userPrompt = """
Task: Write a high-impact, 3-5 sentence ATS-optimized professional summary.
Job Title: ${req.jobTitle}
Years of Experience: ${req.yearsOfExperience}
Industry: ${req.industry}
Skills: ${req.skills.joinToString(", ")}
Career Goals: ${req.careerGoals}
Existing Summary: ${req.existingSummary}

Format strictly as JSON:
{
  "summary": "...",
  "highlights": ["highlight 1", "highlight 2"]
}
""".trimIndent()

            try {
                val json = groqClient.completeJson(SYSTEM_PROMPT, userPrompt)
                val response = groqClient.jsonParser.decodeFromString<SummaryResponseDto>(json)
                call.respond(HttpStatusCode.OK, response)
            } catch (e: Exception) {
                logger.error("Summary generation failed", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to (e.message ?: "Generation failed")))
            }
        }

        // 2. Experience Improvement
        post("/experience") {
            val req = runCatching { call.receive<ExperienceRequestDto>() }.getOrNull()
            if (req == null || req.position.isBlank() || req.company.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Position and company are required"))
                return@post
            }

            val userPrompt = """
Task: Polish the experience description and generate action-oriented bullet points without inventing unstated facts or metrics.
Position: ${req.position}
Company: ${req.company}
Raw Description: ${req.rawDescription}
Achievements: ${req.achievements.joinToString("\n- ")}

Format strictly as JSON:
{
  "improvedDescription": "...",
  "bulletPoints": ["action verb bullet 1", "action verb bullet 2"]
}
""".trimIndent()

            try {
                val json = groqClient.completeJson(SYSTEM_PROMPT, userPrompt)
                val response = groqClient.jsonParser.decodeFromString<ExperienceResponseDto>(json)
                call.respond(HttpStatusCode.OK, response)
            } catch (e: Exception) {
                logger.error("Experience improvement failed", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to (e.message ?: "Improvement failed")))
            }
        }

        // 3. Achievement Generator
        post("/achievements") {
            val req = runCatching { call.receive<AchievementRequestDto>() }.getOrNull()
            if (req == null || req.rawResponsibilities.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Responsibilities are required"))
                return@post
            }

            val userPrompt = """
Task: Transform the responsibilities into concise, achievement-oriented bullets.
Context: ${req.roleOrContext}
Responsibilities: ${req.rawResponsibilities}

Format strictly as JSON:
{
  "achievements": ["bullet 1", "bullet 2"]
}
""".trimIndent()

            try {
                val json = groqClient.completeJson(SYSTEM_PROMPT, userPrompt)
                val response = groqClient.jsonParser.decodeFromString<AchievementResponseDto>(json)
                call.respond(HttpStatusCode.OK, response)
            } catch (e: Exception) {
                logger.error("Achievements generation failed", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to (e.message ?: "Generation failed")))
            }
        }

        // 4. Project Improvement
        post("/project") {
            val req = runCatching { call.receive<ProjectRequestDto>() }.getOrNull()
            if (req == null || req.projectName.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Project name is required"))
                return@post
            }

            val userPrompt = """
Task: Polish the project description for a professional CV.
Project: ${req.projectName}
Existing Description: ${req.description}
Technologies: ${req.technologies.joinToString(", ")}
Role: ${req.role}
Raw Achievements: ${req.rawAchievements.joinToString("\n- ")}

Format strictly as JSON:
{
  "improvedDescription": "...",
  "keyBulletPoints": ["bullet 1", "bullet 2"]
}
""".trimIndent()

            try {
                val json = groqClient.completeJson(SYSTEM_PROMPT, userPrompt)
                val response = groqClient.jsonParser.decodeFromString<ProjectResponseDto>(json)
                call.respond(HttpStatusCode.OK, response)
            } catch (e: Exception) {
                logger.error("Project improvement failed", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to (e.message ?: "Improvement failed")))
            }
        }

        // 5. Full CV Generation
        post("/cv") {
            val req = runCatching { call.receive<FullCvRequestDto>() }.getOrNull()
            if (req == null || req.jobTitle.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Target job title is required"))
                return@post
            }

            val userPrompt = """
Task: Draft a complete structured CV tailored for '${req.jobTitle}'.
Years of Experience: ${req.yearsOfExperience}
Industry: ${req.industry}
Key Skills: ${req.skills.joinToString(", ")}
Experience: ${req.rawExperience}
Education: ${req.education}
Projects: ${req.projects}
Target Job Description: ${req.targetJobDescription}

Format strictly as JSON:
{
  "professionalSummary": "...",
  "experiences": [
    {
      "company": "...",
      "position": "...",
      "description": "...",
      "achievements": ["..."]
    }
  ],
  "skills": ["..."],
  "projects": [
    {
      "name": "...",
      "description": "...",
      "technologies": ["..."],
      "achievements": ["..."]
    }
  ]
}
""".trimIndent()

            try {
                val json = groqClient.completeJson(SYSTEM_PROMPT, userPrompt)
                val response = groqClient.jsonParser.decodeFromString<FullCvResponseDto>(json)
                call.respond(HttpStatusCode.OK, response)
            } catch (e: Exception) {
                logger.error("Full CV generation failed", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to (e.message ?: "CV generation failed")))
            }
        }

        // 6. Job Description Optimizer
        post("/job-analysis") {
            val req = runCatching { call.receive<JobAnalysisRequestDto>() }.getOrNull()
            if (req == null || req.jobDescription.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Job description is required"))
                return@post
            }

            val userPrompt = """
Task: Analyze this Job Description against the user's CV data.
Job Description:
${req.jobDescription}

CV Summary:
${req.cvSummary}

CV Skills:
${req.cvSkills.joinToString(", ")}

CV Experiences:
${req.cvExperiences.joinToString("\n---\n")}

Format strictly as JSON:
{
  "matchEstimatePercentage": 85,
  "keyRequirements": ["...", "..."],
  "detectedKeywords": ["...", "..."],
  "missingKeywordsGaps": ["...", "..."],
  "suggestedImprovements": ["...", "..."]
}
""".trimIndent()

            try {
                val json = groqClient.completeJson(SYSTEM_PROMPT, userPrompt)
                val response = groqClient.jsonParser.decodeFromString<JobAnalysisResponseDto>(json)
                call.respond(HttpStatusCode.OK, response)
            } catch (e: Exception) {
                logger.error("Job analysis failed", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to (e.message ?: "Job analysis failed")))
            }
        }
    }
}
