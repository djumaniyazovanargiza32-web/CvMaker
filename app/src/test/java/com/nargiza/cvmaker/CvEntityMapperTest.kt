package com.nargiza.cvmaker

import com.nargiza.cvmaker.core.database.mapper.CvEntityMapper
import com.nargiza.cvmaker.core.model.Cv
import org.junit.Assert.assertEquals
import org.junit.Test

class CvEntityMapperTest {

    @Test
    fun `mapping domain Cv to entity and back preserves all fields and lists`() {
        val original = Cv.sample()
        val entity = CvEntityMapper.toEntity(original)
        val domain = CvEntityMapper.toDomain(entity)

        assertEquals(original.id, domain.id)
        assertEquals(original.title, domain.title)
        assertEquals(original.personalInfo.fullName, domain.personalInfo.fullName)
        assertEquals(original.personalInfo.email, domain.personalInfo.email)
        assertEquals(original.professionalSummary, domain.professionalSummary)
        assertEquals(original.experiences.size, domain.experiences.size)
        assertEquals(original.education.size, domain.education.size)
        assertEquals(original.skills.size, domain.skills.size)
        assertEquals(original.projects.size, domain.projects.size)
        assertEquals(original.certifications.size, domain.certifications.size)
        assertEquals(original.templateId, domain.templateId)
    }
}
