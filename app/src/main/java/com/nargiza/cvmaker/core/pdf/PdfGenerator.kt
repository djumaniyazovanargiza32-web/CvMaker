package com.nargiza.cvmaker.core.pdf

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.nargiza.cvmaker.core.common.AppError
import com.nargiza.cvmaker.core.common.FilenameSanitizer
import com.nargiza.cvmaker.core.model.Cv
import com.nargiza.cvmaker.core.model.TemplateType
import java.io.File
import java.io.FileOutputStream

class PdfGenerator(private val context: Context) {

    // Standard A4 at 72 points per inch
    private val pageWidth = 595
    private val pageHeight = 842
    private val margin = 40f
    private val contentWidth = pageWidth - (margin * 2)

    data class TemplatePalette(
        val primaryColor: Int,
        val secondaryColor: Int,
        val textColor: Int,
        val mutedTextColor: Int,
        val dividerColor: Int,
        val headerBackground: Int? = null,
        val isSerif: Boolean = false
    )

    private fun getPalette(templateType: TemplateType): TemplatePalette {
        return when (templateType) {
            TemplateType.CLASSIC -> TemplatePalette(
                primaryColor = Color.rgb(24, 24, 24),
                secondaryColor = Color.rgb(70, 70, 70),
                textColor = Color.rgb(33, 33, 33),
                mutedTextColor = Color.rgb(100, 100, 100),
                dividerColor = Color.rgb(180, 180, 180),
                isSerif = true
            )
            TemplateType.MODERN -> TemplatePalette(
                primaryColor = Color.rgb(14, 116, 144), // Modern Teal #0E7490
                secondaryColor = Color.rgb(3, 105, 161),
                textColor = Color.rgb(30, 41, 59),
                mutedTextColor = Color.rgb(100, 116, 139),
                dividerColor = Color.rgb(203, 213, 225),
                isSerif = false
            )
            TemplateType.MINIMAL -> TemplatePalette(
                primaryColor = Color.rgb(30, 30, 30),
                secondaryColor = Color.rgb(80, 80, 80),
                textColor = Color.rgb(40, 40, 40),
                mutedTextColor = Color.rgb(120, 120, 120),
                dividerColor = Color.rgb(220, 220, 220),
                isSerif = false
            )
            TemplateType.PROFESSIONAL -> TemplatePalette(
                primaryColor = Color.rgb(30, 58, 138), // Corporate Navy #1E3A8A
                secondaryColor = Color.rgb(37, 99, 235),
                textColor = Color.rgb(17, 24, 39),
                mutedTextColor = Color.rgb(75, 85, 99),
                dividerColor = Color.rgb(209, 213, 219),
                isSerif = false
            )
            TemplateType.EXECUTIVE -> TemplatePalette(
                primaryColor = Color.rgb(136, 19, 55), // Executive Burgundy #881337
                secondaryColor = Color.rgb(159, 18, 57),
                textColor = Color.rgb(24, 24, 27),
                mutedTextColor = Color.rgb(82, 82, 91),
                dividerColor = Color.rgb(212, 212, 216),
                headerBackground = Color.rgb(253, 242, 244),
                isSerif = true
            )
        }
    }

