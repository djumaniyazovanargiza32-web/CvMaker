package com.nargiza.cvmaker.backend.ai

import com.nargiza.cvmaker.backend.config.AppConfig
import com.nargiza.cvmaker.backend.model.GroqChatRequest
import com.nargiza.cvmaker.backend.model.GroqChatResponse
import com.nargiza.cvmaker.backend.model.GroqMessage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

class GroqClient {

    private val logger = LoggerFactory.getLogger(GroqClient::class.java)

    val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val httpClient = HttpClient(Java) {
        install(ContentNegotiation) {
            json(jsonParser)
        }
    }

    suspend fun completeJson(systemPrompt: String, userPrompt: String): String {
        val apiKey = AppConfig.groqApiKey
        if (apiKey.isBlank()) {
            logger.warn("GROQ_API_KEY is not set. Please supply GROQ_API_KEY in environment or local.properties.")
            throw IllegalStateException("GROQ_API_KEY is not configured on the server")
        }

        val request = GroqChatRequest(
            model = AppConfig.groqModel,
            messages = listOf(
                GroqMessage(role = "system", content = systemPrompt),
                GroqMessage(role = "user", content = userPrompt)
            ),
            temperature = 0.2
        )

        val response: HttpResponse = httpClient.post("https://api.groq.com/openai/v1/chat/completions") {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        if (response.status != HttpStatusCode.OK) {
            val errorBody = response.body<String>()
            logger.error("Groq API returned error status: ${response.status.value}, body: $errorBody")
            if (response.status == HttpStatusCode.TooManyRequests) {
                throw RuntimeException("Rate limit reached on Groq API. Please try again shortly.")
            }
            throw RuntimeException("Groq API error (${response.status.value}): $errorBody")
        }

        val chatResponse = response.body<GroqChatResponse>()
        val content = chatResponse.choices.firstOrNull()?.message?.content
            ?: throw IllegalStateException("Empty completion received from Groq")

        return content
    }
}
