package com.nargiza.cvmaker

import com.nargiza.cvmaker.core.common.FilenameSanitizer
import org.junit.Assert.assertEquals
import org.junit.Test

class FilenameSanitizerTest {

    @Test
    fun `standard name sanitizes with underscores`() {
        val result = FilenameSanitizer.sanitizeToPdfFilename("Nargiza Karimova")
        assertEquals("Nargiza_Karimova_CV.pdf", result)
    }

    @Test
    fun `name with invalid filesystem characters sanitizes correctly`() {
        val result = FilenameSanitizer.sanitizeToPdfFilename("Alex/Johnson:Lead*Dev?")
        assertEquals("Alex_Johnson_Lead_Dev_CV.pdf", result)
    }

    @Test
    fun `blank name defaults to Resume_CV pdf`() {
        val result = FilenameSanitizer.sanitizeToPdfFilename("   ")
        assertEquals("Resume_CV.pdf", result)
    }

    @Test
    fun `null name defaults to Resume_CV pdf`() {
        val result = FilenameSanitizer.sanitizeToPdfFilename(null)
        assertEquals("Resume_CV.pdf", result)
    }
}
