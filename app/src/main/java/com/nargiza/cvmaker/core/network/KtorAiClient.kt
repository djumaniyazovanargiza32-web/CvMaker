package com.nargiza.cvmaker.core.network

import com.nargiza.cvmaker.core.common.AppError
import com.nargiza.cvmaker.core.common.AppLogger
import com.nargiza.cvmaker.core.database.preferences.UserPreferencesDataStore
import com.nargiza.cvmaker.core.model.AiAchievementRequest
import com.nargiza.cvmaker.core.model.AiAchievementResponse
import com.nargiza.cvmaker.core.model.AiCvRequest
import com.nargiza.cvmaker.core.model.AiCvResponse
import com.nargiza.cvmaker.core.model.AiExperienceRequest
import com.nargiza.cvmaker.core.model.AiExperienceResponse
import com.nargiza.cvmaker.core.model.AiJobAnalysisRequest
import com.nargiza.cvmaker.core.model.AiJobAnalysisResponse
import com.nargiza.cvmaker.core.model.AiProjectRequest
import com.nargiza.cvmaker.core.model.AiProjectResponse
import com.nargiza.cvmaker.core.model.AiSummaryRequest
import com.nargiza.cvmaker.core.model.AiSummaryResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerializationException
import java.io.IOException

class KtorAiClient(
    val httpClient: HttpClient,
    private val preferencesDataStore: UserPreferencesDataStore,
    private val logger: AppLogger
) {

    private suspend fun getBaseUrl(): String {
        return preferencesDataStore.preferencesFlow.first().backendUrl.trimEnd('/')
    }

    suspend fun generateSummary(request: AiSummaryRequest): Result<AiSummaryResponse> = safeApiCall {
        val url = "${getBaseUrl()}/api/v1/ai/summary"
        httpClient.post(url) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun improveExperience(request: AiExperienceRequest): Result<AiExperienceResponse> = safeApiCall {
        val url = "${getBaseUrl()}/api/v1/ai/experience"
        httpClient.post(url) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun generateAchievements(request: AiAchievementRequest): Result<AiAchievementResponse> = safeApiCall {
        val url = "${getBaseUrl()}/api/v1/ai/achievements"
        httpClient.post(url) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun improveProject(request: AiProjectRequest): Result<AiProjectResponse> = safeApiCall {
        val url = "${getBaseUrl()}/api/v1/ai/project"
        httpClient.post(url) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun generateFullCv(request: AiCvRequest): Result<AiCvResponse> = safeApiCall {
        val url = "${getBaseUrl()}/api/v1/ai/cv"
        httpClient.post(url) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun analyzeJob(request: AiJobAnalysisRequest): Result<AiJobAnalysisResponse> = safeApiCall {
        val url = "${getBaseUrl()}/api/v1/ai/job-analysis"
        httpClient.post(url) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    private suspend inline fun <reified T> safeApiCall(block: () -> HttpResponse): Result<T> {
        return try {
            val response = block()
            when (response.status) {
                HttpStatusCode.OK, HttpStatusCode.Created -> {
                    Result.success(response.body<T>())
                }
                HttpStatusCode.BadRequest -> {
                    logger.error("KtorAiClient", "HTTP 400 Bad Request")
                    Result.failure(AppError.ValidationError("Invalid data provided to AI service"))
                }
                HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden -> {
                    logger.error("KtorAiClient", "HTTP ${response.status.value} Unauthorized")
                    Result.failure(AppError.Unauthorized())
                }
                HttpStatusCode.TooManyRequests -> {
                    logger.error("KtorAiClient", "HTTP 429 Too Many Requests")
                    Result.failure(AppError.RateLimited())
                }
                HttpStatusCode.InternalServerError, HttpStatusCode.BadGateway, HttpStatusCode.ServiceUnavailable -> {
                    logger.error("KtorAiClient", "HTTP ${response.status.value} Server Error")
                    Result.failure(AppError.ServerError(statusCode = response.status.value))
                }
                else -> {
                    logger.error("KtorAiClient", "Unexpected status code: ${response.status.value}")
                    Result.failure(AppError.UnknownError("Server returned code ${response.status.value}"))
                }
            }
        } catch (e: HttpRequestTimeoutException) {
            logger.error("KtorAiClient", "Request timeout", e)
            Result.failure(AppError.Timeout(cause = e))
        } catch (e: SocketTimeoutException) {
            logger.error("KtorAiClient", "Socket timeout", e)
            Result.failure(AppError.Timeout(cause = e))
        } catch (e: ConnectTimeoutException) {
            logger.error("KtorAiClient", "Connect timeout", e)
            Result.failure(AppError.Timeout(cause = e))
        } catch (e: IOException) {
            logger.error("KtorAiClient", "Network IO failure", e)
            Result.failure(AppError.NetworkError("Cannot reach AI server. Please verify backend is running and internet is active.", e))
        } catch (e: SerializationException) {
            logger.error("KtorAiClient", "JSON Serialization failure", e)
            Result.failure(AppError.UnknownError("The AI response could not be processed. Please try again.", e))
        } catch (e: Throwable) {
            logger.error("KtorAiClient", "Unhandled exception", e)
            Result.failure(AppError.UnknownError(e.message ?: "Unexpected error", e))
        }
    }
}
