package com.nargiza.cvmaker

import com.nargiza.cvmaker.core.common.AiPromptBuilder
import com.nargiza.cvmaker.core.model.AiAchievementRequest
import com.nargiza.cvmaker.core.model.AiExperienceRequest
import com.nargiza.cvmaker.core.model.AiSummaryRequest
import org.junit.Assert.assertTrue
import org.junit.Test

class AiPromptBuilderTest {

    @Test
    fun `summary prompt includes constraints and job title`() {
        val request = AiSummaryRequest(
            jobTitle = "Android Architect",
            yearsOfExperience = "7+",
            skills = listOf("Kotlin", "Jetpack Compose", "Coroutines")
        )
        val prompt = AiPromptBuilder.buildSummaryPrompt(request)
        assertTrue(prompt.contains("Android Architect"))
        assertTrue(prompt.contains("Jetpack Compose"))
        assertTrue(prompt.contains("Never invent"))
    }

    @Test
    fun `experience prompt preserves company and position`() {
        val request = AiExperienceRequest(
            position = "Tech Lead",
            company = "Acme Corp",
            rawDescription = "Maintained core mobile services."
        )
        val prompt = AiPromptBuilder.buildExperiencePrompt(request)
        assertTrue(prompt.contains("Tech Lead"))
        assertTrue(prompt.contains("Acme Corp"))
        assertTrue(prompt.contains("Maintained core mobile services."))
    }

    @Test
    fun `achievement prompt includes role and responsibilities`() {
        val request = AiAchievementRequest(
            roleOrContext = "Staff Engineer",
            rawResponsibilities = "Refactored checkout flow and handled crashes"
        )
        val prompt = AiPromptBuilder.buildAchievementPrompt(request)
        assertTrue(prompt.contains("Staff Engineer"))
        assertTrue(prompt.contains("Refactored checkout flow"))
    }
}
