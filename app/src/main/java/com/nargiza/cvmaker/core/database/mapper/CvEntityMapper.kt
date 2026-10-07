package com.nargiza.cvmaker.core.database.mapper

import com.nargiza.cvmaker.core.database.entity.CvEntity
import com.nargiza.cvmaker.core.model.Achievement
import com.nargiza.cvmaker.core.model.Certification
import com.nargiza.cvmaker.core.model.Cv
import com.nargiza.cvmaker.core.model.Education
import com.nargiza.cvmaker.core.model.Experience
import com.nargiza.cvmaker.core.model.Language
import com.nargiza.cvmaker.core.model.PersonalInfo
import com.nargiza.cvmaker.core.model.Project
import com.nargiza.cvmaker.core.model.Skill
import com.nargiza.cvmaker.core.model.SocialLinks
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object CvEntityMapper {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    fun toEntity(cv: Cv): CvEntity {
        return CvEntity(
            id = cv.id,
            title = cv.title,
            fullName = cv.personalInfo.fullName,
            professionalTitle = cv.personalInfo.professionalTitle,
            email = cv.personalInfo.email,
            phone = cv.personalInfo.phone,
            location = cv.personalInfo.location,
            website = cv.personalInfo.website,
            linkedIn = cv.personalInfo.linkedIn,
            github = cv.personalInfo.github,
            professionalSummary = cv.professionalSummary,
            experiencesJson = json.encodeToString(cv.experiences),
            educationJson = json.encodeToString(cv.education),
            skillsJson = json.encodeToString(cv.skills),
            projectsJson = json.encodeToString(cv.projects),
            certificationsJson = json.encodeToString(cv.certifications),
            languagesJson = json.encodeToString(cv.languages),
            achievementsJson = json.encodeToString(cv.achievements),
            linksJson = json.encodeToString(cv.links),
            templateId = cv.templateId,
            createdAt = cv.createdAt,
            updatedAt = cv.updatedAt
        )
    }

    fun toDomain(entity: CvEntity): Cv {
        return Cv(
            id = entity.id,
            title = entity.title,
            personalInfo = PersonalInfo(
                fullName = entity.fullName,
                professionalTitle = entity.professionalTitle,
                email = entity.email,
                phone = entity.phone,
                location = entity.location,
                website = entity.website,
                linkedIn = entity.linkedIn,
                github = entity.github
            ),
            professionalSummary = entity.professionalSummary,
            experiences = runCatching {
                json.decodeFromString<List<Experience>>(entity.experiencesJson)
            }.getOrDefault(emptyList()),
            education = runCatching {
                json.decodeFromString<List<Education>>(entity.educationJson)
            }.getOrDefault(emptyList()),
            skills = runCatching {
                json.decodeFromString<List<Skill>>(entity.skillsJson)
            }.getOrDefault(emptyList()),
            projects = runCatching {
                json.decodeFromString<List<Project>>(entity.projectsJson)
            }.getOrDefault(emptyList()),
            certifications = runCatching {
                json.decodeFromString<List<Certification>>(entity.certificationsJson)
            }.getOrDefault(emptyList()),
            languages = runCatching {
                json.decodeFromString<List<Language>>(entity.languagesJson)
            }.getOrDefault(emptyList()),
            achievements = runCatching {
                json.decodeFromString<List<Achievement>>(entity.achievementsJson)
            }.getOrDefault(emptyList()),
            links = runCatching {
                json.decodeFromString<SocialLinks>(entity.linksJson)
            }.getOrDefault(SocialLinks()),
            templateId = entity.templateId,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}
