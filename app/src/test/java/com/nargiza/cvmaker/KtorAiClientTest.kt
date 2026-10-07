package com.nargiza.cvmaker

import com.nargiza.cvmaker.core.common.AppError
import com.nargiza.cvmaker.core.common.AppLogger
import com.nargiza.cvmaker.core.database.preferences.UserPreferences
import com.nargiza.cvmaker.core.database.preferences.UserPreferencesDataStore
import com.nargiza.cvmaker.core.model.AiSummaryRequest
import com.nargiza.cvmaker.core.network.KtorAiClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertTrue
import org.junit.Test

class KtorAiClientTest {

    private val preferencesDataStore: UserPreferencesDataStore = mockk {
        every { preferencesFlow } returns flowOf(UserPreferences(backendUrl = "http://localhost:8080"))
    }
    private val logger: AppLogger = mockk(relaxed = true)

    private fun createClient(responseStatus: HttpStatusCode, responseContent: String): KtorAiClient {
        val mockEngine = MockEngine { _ ->
            respond(
                content = responseContent,
                status = responseStatus,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
        }
        return KtorAiClient(httpClient, preferencesDataStore, logger)
    }

    @Test
    fun `generateSummary 200 success returns decoded response`() = runTest {
        val client = createClient(
            HttpStatusCode.OK,
            """{"summary":"Accomplished Android Engineer...","highlights":["Mobile","Compose"]}"""
        )
        val result = client.generateSummary(AiSummaryRequest("Android Dev", "5+"))
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.summary?.contains("Accomplished") == true)
    }

    @Test
    fun `generateSummary 400 returns ValidationError`() = runTest {
        val client = createClient(HttpStatusCode.BadRequest, """{"error":"Bad Request"}""")
        val result = client.generateSummary(AiSummaryRequest("", ""))
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
    }

    @Test
    fun `generateSummary 401 returns Unauthorized`() = runTest {
        val client = createClient(HttpStatusCode.Unauthorized, """{"error":"Unauthorized"}""")
        val result = client.generateSummary(AiSummaryRequest("Dev", "3"))
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.Unauthorized)
    }

    @Test
    fun `generateSummary 429 returns RateLimited`() = runTest {
        val client = createClient(HttpStatusCode.TooManyRequests, """{"error":"Too Many Requests"}""")
        val result = client.generateSummary(AiSummaryRequest("Dev", "3"))
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.RateLimited)
    }

    @Test
    fun `generateSummary 500 returns ServerError`() = runTest {
        val client = createClient(HttpStatusCode.InternalServerError, """{"error":"Server Error"}""")
        val result = client.generateSummary(AiSummaryRequest("Dev", "3"))
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ServerError)
    }
}
