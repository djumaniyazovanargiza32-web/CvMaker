package com.nargiza.cvmaker.core.common

sealed class AppError(open val userMessage: String, override val cause: Throwable? = null) : Throwable(userMessage, cause) {
    data class NetworkError(
        override val userMessage: String = "Network connection problem. Please check your internet connection.",
        override val cause: Throwable? = null
    ) : AppError(userMessage, cause)

    data class RateLimited(
        override val userMessage: String = "AI is temporarily busy. Please try again shortly.",
        override val cause: Throwable? = null
    ) : AppError(userMessage, cause)

    data class Unauthorized(
        override val userMessage: String = "Unauthorized request. Please check API credentials.",
        override val cause: Throwable? = null
    ) : AppError(userMessage, cause)

    data class ServerError(
        override val userMessage: String = "AI service temporarily unavailable. Please try again later.",
        val statusCode: Int = 500,
        override val cause: Throwable? = null
    ) : AppError(userMessage, cause)

    data class Timeout(
        override val userMessage: String = "The request timed out. Please try again.",
        override val cause: Throwable? = null
    ) : AppError(userMessage, cause)

    data class DatabaseError(
        override val userMessage: String = "Failed to save or load data from the local database.",
        override val cause: Throwable? = null
    ) : AppError(userMessage, cause)

    data class PdfGenerationError(
        override val userMessage: String = "Could not generate PDF document. Please verify available space.",
        override val cause: Throwable? = null
    ) : AppError(userMessage, cause)

    data class ValidationError(
        override val userMessage: String,
        val field: String? = null
    ) : AppError(userMessage)

    data class UnknownError(
        override val userMessage: String = "An unexpected error occurred. Please try again.",
        override val cause: Throwable? = null
    ) : AppError(userMessage, cause)
}