    fun generatePdf(cv: Cv): File {
        val document = PdfDocument()
        val templateType = TemplateType.fromId(cv.templateId)
        val palette = getPalette(templateType)

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        var currentY = margin

        val baseTypeface = if (palette.isSerif) Typeface.SERIF else Typeface.SANS_SERIF

        val titlePaint = Paint().apply {
            color = palette.primaryColor
            textSize = 22f
            typeface = Typeface.create(baseTypeface, Typeface.BOLD)
            isAntiAlias = true
        }

        val subTitlePaint = Paint().apply {
            color = palette.secondaryColor
            textSize = 13f
            typeface = Typeface.create(baseTypeface, Typeface.BOLD)
            isAntiAlias = true
        }

        val sectionHeaderPaint = Paint().apply {
            color = palette.primaryColor
            textSize = 12f
            typeface = Typeface.create(baseTypeface, Typeface.BOLD)
            isAntiAlias = true
        }

        val bodyPaint = Paint().apply {
            color = palette.textColor
            textSize = 9.5f
            typeface = Typeface.create(baseTypeface, Typeface.NORMAL)
            isAntiAlias = true
        }

        val boldBodyPaint = Paint().apply {
            color = palette.textColor
            textSize = 10f
            typeface = Typeface.create(baseTypeface, Typeface.BOLD)
            isAntiAlias = true
        }

        val mutedPaint = Paint().apply {
            color = palette.mutedTextColor
            textSize = 9f
            typeface = Typeface.create(baseTypeface, Typeface.NORMAL)
            isAntiAlias = true
        }

        val dividerPaint = Paint().apply {
            color = palette.dividerColor
            strokeWidth = 1f
            isAntiAlias = true
        }

        fun drawFooter() {
            val footerText = "Page $pageNumber"
            val textWidth = mutedPaint.measureText(footerText)
            canvas.drawText(footerText, pageWidth - margin - textWidth, pageHeight - 20f, mutedPaint)
        }

        fun checkPageBreak(requiredHeight: Float) {
            if (currentY + requiredHeight > pageHeight - margin - 20f) {
                drawFooter()
                document.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas
                currentY = margin
            }
        }

        fun drawWrappedText(text: String, paint: Paint, maxWidth: Float, lineSpacing: Float = 4f) {
            val words = text.split(Regex("\\s+"))
            var line = StringBuilder()

            for (word in words) {
                val testLine = if (line.isEmpty()) word else "$line $word"
                if (paint.measureText(testLine) <= maxWidth) {
                    line = StringBuilder(testLine)
                } else {
                    checkPageBreak(paint.textSize + lineSpacing)
                    canvas.drawText(line.toString(), margin, currentY + paint.textSize, paint)
                    currentY += paint.textSize + lineSpacing
                    line = StringBuilder(word)
                }
            }
            if (line.isNotEmpty()) {
                checkPageBreak(paint.textSize + lineSpacing)
                canvas.drawText(line.toString(), margin, currentY + paint.textSize, paint)
                currentY += paint.textSize + lineSpacing
            }
        }

        fun drawSectionHeader(title: String) {
            checkPageBreak(30f)
            currentY += 8f
            canvas.drawText(title.uppercase(), margin, currentY + 12f, sectionHeaderPaint)
            currentY += 16f
            canvas.drawLine(margin, currentY, pageWidth - margin, currentY, dividerPaint)
            currentY += 10f
        }

        // 1. Draw Header
        val name = cv.personalInfo.fullName.ifBlank { "Professional Resume" }
        canvas.drawText(name, margin, currentY + 20f, titlePaint)
        currentY += 26f

        if (cv.personalInfo.professionalTitle.isNotBlank()) {
            canvas.drawText(cv.personalInfo.professionalTitle, margin, currentY + 13f, subTitlePaint)
            currentY += 18f
        }

        // Contact info line
        val contacts = listOfNotNull(
            cv.personalInfo.email.ifBlank { null },
            cv.personalInfo.phone.ifBlank { null },
            cv.personalInfo.location.ifBlank { null },
            cv.personalInfo.website?.ifBlank { null },
            cv.personalInfo.linkedIn?.ifBlank { null }
        )
        if (contacts.isNotEmpty()) {
            val contactLine = contacts.joinToString("   •   ")
            drawWrappedText(contactLine, mutedPaint, contentWidth, lineSpacing = 3f)
            currentY += 4f
        }
        currentY += 6f
        canvas.drawLine(margin, currentY, pageWidth - margin, currentY, dividerPaint)
        currentY += 10f

        // 2. Professional Summary
        if (cv.professionalSummary.isNotBlank()) {
            drawSectionHeader("Professional Summary")
            drawWrappedText(cv.professionalSummary, bodyPaint, contentWidth, lineSpacing = 4f)
            currentY += 6f
        }

        // 3. Experience
        if (cv.experiences.isNotEmpty()) {
            drawSectionHeader("Work Experience")
            for (exp in cv.experiences) {
                checkPageBreak(40f)
                val posCompany = "${exp.position} — ${exp.company}"
                canvas.drawText(posCompany, margin, currentY + 10f, boldBodyPaint)

                val dateStr = if (exp.isCurrent) "${exp.startDate} - Present" else "${exp.startDate} - ${exp.endDate.orEmpty()}"
                val dateLoc = if (exp.location.isNotBlank()) "$dateStr | ${exp.location}" else dateStr
                val dateWidth = mutedPaint.measureText(dateLoc)
                canvas.drawText(dateLoc, pageWidth - margin - dateWidth, currentY + 10f, mutedPaint)
                currentY += 16f

                if (exp.description.isNotBlank()) {
                    drawWrappedText(exp.description, bodyPaint, contentWidth, lineSpacing = 3f)
                }

                for (ach in exp.achievements) {
                    if (ach.isNotBlank()) {
                        checkPageBreak(16f)
                        val bulletText = "•  $ach"
                        drawWrappedText(bulletText, bodyPaint, contentWidth - 10f, lineSpacing = 3f)
                    }
                }
                currentY += 8f
            }
        }

        // 4. Education
        if (cv.education.isNotEmpty()) {
            drawSectionHeader("Education")
            for (edu in cv.education) {
                checkPageBreak(30f)
                val degreeField = "${edu.degree} in ${edu.fieldOfStudy}"
                canvas.drawText(degreeField, margin, currentY + 10f, boldBodyPaint)

                val eduDate = if (edu.endDate.isNullOrBlank()) edu.startDate else "${edu.startDate} - ${edu.endDate}"
                val dateWidth = mutedPaint.measureText(eduDate)
                canvas.drawText(eduDate, pageWidth - margin - dateWidth, currentY + 10f, mutedPaint)
                currentY += 14f

                if (edu.institution.isNotBlank()) {
                    canvas.drawText(edu.institution, margin, currentY + 9f, mutedPaint)
                    currentY += 14f
                }
                currentY += 4f
            }
        }

        // 5. Skills
        if (cv.skills.isNotEmpty()) {
            drawSectionHeader("Skills")
            val grouped = cv.skills.groupBy { it.category }
            for ((category, skills) in grouped) {
                checkPageBreak(20f)
                val skillNames = skills.joinToString(", ") { "${it.name} (${it.level.displayName})" }
                val fullSkillLine = "$category: $skillNames"
                drawWrappedText(fullSkillLine, bodyPaint, contentWidth, lineSpacing = 3f)
                currentY += 3f
            }
        }

        // 6. Projects
        if (cv.projects.isNotEmpty()) {
            drawSectionHeader("Key Projects")
            for (proj in cv.projects) {
                checkPageBreak(30f)
                canvas.drawText(proj.name, margin, currentY + 10f, boldBodyPaint)
                if (proj.role?.isNotBlank() == true) {
                    val roleWidth = mutedPaint.measureText(proj.role)
                    canvas.drawText(proj.role, pageWidth - margin - roleWidth, currentY + 10f, mutedPaint)
                }
                currentY += 14f

                if (proj.technologies.isNotEmpty()) {
                    val techLine = "Technologies: " + proj.technologies.joinToString(", ")
                    drawWrappedText(techLine, mutedPaint, contentWidth, lineSpacing = 2f)
                }

                if (proj.description.isNotBlank()) {
                    drawWrappedText(proj.description, bodyPaint, contentWidth, lineSpacing = 3f)
                }

                for (ach in proj.achievements) {
                    if (ach.isNotBlank()) {
                        val bulletText = "•  $ach"
                        drawWrappedText(bulletText, bodyPaint, contentWidth - 10f, lineSpacing = 2f)
                    }
                }
                currentY += 6f
            }
        }

        // 7. Certifications
        if (cv.certifications.isNotEmpty()) {
            drawSectionHeader("Certifications")
            for (cert in cv.certifications) {
                checkPageBreak(20f)
                val certTitle = "${cert.name} — ${cert.issuer}"
                canvas.drawText(certTitle, margin, currentY + 10f, boldBodyPaint)
                if (cert.issueDate.isNotBlank()) {
                    val dateWidth = mutedPaint.measureText(cert.issueDate)
                    canvas.drawText(cert.issueDate, pageWidth - margin - dateWidth, currentY + 10f, mutedPaint)
                }
                currentY += 16f
            }
        }

        // 8. Languages & Achievements
        if (cv.languages.isNotEmpty()) {
            drawSectionHeader("Languages")
            val langLine = cv.languages.joinToString("   •   ") { "${it.name} (${it.proficiency})" }
            drawWrappedText(langLine, bodyPaint, contentWidth, lineSpacing = 3f)
            currentY += 6f
        }

        if (cv.achievements.isNotEmpty()) {
            drawSectionHeader("Key Achievements")
            for (ach in cv.achievements) {
                checkPageBreak(25f)
                canvas.drawText(ach.title, margin, currentY + 10f, boldBodyPaint)
                currentY += 14f
                if (ach.description.isNotBlank()) {
                    drawWrappedText(ach.description, bodyPaint, contentWidth, lineSpacing = 3f)
                }
                currentY += 4f
            }
        }

        // Draw last page footer
        drawFooter()
        document.finishPage(page)

        // Write to file
        val outputDir = File(context.cacheDir, "generated_cvs").apply { mkdirs() }
        val filename = FilenameSanitizer.sanitizeToPdfFilename(cv.personalInfo.fullName)
        val file = File(outputDir, filename)

        try {
            FileOutputStream(file).use { out ->
                document.writeTo(out)
            }
        } catch (e: Exception) {
            throw AppError.PdfGenerationError("Failed to write PDF: ${e.message}", e)
        } finally {
            document.close()
        }

        return file
    }
}
