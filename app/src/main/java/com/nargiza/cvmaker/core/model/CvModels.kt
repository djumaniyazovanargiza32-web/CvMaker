package com.nargiza.cvmaker.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class SkillLevel(val displayName: String, val weight: Float) {
    BEGINNER("Beginner", 0.25f),
    INTERMEDIATE("Intermediate", 0.50f),
    ADVANCED("Advanced", 0.75f),
    EXPERT("Expert", 1.0f)
}

@Serializable
enum class TemplateType(val id: String, val title: String, val description: String) {
    CLASSIC("classic", "Classic", "Timeless, traditional serif style trusted by academia and corporate firms."),
    MODERN("modern", "Modern", "Clean, contemporary design with subtle accent colors for tech & creative roles."),
    MINIMAL("minimal", "Minimal", "High readability, clean lines, and distraction-free white space."),
    PROFESSIONAL("professional", "Professional", "Polished ATS-optimized layout ideal for corporate & enterprise positions."),
    EXECUTIVE("executive", "Executive", "Distinguished layout with prominent header and career progression emphasis.");

    companion object {
        fun fromId(id: String): TemplateType = entries.find { it.id.equals(id, ignoreCase = true) } ?: MODERN
    }
}

@Serializable
data class PersonalInfo(
    val fullName: String = "",
    val professionalTitle: String = "",
    val email: String = "",
    val phone: String = "",
    val location: String = "",
    val website: String? = null,
    val linkedIn: String? = null,
    val github: String? = null
)

@Serializable
data class Experience(
    val id: String = "",
    val company: String = "",
    val position: String = "",
    val location: String = "",
    val startDate: String = "",
    val endDate: String? = null,
    val isCurrent: Boolean = false,
    val description: String = "",
    val achievements: List<String> = emptyList()
)

@Serializable
data class Education(
    val id: String = "",
    val institution: String = "",
    val degree: String = "",
    val fieldOfStudy: String = "",
    val startDate: String = "",
    val endDate: String? = null
)

@Serializable
data class Skill(
    val id: String = "",
    val name: String = "",
    val category: String = "Other",
    val level: SkillLevel = SkillLevel.INTERMEDIATE
)

@Serializable
data class Project(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val technologies: List<String> = emptyList(),
    val url: String? = null,
    val role: String? = null,
    val achievements: List<String> = emptyList()
)

@Serializable
data class Certification(
    val id: String = "",
    val name: String = "",
    val issuer: String = "",
    val issueDate: String = "",
    val expirationDate: String? = null,
    val credentialUrl: String? = null
)

@Serializable
data class Language(
    val id: String = "",
    val name: String = "",
    val proficiency: String = "Fluent"
)

@Serializable
data class Achievement(
    val id: String = "",
    val title: String = "",
    val description: String = ""
)

@Serializable
data class SocialLinks(
    val linkedIn: String? = null,
    val github: String? = null,
    val portfolio: String? = null,
    val other: List<String> = emptyList()
)

