package com.nargiza.cvmaker.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nargiza.cvmaker.core.model.Cv
import com.nargiza.cvmaker.core.model.TemplateType

data class TemplateStyle(
    val primaryColor: Color,
    val secondaryColor: Color,
    val textColor: Color,
    val mutedColor: Color,
    val dividerColor: Color,
    val headerBgColor: Color = Color.Transparent,
    val fontFamily: FontFamily = FontFamily.SansSerif
)

fun getTemplateStyle(templateType: TemplateType): TemplateStyle {
    return when (templateType) {
        TemplateType.CLASSIC -> TemplateStyle(
            primaryColor = Color(0xFF181818),
            secondaryColor = Color(0xFF464646),
            textColor = Color(0xFF212121),
            mutedColor = Color(0xFF646464),
            dividerColor = Color(0xFFB4B4B4),
            fontFamily = FontFamily.Serif
        )
        TemplateType.MODERN -> TemplateStyle(
            primaryColor = Color(0xFF0E7490),
            secondaryColor = Color(0xFF0369A1),
            textColor = Color(0xFF1E293B),
            mutedColor = Color(0xFF64748B),
            dividerColor = Color(0xFFCBD5E1),
            fontFamily = FontFamily.SansSerif
        )
        TemplateType.MINIMAL -> TemplateStyle(
            primaryColor = Color(0xFF1E1E1E),
            secondaryColor = Color(0xFF505050),
            textColor = Color(0xFF282828),
            mutedColor = Color(0xFF787878),
            dividerColor = Color(0xFFDCDCDC),
            fontFamily = FontFamily.SansSerif
        )
        TemplateType.PROFESSIONAL -> TemplateStyle(
            primaryColor = Color(0xFF1E3A8A),
            secondaryColor = Color(0xFF2563EB),
            textColor = Color(0xFF111827),
            mutedColor = Color(0xFF4B5563),
            dividerColor = Color(0xFFD1D5DB),
            fontFamily = FontFamily.SansSerif
        )
        TemplateType.EXECUTIVE -> TemplateStyle(
            primaryColor = Color(0xFF881337),
            secondaryColor = Color(0xFF9F1239),
            textColor = Color(0xFF18181B),
            mutedColor = Color(0xFF52525B),
            dividerColor = Color(0xFFD4D4D8),
            headerBgColor = Color(0xFFFFF1F2),
            fontFamily = FontFamily.Serif
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CvDocumentPaper(
    cv: Cv,
    modifier: Modifier = Modifier
) {
    val template = TemplateType.fromId(cv.templateId)
    val style = getTemplateStyle(template)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(4.dp)),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header
            if (style.headerBgColor != Color.Transparent) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(style.headerBgColor, RoundedCornerShape(4.dp))
                        .padding(12.dp)
                ) {
                    HeaderContent(cv = cv, style = style)
                }
            } else {
                HeaderContent(cv = cv, style = style)
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = style.dividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Professional Summary
            if (cv.professionalSummary.isNotBlank()) {
                SectionHeader(title = "Professional Summary", style = style)
                Text(
                    text = cv.professionalSummary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = style.textColor,
                    fontFamily = style.fontFamily
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Experience
            if (cv.experiences.isNotEmpty()) {
                SectionHeader(title = "Work Experience", style = style)
                cv.experiences.forEach { exp ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "${exp.position} — ${exp.company}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = style.textColor,
                            fontFamily = style.fontFamily
                        )
                        val dateText = if (exp.isCurrent) "${exp.startDate} - Present" else "${exp.startDate} - ${exp.endDate.orEmpty()}"
                        Text(
                            text = if (exp.location.isNotBlank()) "$dateText | ${exp.location}" else dateText,
                            fontSize = 11.sp,
                            color = style.mutedColor,
                            fontFamily = style.fontFamily
                        )
                    }
                    if (exp.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = exp.description,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = style.textColor,
                            fontFamily = style.fontFamily
                        )
                    }
                    exp.achievements.forEach { ach ->
                        if (ach.isNotBlank()) {
                            Row(modifier = Modifier.padding(top = 2.dp, start = 4.dp)) {
                                Text(text = "• ", color = style.primaryColor, fontSize = 12.sp)
                                Text(
                                    text = ach,
                                    fontSize = 11.5.sp,
                                    lineHeight = 16.sp,
                                    color = style.textColor,
                                    fontFamily = style.fontFamily
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Education
            if (cv.education.isNotEmpty()) {
                SectionHeader(title = "Education", style = style)
                cv.education.forEach { edu ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "${edu.degree} in ${edu.fieldOfStudy}",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = style.textColor,
                            fontFamily = style.fontFamily
                        )
                        val eduDate = if (edu.endDate.isNullOrBlank()) edu.startDate else "${edu.startDate} - ${edu.endDate}"
                        Text(
                            text = eduDate,
                            fontSize = 11.sp,
                            color = style.mutedColor,
                            fontFamily = style.fontFamily
                        )
                    }
                    if (edu.institution.isNotBlank()) {
                        Text(
                            text = edu.institution,
                            fontSize = 11.5.sp,
                            color = style.mutedColor,
                            fontFamily = style.fontFamily
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Skills
            if (cv.skills.isNotEmpty()) {
                SectionHeader(title = "Skills", style = style)
                val grouped = cv.skills.groupBy { it.category }
                grouped.forEach { (category, skills) ->
                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                        Text(
                            text = "$category: ",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = style.textColor,
                            fontFamily = style.fontFamily
                        )
                        Text(
                            text = skills.joinToString(", ") { "${it.name} (${it.level.displayName})" },
                            fontSize = 12.sp,
                            color = style.textColor,
                            fontFamily = style.fontFamily
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Projects
            if (cv.projects.isNotEmpty()) {
                SectionHeader(title = "Key Projects", style = style)
                cv.projects.forEach { proj ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = proj.name,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = style.textColor,
                            fontFamily = style.fontFamily
                        )
                        if (proj.role?.isNotBlank() == true) {
                            Text(
                                text = proj.role,
                                fontSize = 11.sp,
                                color = style.mutedColor,
                                fontFamily = style.fontFamily
                            )
                        }
                    }
                    if (proj.technologies.isNotEmpty()) {
                        Text(
                            text = "Technologies: " + proj.technologies.joinToString(", "),
                            fontSize = 11.sp,
                            color = style.secondaryColor,
                            fontFamily = style.fontFamily
                        )
                    }
                    if (proj.description.isNotBlank()) {
                        Text(
                            text = proj.description,
                            fontSize = 11.5.sp,
                            color = style.textColor,
                            fontFamily = style.fontFamily
                        )
                    }
                    proj.achievements.forEach { ach ->
                        if (ach.isNotBlank()) {
                            Row(modifier = Modifier.padding(start = 4.dp)) {
                                Text(text = "• ", color = style.primaryColor, fontSize = 11.sp)
                                Text(
                                    text = ach,
                                    fontSize = 11.sp,
                                    color = style.textColor,
                                    fontFamily = style.fontFamily
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Certifications
            if (cv.certifications.isNotEmpty()) {
                SectionHeader(title = "Certifications", style = style)
                cv.certifications.forEach { cert ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${cert.name} — ${cert.issuer}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = style.textColor,
                            fontFamily = style.fontFamily
                        )
                        Text(
                            text = cert.issueDate,
                            fontSize = 11.sp,
                            color = style.mutedColor,
                            fontFamily = style.fontFamily
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Languages
            if (cv.languages.isNotEmpty()) {
                SectionHeader(title = "Languages", style = style)
                Text(
                    text = cv.languages.joinToString("   •   ") { "${it.name} (${it.proficiency})" },
                    fontSize = 12.sp,
                    color = style.textColor,
                    fontFamily = style.fontFamily
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Achievements
            if (cv.achievements.isNotEmpty()) {
                SectionHeader(title = "Honors & Achievements", style = style)
                cv.achievements.forEach { ach ->
                    Text(
                        text = ach.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = style.textColor,
                        fontFamily = style.fontFamily
                    )
                    if (ach.description.isNotBlank()) {
                        Text(
                            text = ach.description,
                            fontSize = 11.5.sp,
                            color = style.textColor,
                            fontFamily = style.fontFamily
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun HeaderContent(cv: Cv, style: TemplateStyle) {
    Column {
        Text(
            text = cv.personalInfo.fullName.ifBlank { "Your Name" },
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = style.primaryColor,
            fontFamily = style.fontFamily
        )
        if (cv.personalInfo.professionalTitle.isNotBlank()) {
            Text(
                text = cv.personalInfo.professionalTitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = style.secondaryColor,
                fontFamily = style.fontFamily
            )
        }
        val contacts = listOfNotNull(
            cv.personalInfo.email.ifBlank { null },
            cv.personalInfo.phone.ifBlank { null },
            cv.personalInfo.location.ifBlank { null },
            cv.personalInfo.website?.ifBlank { null },
            cv.personalInfo.linkedIn?.ifBlank { null }
        )
        if (contacts.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = contacts.joinToString("   •   "),
                fontSize = 10.5.sp,
                color = style.mutedColor,
                fontFamily = style.fontFamily
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String, style: TemplateStyle) {
    Column(modifier = Modifier.padding(top = 6.dp, bottom = 4.dp)) {
        Text(
            text = title.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = style.primaryColor,
            fontFamily = style.fontFamily,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        HorizontalDivider(color = style.dividerColor.copy(alpha = 0.6f), thickness = 0.8.dp)
        Spacer(modifier = Modifier.height(6.dp))
    }
}
