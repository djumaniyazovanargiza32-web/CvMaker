package com.nargiza.cvmaker.domain.usecase

import com.nargiza.cvmaker.core.common.DispatchersProvider
import com.nargiza.cvmaker.core.model.Cv
import com.nargiza.cvmaker.core.pdf.PdfExporter
import com.nargiza.cvmaker.core.pdf.PdfGenerator
import kotlinx.coroutines.withContext
import java.io.File

class GeneratePdfUseCase(
    private val pdfGenerator: PdfGenerator,
    private val pdfExporter: PdfExporter,
    private val dispatchers: DispatchersProvider
) {
    suspend fun generate(cv: Cv): Result<File> = withContext(dispatchers.io) {
        runCatching {
            pdfGenerator.generatePdf(cv)
        }
    }

    fun openPdf(file: File) {
        pdfExporter.openPdf(file)
    }

    fun sharePdf(file: File) {
        pdfExporter.sharePdf(file)
    }

    suspend fun saveToDownloads(file: File) = withContext(dispatchers.io) {
        runCatching {
            pdfExporter.saveToDownloads(file)
        }
    }
}
