package com.nargiza.cvmaker.core.common

import com.nargiza.cvmaker.core.model.AiAchievementRequest
import com.nargiza.cvmaker.core.model.AiCvRequest
import com.nargiza.cvmaker.core.model.AiExperienceRequest
import com.nargiza.cvmaker.core.model.AiJobAnalysisRequest
import com.nargiza.cvmaker.core.model.AiProjectRequest
import com.nargiza.cvmaker.core.model.AiSummaryRequest

object AiPromptBuilder {

    private const val SYSTEM_CONSTRAINTS = """
You are a senior executive resume strategist and ATS optimization expert.
CRITICAL INTEGRITY & ACCURACY RULES:
1. Never invent employment history, education, certifications, companies, or technologies.
2. Never invent numerical metrics or percentages unless explicitly provided in the user's input.
3. Never claim an achievement that was not supplied.
4. Improve wording, impact, and clarity while strictly preserving factual accuracy.
5. Use professional, modern language with active verbs.
6. Prefer concise bullet points and eliminate fluff or generic buzzwords.
7. Return clean JSON strictly matching the requested format.
"""

    fun buildSummaryPrompt(request: AiSummaryRequest): String {
        return """
$SYSTEM_CONSTRAINTS

Task: Generate a compelling, ATS-friendly professional summary (3-5 sentences).
Job Title: ${request.jobTitle}
Years of Experience: ${request.yearsOfExperience}
Industry: ${request.industry}
Key Skills: ${request.skills.joinToString(", ")}
Career Goals: ${request.careerGoals}
Existing Summary (if any): ${request.existingSummary}

Format your response as valid JSON:
{
  "summary": "...",
  "highlights": ["highlight 1", "highlight 2"]
}
""".trimIndent()
    }

    fun buildExperiencePrompt(request: AiExperienceRequest): String {
        return """
$SYSTEM_CONSTRAINTS

Task: Improve the work experience description and generate strong action-driven bullet points without inventing unstated facts or fake metrics.
Position: ${request.position}
Company: ${request.company}
Raw Description: ${request.rawDescription}
Existing Achievements: ${request.achievements.joinToString("\n- ")}

Format your response as valid JSON:
{
  "improvedDescription": "...",
  "bulletPoints": ["action verb bullet 1", "action verb bullet 2"]
}
""".trimIndent()
    }

    fun buildAchievementPrompt(request: AiAchievementRequest): String {
        return """
$SYSTEM_CONSTRAINTS

Task: Convert the user's responsibilities into concise, achievement-oriented bullets.
Role Context: ${request.roleOrContext}
Raw Responsibilities: ${request.rawResponsibilities}

Rules:
- Start with strong action verbs.
- Be concise.
- Do not invent metrics or technologies.

Format your response as valid JSON:
{
  "achievements": ["achievement bullet 1", "achievement bullet 2"]
}
""".trimIndent()
    }

    fun buildProjectPrompt(request: AiProjectRequest): String {
        return """
$SYSTEM_CONSTRAINTS

Task: Polish this project description for a professional CV.
Project Name: ${request.projectName}
Existing Description: ${request.description}
Technologies: ${request.technologies.joinToString(", ")}
Role: ${request.role}
Raw Achievements: ${request.rawAchievements.joinToString("\n- ")}

Format your response as valid JSON:
{
  "improvedDescription": "...",
  "keyBulletPoints": ["bullet 1", "bullet 2"]
}
""".trimIndent()
    }

    fun buildCvPrompt(request: AiCvRequest): String {
        return """
$SYSTEM_CONSTRAINTS

Task: Generate a structured CV draft based on the user's background.
Target Job Title: ${request.jobTitle}
Years of Experience: ${request.yearsOfExperience}
Industry: ${request.industry}
Key Skills: ${request.skills.joinToString(", ")}
Experience Details: ${request.rawExperience}
Education: ${request.education}
Projects: ${request.projects}
Target Job Description (Optional): ${request.targetJobDescription}

Format your response strictly as valid JSON:
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
    }

    fun buildJobOptimizationPrompt(request: AiJobAnalysisRequest): String {
        return """
$SYSTEM_CONSTRAINTS

Task: Analyze the given Job Description against the user's CV summary, skills, and experiences.
Job Description:
${request.jobDescription}

User's CV Summary:
${request.cvSummary}

User's CV Skills:
${request.cvSkills.joinToString(", ")}

User's CV Experience Snippets:
${request.cvExperiences.joinToString("\n---\n")}

Provide:
1. matchEstimatePercentage: An integer estimate (0-100) representing alignment. Note: This is an AI-generated match estimate, not a proprietary ATS score.
2. keyRequirements: Key skills and criteria from the job posting.
3. detectedKeywords: Skills/keywords from the job posting that are present in the CV.
4. missingKeywordsGaps: Required/preferred skills missing from the CV.
5. suggestedImprovements: Specific, actionable recommendations to tailor the CV for this role without fabricating experience.

Format your response strictly as valid JSON:
{
  "matchEstimatePercentage": 82,
  "keyRequirements": ["...", "..."],
  "detectedKeywords": ["...", "..."],
  "missingKeywordsGaps": ["...", "..."],
  "suggestedImprovements": ["...", "..."]
}
""".trimIndent()
    }
}
