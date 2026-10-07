package com.nargiza.cvmaker.core.common

import android.util.Patterns
import com.nargiza.cvmaker.core.model.PersonalInfo

object CvValidator {

    data class ValidationResult(
        val isValid: Boolean,
        val errors: Map<String, String> = emptyMap()
    )

    fun validatePersonalInfo(personalInfo: PersonalInfo): ValidationResult {
        val errors = mutableMapOf<String, String>()

        if (personalInfo.fullName.trim().isEmpty()) {
            errors["fullName"] = "Full name is required"
        }

        if (personalInfo.professionalTitle.trim().isEmpty()) {
            errors["professionalTitle"] = "Professional title is required"
        }

        val email = personalInfo.email.trim()
        if (email.isEmpty()) {
            errors["email"] = "Email address is required"
        } else if (!isValidEmail(email)) {
            errors["email"] = "Please enter a valid email address"
        }

        val phone = personalInfo.phone.trim()
        if (phone.isNotEmpty() && !isValidPhone(phone)) {
            errors["phone"] = "Please enter a valid phone number"
        }

        personalInfo.website?.trim()?.let {
            if (it.isNotEmpty() && !isValidUrl(it)) {
                errors["website"] = "Please enter a valid URL (e.g., https://example.com)"
            }
        }

        personalInfo.linkedIn?.trim()?.let {
            if (it.isNotEmpty() && !isValidUrl(it) && !it.contains("linkedin.com", ignoreCase = true)) {
                errors["linkedIn"] = "Please enter a valid LinkedIn URL or handle"
            }
        }

        personalInfo.github?.trim()?.let {
            if (it.isNotEmpty() && !isValidUrl(it) && !it.contains("github.com", ignoreCase = true)) {
                errors["github"] = "Please enter a valid GitHub URL or handle"
            }
        }

        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }

    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && (Patterns.EMAIL_ADDRESS?.matcher(email)?.matches() ?: email.contains("@"))
    }

    fun isValidPhone(phone: String): Boolean {
        val clean = phone.replace(Regex("[\\s()+-]"), "")
        return clean.length in 7..15 && clean.all { it.isDigit() }
    }

    fun isValidUrl(url: String): Boolean {
        val formatted = if (!url.startsWith("http://") && !url.startsWith("https://")) {
            "https://$url"
        } else {
            url
        }
        return Patterns.WEB_URL?.matcher(formatted)?.matches() ?: formatted.contains(".")
    }
}
