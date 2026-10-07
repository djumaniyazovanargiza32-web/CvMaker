package com.nargiza.cvmaker.backend

import com.nargiza.cvmaker.backend.ai.GroqClient
import com.nargiza.cvmaker.backend.config.AppConfig
import com.nargiza.cvmaker.backend.routes.aiRoutes
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import com.nargiza.cvmaker.backend.model.HealthResponseDto

fun main() {
    val logger = LoggerFactory.getLogger("Application")
    val port = AppConfig.port
    logger.info("Starting AI CV Maker Backend Server on port $port...")
    logger.info("Groq Model configured: ${AppConfig.groqModel}")
    logger.info("Groq API Key status: ${if (AppConfig.groqApiKey.isNotBlank()) "CONFIGURED" else "MISSING"}")

    embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Delete)
    }

    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                encodeDefaults = true
            }
        )
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            val logger = LoggerFactory.getLogger("StatusPages")
            logger.error("Unhandled server exception: ${cause.message}", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to (cause.message ?: "Internal Server Error"))
            )
        }
    }

    val groqClient = GroqClient()

    routing {
        get("/") {
            call.respondText("AI CV Maker Backend is online.")
        }
        get("/health") {
            call.respond(
                HealthResponseDto(
                    status = "OK",
                    groqConfigured = AppConfig.groqApiKey.isNotBlank(),
                    model = AppConfig.groqModel
                )
            )
        }
        aiRoutes(groqClient)
    }
}
