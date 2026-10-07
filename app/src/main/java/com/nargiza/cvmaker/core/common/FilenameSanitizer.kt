package com.nargiza.cvmaker.core.common

object FilenameSanitizer {
    /**
     * Sanitizes full name into a safe file name format: Full_Name_CV.pdf
     * Replaces characters like / \ : * ? " < > | and spaces with underscores,
     * trimming leading and trailing underscores.
     */
    fun sanitizeToPdfFilename(fullName: String?): String {
        val trimmed = fullName?.trim().orEmpty()
        val cleaned = trimmed.replace(Regex("[\\\\/:*?\"<>|\\s]+"), "_").trim('_')
        val baseName = if (cleaned.isBlank()) {
            "Resume"
        } else {
            cleaned
        }
        return "${baseName}_CV.pdf"
    }
}
