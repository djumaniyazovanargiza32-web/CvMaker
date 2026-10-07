package com.nargiza.cvmaker

import com.nargiza.cvmaker.core.common.CvValidator
import com.nargiza.cvmaker.core.model.PersonalInfo
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CvValidatorTest {

    @Test
    fun `valid personal info returns true`() {
        val info = PersonalInfo(
            fullName = "Alex Johnson",
            professionalTitle = "Senior Android Engineer",
            email = "alex@example.com",
            phone = "+1 (555) 234-5678",
            location = "San Francisco, CA",
            website = "https://alex.dev"
        )
        val result = CvValidator.validatePersonalInfo(info)
        assertTrue(result.isValid)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun `empty full name returns error`() {
        val info = PersonalInfo(
            fullName = "",
            professionalTitle = "Engineer",
            email = "test@example.com"
        )
        val result = CvValidator.validatePersonalInfo(info)
        assertFalse(result.isValid)
        assertTrue(result.errors.containsKey("fullName"))
    }

    @Test
    fun `empty professional title returns error`() {
        val info = PersonalInfo(
            fullName = "Alex Johnson",
            professionalTitle = "",
            email = "test@example.com"
        )
        val result = CvValidator.validatePersonalInfo(info)
        assertFalse(result.isValid)
        assertTrue(result.errors.containsKey("professionalTitle"))
    }

    @Test
    fun `invalid email returns error`() {
        val info = PersonalInfo(
            fullName = "Alex Johnson",
            professionalTitle = "Developer",
            email = "not-an-email"
        )
        val result = CvValidator.validatePersonalInfo(info)
        assertFalse(result.isValid)
        assertTrue(result.errors.containsKey("email"))
    }

    @Test
    fun `invalid phone returns error`() {
        val info = PersonalInfo(
            fullName = "Alex Johnson",
            professionalTitle = "Developer",
            email = "alex@example.com",
            phone = "abc-invalid"
        )
        val result = CvValidator.validatePersonalInfo(info)
        assertFalse(result.isValid)
        assertTrue(result.errors.containsKey("phone"))
    }
}
