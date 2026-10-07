package com.nargiza.cvmaker.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "cvs")
data class CvEntity(
    @PrimaryKey val id: String,
    val title: String,
    val fullName: String,
    val professionalTitle: String,
    val email: String,
    val phone: String,
    val location: String,
    val website: String?,
    val linkedIn: String?,
    val github: String?,
    val professionalSummary: String,
    val experiencesJson: String,
    val educationJson: String,
    val skillsJson: String,
    val projectsJson: String,
    val certificationsJson: String,
    val languagesJson: String,
    val achievementsJson: String,
    val linksJson: String,
    val templateId: String,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "experiences")
data class ExperienceEntity(
    @PrimaryKey val id: String,
    val cvId: String,
    val company: String,
    val position: String,
    val location: String,
    val startDate: String,
    val endDate: String?,
    val isCurrent: Boolean,
    val description: String,
    val achievementsJson: String
)

@Entity(tableName = "educations")
data class EducationEntity(
    @PrimaryKey val id: String,
    val cvId: String,
    val institution: String,
    val degree: String,
    val fieldOfStudy: String,
    val startDate: String,
    val endDate: String?
)

@Entity(tableName = "skills")
data class SkillEntity(
    @PrimaryKey val id: String,
    val cvId: String,
    val name: String,
    val category: String,
    val level: String
)

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val cvId: String,
    val name: String,
    val description: String,
    val technologiesJson: String,
    val url: String?,
    val role: String?,
    val achievementsJson: String
)

@Entity(tableName = "certifications")
data class CertificationEntity(
    @PrimaryKey val id: String,
    val cvId: String,
    val name: String,
    val issuer: String,
    val issueDate: String,
    val expirationDate: String?,
    val credentialUrl: String?
)

@Entity(tableName = "languages")
data class LanguageEntity(
    @PrimaryKey val id: String,
    val cvId: String,
    val name: String,
    val proficiency: String
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val cvId: String,
    val title: String,
    val description: String
)