@Serializable
data class Cv(
    val id: String = "",
    val title: String = "",
    val personalInfo: PersonalInfo = PersonalInfo(),
    val professionalSummary: String = "",
    val experiences: List<Experience> = emptyList(),
    val education: List<Education> = emptyList(),
    val skills: List<Skill> = emptyList(),
    val projects: List<Project> = emptyList(),
    val certifications: List<Certification> = emptyList(),
    val languages: List<Language> = emptyList(),
    val achievements: List<Achievement> = emptyList(),
    val links: SocialLinks = SocialLinks(),
    val templateId: String = TemplateType.MODERN.id,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        fun empty(): Cv = Cv(
            id = java.util.UUID.randomUUID().toString(),
            title = "Untitled CV",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        fun sample(): Cv = Cv(
            id = java.util.UUID.randomUUID().toString(),
            title = "Senior Android Developer CV",
            personalInfo = PersonalInfo(
                fullName = "Alex Johnson",
                professionalTitle = "Senior Android Engineer",
                email = "alex.johnson@example.com",
                phone = "+1 (555) 234-5678",
                location = "San Francisco, CA",
                website = "https://alexjohnson.dev",
                linkedIn = "linkedin.com/in/alexjohnson",
                github = "github.com/alexjohnson"
            ),
            professionalSummary = "Senior Android Engineer with 8+ years of experience architecting scalable, high-performance mobile applications using Kotlin, Jetpack Compose, Coroutines, and Clean Architecture. Proven track record leading mobile platform teams and delivering modern Android applications with millions of active users.",
            experiences = listOf(
                Experience(
                    id = "exp-1",
                    company = "TechCorp Global",
                    position = "Lead Android Engineer",
                    location = "San Francisco, CA",
                    startDate = "2021",
                    endDate = null,
                    isCurrent = true,
                    description = "Lead Android platform architecture across 4 feature teams, driving migration to Jetpack Compose and modern MVI.",
                    achievements = listOf(
                        "Reduced application startup time by 38% through baseline profiles and dependency optimization.",
                        "Architected unified Design System in Jetpack Compose, improving developer velocity by 25%.",
                        "Mentored 6 engineers and established standardized CI/CD pipelines with GitHub Actions."
                    )
                ),
                Experience(
                    id = "exp-2",
                    company = "Innovate Labs",
                    position = "Senior Mobile Developer",
                    location = "Austin, TX",
                    startDate = "2018",
                    endDate = "2021",
                    isCurrent = false,
                    description = "Engineered real-time collaboration features and offline-first synchronization using Room and Kotlin Coroutines.",
                    achievements = listOf(
                        "Built robust offline sync engine with conflict resolution, supporting 500k+ daily transactions.",
                        "Optimized background memory footprint by 40% using LeakCanary and strict coroutine scoping."
                    )
                )
            ),
            education = listOf(
                Education(
                    id = "edu-1",
                    institution = "University of Texas at Austin",
                    degree = "Bachelor of Science",
                    fieldOfStudy = "Computer Science",
                    startDate = "2014",
                    endDate = "2018"
                )
            ),
            skills = listOf(
                Skill(id = "s1", name = "Kotlin", category = "Programming", level = SkillLevel.EXPERT),
                Skill(id = "s2", name = "Jetpack Compose", category = "Frameworks", level = SkillLevel.EXPERT),
                Skill(id = "s3", name = "Android SDK", category = "Frameworks", level = SkillLevel.EXPERT),
                Skill(id = "s4", name = "Coroutines & Flow", category = "Programming", level = SkillLevel.EXPERT),
                Skill(id = "s5", name = "Room DB", category = "Databases", level = SkillLevel.ADVANCED),
                Skill(id = "s6", name = "Koin / Hilt", category = "Frameworks", level = SkillLevel.ADVANCED),
                Skill(id = "s7", name = "Ktor Client", category = "Cloud", level = SkillLevel.ADVANCED),
                Skill(id = "s8", name = "Clean Architecture", category = "Soft Skills", level = SkillLevel.EXPERT),
                Skill(id = "s9", name = "CI/CD & Git", category = "DevOps", level = SkillLevel.ADVANCED)
            ),
            projects = listOf(
                Project(
                    id = "proj-1",
                    name = "AI CV Maker",
                    description = "Modern production Android application with on-device PDF generation, clean MVI architecture, and AI-assisted CV enhancement.",
                    technologies = listOf("Kotlin", "Jetpack Compose", "Room", "Koin", "Coroutines"),
                    url = "https://github.com/alexjohnson/cvmaker",
                    role = "Lead Architect & Developer",
                    achievements = listOf(
                        "Designed multi-page A4 canvas pagination with high-precision typography.",
                        "Implemented offline-first MVI architecture with full Room persistence."
                    )
                )
            ),
            certifications = listOf(
                Certification(
                    id = "cert-1",
                    name = "Associate Android Developer",
                    issuer = "Google",
                    issueDate = "2020",
                    expirationDate = null,
                    credentialUrl = "https://developers.google.com/certification"
                )
            ),
            languages = listOf(
                Language(id = "lang-1", name = "English", proficiency = "Native / Fluent"),
                Language(id = "lang-2", name = "German", proficiency = "Conversational")
            ),
            achievements = listOf(
                Achievement(
                    id = "ach-1",
                    title = "Google Developer Expert Candidate",
                    description = "Recognized for community contributions to Android and Kotlin open source."
                )
            ),
            templateId = TemplateType.MODERN.id
        )
    }
}
